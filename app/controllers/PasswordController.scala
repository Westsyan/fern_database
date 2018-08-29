package controllers

import javax.inject.Inject

import dao._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global

class PasswordController @Inject()(passworddao: passwordDao) extends Controller{


  case class loginData(account:String,password:String)

  val loginForm = Form(
    mapping(
      "account" -> text,
      "password" -> text
    )(loginData.apply)(loginData.unapply)
  )

  def login = Action.async{implicit request=>
    val data = loginForm.bindFromRequest.get
    val account = data.account
    val pwd = data.password
    passworddao.selectByAccount(account).map{x=>
      if(x.password == pwd){
        Redirect(routes.IndexController.adminHome()).withSession(request.session + ("id" -> x.id.toString)+("user" -> x.user))
      }else{
        Redirect(routes.IndexController.loginBefore()).flashing("info" -> "账号密码错误，请重新输入！")
      }
    }
  }


  def updateBefore = Action{implicit request=>
    Ok(views.html.admin.changePassword())
  }

  case class passwordData(newPasswordAgain:String)

  val passwordForm = Form(
    mapping(
      "newPasswordAgain" -> text
    )(passwordData.apply)(passwordData.unapply)
  )

  def updatePassword = Action.async{implicit request=>
    val data = passwordForm.bindFromRequest.get
      val id = request.session.get("id").get.toInt
      passworddao.updatePassword(data.newPasswordAgain,id).map{x=>
        Redirect(routes.IndexController.loginBefore()).withNewSession.flashing("info" -> "请重新登陆")
      }
  }

  def getPassword(password:String) = Action.async{implicit request=>
    val user = request.session.get("user").get
    passworddao.selectByAccount(user).map{x=>
      val valid =if(x.password == password){
        "true"
      }else{
        "false"
      }
      Ok(Json.obj("valid" -> valid))
    }
  }

  def logout = Action{
    Redirect(routes.IndexController.loginBefore()).withNewSession
  }
}
