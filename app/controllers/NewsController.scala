package controllers

import java.io.File
import java.nio.file.Files
import javax.inject.Inject

import dao._
import models.Tables.NewsRow
import org.apache.commons.io.FileUtils
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class NewsController @Inject()(newsdao: newsDao) extends Controller {


  def addNewsBefore = Action { implicit request =>
    Ok(views.html.news.addNews())
  }

  case class newsData(title: String)

  val newsForm = Form(
    mapping(
      "title" -> text
    )(newsData.apply)(newsData.unapply)
  )

  def addNews = Action(parse.multipartFormData) { implicit request =>
    var valid = "true"
    var message = ""
    try {
      val file = request.body.file("file").get
      val id = request.session.get("id").get
      val data = newsForm.bindFromRequest.get
      val filename = data.title + Utils.random
      val path = Utils.path + "/" + id + "/news/" + filename
      val tmpDir = Files.createTempDirectory("tmpDir").toString
      val tmpTxtFile = new File(tmpDir, "tmp.txt")
      file.ref.moveTo(tmpTxtFile, true)
      mntToHtml.mht2html(tmpTxtFile.getAbsolutePath, path)
      val date = Utils.date
      pngToEncode.pngToBase64(new File(path))
      val row = NewsRow(0, id.toInt, data.title, path + "/news.html", date)
      Await.result(newsdao.insertNews(Seq(row)), Duration.Inf)
    } catch {
      case e: Exception =>
        valid = "false"
        message = e.getMessage
    }
    Ok(Json.obj("valid" -> valid, "message" -> message))
  }

  case class resetData(resetId: String)

  val resetForm = Form(
    mapping(
      "resetId" -> text
    )(resetData.apply)(resetData.unapply)
  )

  def resetNews = Action(parse.multipartFormData) { implicit request =>
    var valid = "true"
    var message = ""
    try {
      val file = request.body.file("file").get
      val data = resetForm.bindFromRequest.get
      val row = Await.result(newsdao.getById(data.resetId.toInt), Duration.Inf)
      val news = row.path
      new File(news).delete()
      val path = news.replaceAll("\\\\", "/").split("/").dropRight(1).mkString("/")
      val tmpDir = Files.createTempDirectory("tmpDir").toString
      val tmpTxtFile = new File(tmpDir, "tmp.txt")
      file.ref.moveTo(tmpTxtFile, true)
      mntToHtml.mht2html(tmpTxtFile.getAbsolutePath, path)
      pngToEncode.pngToBase64(new File(path))
    } catch {
      case e: Exception =>
        valid = "false"
        message = e.getMessage
    }
    Ok(Json.obj("valid" -> valid, "message" -> message))
  }


  def getAllNews = Action.async { implicit request =>
    val id = request.session.get("id").get.toInt
    newsdao.getAllByUserid(id).map { x =>
      val json = x.sortBy(_.id).reverse.map { y =>
        val title = "<a target='_blank' href='/fern/news/newsInfo?id=" + y.id + "'>" + y.title + "</a>"
        val createdate = y.createdate
        val operation =
          s"""
             |  <button class="update" onclick="updateTitle(this)" value="${y.title}" id="${y.id}" title="修改标题"><i class="fa fa-pencil"></i></button>
             |  <button class="update" onclick="restart(this)" value="${y.id}" title="重新上传"><i class="fa fa-repeat"></i></button>
             |  <button class="update" onclick="openDelete(this)" value="${y.title}" id="${y.id}" title="删除新闻"><i class="fa fa-trash"></i></button>
           """.stripMargin
        val time = createdate.toLocalDate + " " + createdate.toLocalTime.toString.split('.').head
        Json.obj("title" -> title, "createdate" -> time, "operation" -> operation)
      }
      Ok(Json.toJson(json))
    }
  }

  def getAllNewsTitle = Action.async { implicit request =>
    newsdao.getAllNews.map { x =>
      val json = x.sortBy(_.id).reverse.map { y =>
        "<a target='_blank' href='/fern/news/newsInfo?id=" + y.id + "'><i class='fa fa-angle-right' style='margin-right:2em;'></i>" + y.title + "</a>"
      }
      val jsons = getJson(json).map { y =>
        Json.obj("title" -> y)
      }
      Ok(Json.toJson(jsons))
    }

  }

  def getJson(html: Seq[String]): Seq[String] = {
    val ele = Seq("", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
    val size = html.size
    val json = if (size % 15 != 0) {
      val spaceNum = 15 - (size % 15)
      val space = ele.take(spaceNum)
      html ++ space
    } else {
      html
    }
    json
  }


  def openNews(id: Int) = Action { implicit request =>
    val row = Await.result(newsdao.getById(id), Duration.Inf)
    val html = FileUtils.readFileToString(new File(row.path))
    val time = row.createdate.toLocalDate + " " + row.createdate.toLocalTime.toString.split('.').head
    Ok(Json.obj("array" -> html, "title" -> row.title, "createdate" -> time))
  }

  def newsInfo(id: Int) = Action {
    Ok(views.html.news.newsText(id))
  }


  def deleteById(id: Int) = Action.async { implicit request =>
    newsdao.getById(id).flatMap { y =>
      newsdao.deleteById(id).map { x =>
        val path = y.path.replaceAll("\\\\", "/").split("/").dropRight(1).mkString("/")
        FileUtils.deleteDirectory(new File(path))
        Ok(Json.toJson(Json.obj("valid" -> "true")))
      }
    }
  }

  case class newTitleData(titleid: String, newtitle: String)

  val newTitleForm = Form(
    mapping(
      "titleid" -> text,
      "newtitle" -> text
    )(newTitleData.apply)(newTitleData.unapply)
  )

  def updateTitle = Action.async { implicit request =>
    val data = newTitleForm.bindFromRequest.get
    val id = data.titleid.toInt
    newsdao.updateTitle(id, data.newtitle).map { x =>
      Ok(Json.toJson("success"))
    }
  }

  def getById(id: String) = Action.async { implicit request =>
    newsdao.getById(id.toInt).map { x =>
      Ok(Json.obj("title" -> x.title, "id" -> x.id))
    }
  }

}
