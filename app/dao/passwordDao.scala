package dao

import javax.inject.Inject

import models.Tables._

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class passwordDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._

  def updatePassword(newpassword:String,id:Int) : Future[Unit] = {
    db.run(Password.filter(_.id === id).map(_.password).update(newpassword)).map(_=>())
  }

  def selectByAccount(account:String) : Future[PasswordRow] = {
    db.run(Password.filter(_.user === account).result.head)
  }

}
