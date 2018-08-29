package controllers

import java.io.File
import java.nio.file.Files
import javax.inject.Inject

import dao._
import models.Tables._
import org.apache.commons.io.FileUtils
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import sun.misc.BASE64Encoder
import utils.{ExecCommand, Utils}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.concurrent.ExecutionContext.Implicits.global

class ChloroplastController @Inject()(chloroplastdao : chloroplastDao)  extends Controller{


  def toBlastn = Action {
    Ok(views.html.chloroplast.blastn())
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
    val row = Await.result(chloroplastdao.getBySample(data.db),Duration.Inf)

    val database = Utils.path + "/1/chloroplast/" + row.head.id+  "/genome.fa "

    val command1 = Utils.path + "/tools/ncbi-blast-2.6.0+/bin/blastn -query " + seqFile.getAbsolutePath + " -subject " +
      database + "-outfmt 5 -evalue " + data.evalue + " -max_target_seqs " + data.maxTargetSeqs +
      " -word_size " + data.wordSize + " -out " + outXml.getAbsolutePath
    val command2 = "python " + Utils.path + "/tools/blast2html-82b8c9722996/blast2html.py -i " + outXml.getAbsolutePath + " -o " + outHtml.getAbsolutePath
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


  def getSample = Action.async{
    chloroplastdao.getAllSample.map{x=>
      val sample = x.map(_.sample)
      Ok(Json.toJson(sample))
    }
  }


  def toORDraw = Action{
    Ok(views.html.chloroplast.ogdraw())
  }

  case class imageData(sample:String)

  val imageForm = Form(
    mapping(
      "sample" -> text
    )(imageData.apply)(imageData.unapply)
  )

  def getImage = Action{implicit request=>
    val data = imageForm.bindFromRequest.get
    val row = Await.result(chloroplastdao.getBySample(data.sample),Duration.Inf)
    val encoder = new BASE64Encoder
    val img = encoder.encode(FileUtils.readFileToByteArray(new File(Utils.path +  s"/1/chloroplast/${row.head.id}/genome.jpg")))
    val photo = s"""<img src="data:image/png;base64,${img}" width="100%"/>"""

    Ok(Json.obj("jpg"-> photo , "id" -> row.head.id))
  }

  def downloadJpg(id:String) = Action{
      val path = Utils.path + "/1/chloroplast/" + id
      val file = new File(path + "/genome.jpg")
      val row = Await.result(chloroplastdao.getById(id.toInt),Duration.Inf)
      Ok.sendFile(file).withHeaders(
        CACHE_CONTROL -> "max-age=3600",
        CONTENT_DISPOSITION -> ("attachment; filename=" + row.sample + ".jpg" ),
        CONTENT_TYPE -> "application/x-download"
      )
  }

  def toUploadSample = Action{implicit request=>
    Ok(views.html.chloroplast.uploadSample())
  }

  case class sampleData(sample:String)

  val sampleForm = Form(
    mapping(
      "sample" -> text
    )(sampleData.apply)(sampleData.unapply)
  )

  def checkSample = Action.async{implicit request=>
    val data = sampleForm.bindFromRequest.get
    chloroplastdao.getBySample(data.sample).map{x=>
      val valid = if(x.size>0){"false"}else{"true"}
      Ok(Json.obj("valid" -> valid))
    }
  }


  def uploadSample = Action(parse.multipartFormData){implicit request=>
      val sample = sampleForm.bindFromRequest.get.sample
      val file = request.body.file("file").get
      Await.result(chloroplastdao.insertSample(Seq(ChloroplastRow(0,sample))),Duration.Inf)
      val row = Await.result(chloroplastdao.getBySample(sample),Duration.Inf)
      val path = Utils.path + "/1/chloroplast/" + row.head.id
      new File(path).mkdirs()
      file.ref.moveTo(new File(path,"genome.gb"))

      val command = new ExecCommand
      val command1 = s"python ${Utils.path}/tools/readVirusGenBank.py -i ${path}/genome.gb -o ${path}/genome.fa"
      val command2 = s"drawgenemap --infile ${path}/genome.gb  --format jpg --outfile=${path}/genome"
    command.exec(command1,command2)
    if(command.isSuccess){
      Ok(Json.obj("valid" -> "true"))
    }else{
      Ok(Json.obj("valid" -> "false","message" -> command.getErrStr))
    }
  }

  def getAllSample = Action.async{implicit request=>
    chloroplastdao.getAllSample.map{x=>
      val json = x.map{y=>
        val sample = y.sample
        val operation =
          s"""
             |  <button class="update" onclick="updateTitle(this)" value="${y.sample}" id="${y.id}" title="修改标题"><i class="fa fa-pencil"></i></button>
             |  <button class="update" onclick="openDelete(this)" value="${y.sample}" id="${y.id}" title="删除样品"><i class="fa fa-trash"></i></button>
           """.stripMargin
        Json.obj("samples" -> sample, "download" -> "1" ,"operation" -> operation)
      }
      Ok(Json.toJson(json))
    }
  }

  def deleteById(id:Int) = Action.async{implicit request=>
    chloroplastdao.deleteById(id).map{x=>
      Ok(Json.obj("valid" -> "true"))
    }
  }



  case class updateData(samleid:Int,newsample:String)

  val updateForm = Form(
    mapping(
      "sampleid" -> number,
      "newsample" -> text
    )(updateData.apply)(updateData.unapply)
  )

  def updateSample = Action.async{implicit request=>
    val data = updateForm.bindFromRequest.get
    chloroplastdao.updateSample(data.samleid,data.newsample).map{x=>
      Ok(Json.obj("valid" -> "true"))
    }
  }

}
