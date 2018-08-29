package controllers

import java.io.File
import java.nio.file.Files
import javax.inject.Inject

import dao._
import models.Tables.RnaInfoRow
import org.apache.commons.io.FileUtils
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import utils._

import scala.concurrent.Await
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class RnaController @Inject()(rnadao: rnaDao) extends Controller {


  def toRNA = Action {
    Ok(views.html.rna.rnaInfo())
  }


  def getAllInfo = Action.async {
    def href(id: Int, code: Int): String = {
      val a = s"""<a href="/fern/rna/download?id=${id}&&code=${code}">FASTA&nbsp;<i class="fa fa-download"></i></a>"""
      a
    }

    rnadao.selectAll.map { x =>
      val json = x.map { y =>
        val sample = s"""<a target="_blank" href="/fern/rna/moreInfo?id=${y.id}">${y.speciesName}</a>"""
        val biosample = s"""<a target="_blank" href="https://www.ncbi.nlm.nih.gov/biosample/?term=${y.biosampleIdInNcbi}">${y.biosampleIdInNcbi}</a>"""
        val gene = s"""<a href="/fern/rna/download?id=${y.id}&&code=4">GFF&nbsp;<i class="fa fa-download"></i></a>"""
        Json.obj("sampleid" -> y.sampleId, "species_name" -> sample, "chinese_name" -> y.chineseName,
          "order" -> y.order, "sub_order" -> y.subOrder, "family" -> y.family, "sub_family" -> y.subFamily,
          "genus" -> y.genus, "tissue" -> y.tissue, "sampling_location" -> y.samplingLocation,
          "instrument" -> y.instrument, "layout" -> y.layout, "raw_data" -> y.rawData, "q30" -> y.q30,
          "number_of_contigs" -> y.numberOfContigs, "n50" -> y.n50, "busco_completed_rate" -> y.buscoCompletedRate,
          "biosample_id_in_ncbi" -> biosample, "assembled_transcripts" -> href(y.id, 1), "cds" -> href(y.id, 2),
          "pep" -> href(y.id, 3), "gene" -> gene)
      }

      Ok(Json.toJson(json))
    }
  }


  def download(id: String, code: String) = Action {
    val path = Utils.path + "/1/species/" + id
    val row = Await.result(rnadao.selectById(id.toInt), Duration.Inf)
    val file = if (code == "1") {
      new File(path + "/assembly.fasta")
    } else if (code == "2") {
      new File(path + "/cds.fasta")
    } else if (code == "3") {
      new File(path + "/pep.fasta")
    } else {
      new File(path + "/gene.gff")
    }
    Ok.sendFile(file).withHeaders(
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> ("attachment; filename=" + row.sampleId + "." + file.getName),
      CONTENT_TYPE -> "application/x-download"
    )
  }

  def toMoreInfo(id: String) = Action.async {
    rnadao.selectById(id.toInt).map { x =>
      Ok(views.html.rna.moreInfo(x))
    }
  }

  def checkName(name: String) = Action.async {
    val n = name.split("_").mkString(" ")
    rnadao.selectByName(n).map { x =>
      val valid = if (x.size == 0) {
        "false"
      } else {
        "true"
      }
      Ok(Json.obj("valid" -> "true"))
    }
  }

  def toMoreInfoByName(name: String) = Action.async {
    val n = name.split("_").mkString(" ")
    rnadao.selectByName(n).map { x =>
      Ok(views.html.rna.moreInfo(x.head))
    }
  }

  def toBlastn = Action {
    Ok(views.html.rna.blastn())
  }

  def getDatabase = Action.async {
    rnadao.selectAll.map { x =>
      val json = "All" +: x.map(_.speciesName)
      Ok(Json.toJson(json))
    }
  }


  case class blastData(method: String, query: String, db: String, evalue: String, wordSize: String, maxTargetSeqs: String)

  val blastForm = Form(
    mapping(
      "method" -> text,
      "query" -> text,
      "db" -> text,
      "evalue" -> text,
      "wordSize" -> text,
      "maxTargetSeqs" -> text
    )(blastData.apply)(blastData.unapply)
  )

  def geneBlastnRun = Action(parse.multipartFormData) { implicit request =>
    val data = blastForm.bindFromRequest.get
    val tmpDir = Files.createTempDirectory("tmpDir").toString
    val seqFile = new File(tmpDir, "seq.fa")
    data.method match {
      case "text" =>
        FileUtils.writeStringToFile(seqFile, data.query)
      case "file" =>
        val file = request.body.file("file").get
        file.ref.moveTo(seqFile, replace = true)
    }
    val outXml = new File(tmpDir, "out.xml")
    val outHtml = new File(tmpDir, "out.html")
    val execCommand = new ExecCommand

    val database = if (data.db == "All") {
      val rows = Await.result(rnadao.selectAll, Duration.Inf)
      val ids = rows.map(_.id)
      println(tmpDir)
      for (i <- ids) {
        val fasta = FileUtils.readLines(new File(Utils.path + "/1/species/" + i + "/assembly.fasta")).asScala
        FileUtils.writeLines(new File(tmpDir, "all.fasta"), fasta.asJava,true)
      }
      (tmpDir + "/all.fasta ")
    } else {
      val id = Await.result(rnadao.getByName(data.db), Duration.Inf)
      (Utils.path + "/1/species/" + id.head.id + "/assembly.fasta ")
    }

    val command1 = Utils.path + "/tools/ncbi-blast-2.6.0+/bin/blastn -query " + seqFile.getAbsolutePath + " -subject " +
      database + "-outfmt 5 -evalue " + data.evalue + " -max_target_seqs " + data.maxTargetSeqs +
      " -word_size " + data.wordSize + " -out " + outXml.getAbsolutePath
    val command2 = "python " + Utils.path + "/tools/blast2html-82b8c9722996/blast2html.py -i " + outXml.getAbsolutePath + " -o " + outHtml.getAbsolutePath
    println(command1)
    println(command2)
    execCommand.exec(command1, command2)
    if (execCommand.isSuccess) {
      val html = FileUtils.readFileToString(outHtml)
      Utils.deleteDirectory(tmpDir)
      Ok(Json.obj("html" -> (html + "\n" + Utils.scriptHtml)))
    } else {
      Utils.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }
  }

  def downloadBlast(blast: String) = Action {
    println(1)
    val tmpDir = Files.createTempDirectory("tmpDir").toString
    val file = new File(tmpDir, "blast.html")
    FileUtils.writeStringToFile(file, blast)

    Ok.sendFile(file).withHeaders(
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> "attachment; filename=blast.html",
      CONTENT_TYPE -> "application/x-download"
    )
  }

  def toProcess = Action {
    Ok(views.html.rna.process())
  }

  def toUploadRNA = Action { implicit request =>
    Ok(views.html.rna.uploadRNA())
  }

  case class RnaData(sampleid: String, species: String, chinese: String, order: String, sub_order: String, family: String,
                     sub_family: String, genus: String, tissue: String, sampling_location: String, instrument: String,
                     layout: String, raw_data: String, q30: String, contigs: String, n50: String, rate: String, ncbi: String)

  val RnaForm = Form(
    mapping(
      "sampleid" -> text,
      "species" -> text,
      "chinese" -> text,
      "order" -> text,
      "sub_order" -> text,
      "family" -> text,
      "sub_family" -> text,
      "genus" -> text,
      "tissue" -> text,
      "sampling_location" -> text,
      "instrument" -> text,
      "layout" -> text,
      "raw_data" -> text,
      "q30" -> text,
      "contigs" -> text,
      "n50" -> text,
      "rate" -> text,
      "ncbi" -> text
    )(RnaData.apply)(RnaData.unapply)
  )

  def uploadRNA = Action(parse.multipartFormData) { implicit request =>
    var valid = "true"
    var message = ""
    try {
      val data = RnaForm.bindFromRequest.get
      val files = request.body.files
      val row = RnaInfoRow(0, data.sampleid, data.species, data.chinese, data.order, data.sub_order, data.family, data.sub_family,
        data.genus, data.tissue, data.sampling_location, data.instrument, data.layout, data.raw_data, data.q30, data.contigs,
        data.n50, data.rate, data.ncbi, "link")
      Await.result(rnadao.insert(Seq(row)), Duration.Inf)
      val rows = Await.result(rnadao.selectBySample(data.sampleid), Duration.Inf)
      val id = rows.head.id
      val path = Utils.path + "/1/species/" + id
      new File(path).mkdirs()
      files.head.ref.moveTo(new File(path, "assembly.fasta"))
      files(1).ref.moveTo(new File(path, "cds.fasta"))
      files(2).ref.moveTo(new File(path, "pep.fasta"))
      files(3).ref.moveTo(new File(path, "gene.gff"))
    } catch {
      case e: Exception =>
        valid = "false"
        message = e.toString
    }
    Ok(Json.obj("valid" -> valid, "message" -> message))
  }


  case class sampleData(sampleid: String)

  val sampleForm = Form(
    mapping(
      "sampleid" -> text
    )(sampleData.apply)(sampleData.unapply)
  )

  def checkSample = Action.async { implicit request =>
    val data = sampleForm.bindFromRequest.get
    rnadao.selectBySample(data.sampleid).map { x =>
      val valid = if (x.size == 0) {
        "true"
      } else {
        "false"
      }
      Ok(Json.obj("valid" -> valid))
    }
  }

  def checkInfoSample(id: Int) = Action.async { implicit request =>
    val data = sampleForm.bindFromRequest.get
    rnadao.selectBySample(data.sampleid).flatMap { x =>
      rnadao.selectById(id).map { y =>
        val valid = if ((x.size == 1 && y.sampleId == data.sampleid) || (x.size == 0)) {
          "true"
        } else {
          "false"
        }
        Ok(Json.obj("valid" -> valid))
      }
    }
  }

  def toUpdateRNA = Action { implicit request =>
    Ok(views.html.rna.updateRNA())
  }

  def toUpdateInfo(id: String) = Action.async { implicit request =>
    rnadao.selectById(id.toInt).map { x =>
      Ok(views.html.rna.updateInfo(x))
    }

  }

  def updateRNA(id: Int) = Action(parse.multipartFormData) { implicit request =>
    var valid = "true"
    var message = ""
    try {
      val data = RnaForm.bindFromRequest.get
      val files = request.body.files
      val row = RnaInfoRow(id, data.sampleid, data.species, data.chinese, data.order, data.sub_order, data.family, data.sub_family,
        data.genus, data.tissue, data.sampling_location, data.instrument, data.layout, data.raw_data, data.q30, data.contigs,
        data.n50, data.rate, data.ncbi, "link")
      Await.result(rnadao.updateRow(id, row), Duration.Inf)
      val path = Utils.path + "/1/species/" + id
      new File(path).mkdirs()
      if (files.head.ref.file.length() != 0) {
        files.head.ref.moveTo(new File(path, "assembly.fasta"))
      } else if (files(1).ref.file.length() != 0) {
        files(1).ref.moveTo(new File(path, "cds.fasta"))
      } else if (files(2).ref.file.length() != 0) {
        files(2).ref.moveTo(new File(path, "pep.fasta"))
      } else if (files(3).ref.file.length() != 0) {
        files(3).ref.moveTo(new File(path, "gene.gff"))
      } else {

      }
    } catch {
      case e: Exception =>
        valid = "false"
        message = e.toString
    }
    Ok(Json.obj("valid" -> valid, "message" -> message))
  }


  def deleteById(id: String) = Action.async { implicit request =>
    rnadao.deleteById(id.toInt).map { x =>
      val path = Utils.path + "/1/species/" + id
      FileUtils.deleteDirectory(new File(path))
      Ok(Json.obj("valid" -> "true"))
    }
  }


  def getInfo = Action.async {
    def href(id: Int, code: Int): String = {
      val a = s"""<a href="/fern/rna/download?id=${id}&&code=${code}">FASTA&nbsp;<i class="fa fa-download"></i></a>"""
      a
    }

    rnadao.selectAll.map { x =>
      val json = x.map { y =>
        val operation =
          s"""
             |  <a target="_blank" href="/fern/back/rna/toUpdateInfo?id=${y.id}"><button class="update" title="修改内容"><i class="fa fa-pencil"></i></button></a>
             |  <button class="update" onclick="openDelete(this)" value="${y.sampleId}" id="${y.id}" title="删除物种"><i class="fa fa-trash"></i></button>
           """.stripMargin
        val sample = s"""<a target="_blank" href="/fern/rna/moreInfo?id=${y.id}">${y.sampleId}</a>"""
        val biosample = s"""<a target="_blank" href="https://www.ncbi.nlm.nih.gov/biosample/?term=${y.biosampleIdInNcbi}">${y.biosampleIdInNcbi}</a>"""
        val gene = s"""<a href="/fern/rna/download?id=${y.id}&&code=4">GFF&nbsp;<i class="fa fa-download"></i></a>"""
        Json.obj("operation" -> operation, "sampleid" -> sample, "species_name" -> y.speciesName, "chinese_name" -> y.chineseName,
          "order" -> y.order, "sub_order" -> y.subOrder, "family" -> y.family, "sub_family" -> y.subFamily,
          "genus" -> y.genus, "tissue" -> y.tissue, "sampling_location" -> y.samplingLocation,
          "instrument" -> y.instrument, "layout" -> y.layout, "raw_data" -> y.rawData, "q30" -> y.q30,
          "number_of_contigs" -> y.numberOfContigs, "n50" -> y.n50, "busco_completed_rate" -> y.buscoCompletedRate,
          "biosample_id_in_ncbi" -> biosample, "assembled_transcripts" -> href(y.id, 1), "cds" -> href(y.id, 2),
          "pep" -> href(y.id, 3), "gene" -> gene)
      }

      Ok(Json.toJson(json))
    }
  }


}
