package controllers

import javax.inject.Inject

import dao._
import models.Tables.MessageRow
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global

class MessageController @Inject()(messagedao: messageDao) extends Controller {


  def toAboard = Action {
    val date = Utils.date
    Ok(views.html.message.messgae())
  }


  case class messageData(name: String, telephone: String, email: String, message: String)

  val messageForm = Form(
    mapping(
      "name" -> text,
      "telephone" -> text,
      "email" -> text,
      "message" -> text
    )(messageData.apply)(messageData.unapply)
  )

  def insertMessage = Action.async { implicit request =>
    val data = messageForm.bindFromRequest.get
    val ip = request.remoteAddress
    println(data.email)
    val date = Utils.date
    val row = MessageRow(0, data.name, data.telephone, data.email, ip, data.message)
    messagedao.insertMessage(Seq(row)).map { x =>
      Ok(Json.toJson("success"))
    }
  }

  def toMessageAdmin = Action { implicit request =>
    Ok(views.html.message.messageAdmin())
  }

  def getMessage = Action.async { implicit request =>

    messagedao.getMessage.map { x =>
      val row = x.map { y =>
        val html =
          s"""
             |  <div class="col-sm-12 message">
             |    <div style="margin-top: 10px;">
             |      <div class="labels"><font style="color: firebrick">name</font> : ${y.name}</div>
             |      <div class="labels"><font style="color: firebrick">telephone</font> : ${y.phone}</div>
             |      <div class="labels"><font style="color: firebrick">e-mail</font> : ${y.mail}</div>
             |      <button class="update" onclick="deleteModel(this)" title="删除留言"  value="请确认是否删除 ${y.name} 的留言" id="${y.id}" style="float: right"><i class="fa fa-trash"></i></button>
             |      <div class="labels"><font style="color: firebrick">date</font> : 2018.08.17</div>
             |      <div class="labels"><font style="color: firebrick">ip</font> : ${y.ip}</div>
             |    </div>
             |    <br>
             |    <br>
             |    <div class="labels-text"><font style="color: firebrick">留言</font> : ${y.message} </div>
             |  </div>
           """.stripMargin
        Json.obj("title" -> html)
      }

      Ok(Json.toJson(row))
    }
  }

  def deleteMessage(id: Int) = Action.async {
    messagedao.deleteById(id).map { x =>
      Ok(Json.obj("valid" -> "true"))
    }
  }


}
