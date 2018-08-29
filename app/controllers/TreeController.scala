package controllers

import java.io.File
import java.nio.file.Files

import org.apache.commons.io.FileUtils
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.{ExecCommand, Utils}

import scala.collection.JavaConverters._

class TreeController  extends Controller{


  def toTree = Action{
    Ok(views.html.tree.treeOfLife())
  }

  def plot = Action{
    val x = FileUtils.readFileToString(new File(Utils.path + "/1/fern_tree.newick"))
    Ok(Json.obj("tree" -> x))
  }

  case class SvgData(svgHtml: String)

  val svgForm = Form(
    mapping(
      "svgHtml" -> text
    )(SvgData.apply)(SvgData.unapply)
  )

  def downloadTree = Action { implicit request =>
    val data = svgForm.bindFromRequest().get
    val dataFile = Files.createTempFile("tmp", ".svg").toFile
    val str = data.svgHtml.replaceAll("</svg>", "\n" + Utils.phylotreeCss + "</svg>")
    FileUtils.writeStringToFile(dataFile, str, "UTF-8")
    Ok.sendFile(dataFile, onClose = () => {
      Files.deleteIfExists(dataFile.toPath)
    }).withHeaders(
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> "attachment; filename=tree.svg",
      CONTENT_TYPE -> "application/x-download"
    )
  }

  def toUploadTree = Action{implicit request=>
    Ok(views.html.tree.uploadNewick())
  }

  def addNewick = Action(parse.multipartFormData) { implicit request =>
    var valid = "true"
    var message = ""
    try {
      val file = request.body.file("file").get
      val id = request.session.get("id").get
      val path = Utils.path + "/" + id + "/fern_tree.newick"
      file.ref.moveTo(new File(path), true)
    } catch {
      case e: Exception =>
        valid = "false"
        message = e.getMessage
    }
    Ok(Json.obj("valid" -> valid, "message" -> message))
  }

  def readSize = Action{
    val buffer = FileUtils.readLines(new File(Utils.path,"size.txt")).asScala
    val x = buffer.head
    val y = buffer.last
    Ok(Json.obj("x"->x,"y"->y))
  }

  def toBlastx = Action{
    Ok(views.html.tree.blastn())
  }

  case class blastData(method: String, query: String, evalue: String, wordSize: String, maxTargetSeqs: String)

  val blastForm = Form(
    mapping(
      "method" -> text,
      "query" -> text,
      "evalue" -> text,
      "wordSize" -> text,
      "maxTargetSeqs" -> text
    )(blastData.apply)(blastData.unapply)
  )

  def blastxRun = Action(parse.multipartFormData) { implicit request =>
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

    val command1 = Utils.path + "/tools/ncbi-blast-2.6.0+/bin/blastx -query " + seqFile.getAbsolutePath + " -subject " +
      Utils.path + "/database " + "-outfmt 5 -evalue " + data.evalue + " -max_target_seqs " + data.maxTargetSeqs +
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

}
