package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global


class messageDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._


  def insertMessage(row : Seq[MessageRow]) : Future[Unit] = {
    db.run(Message ++= row).map(_=>())
  }

  def getMessage : Future[Seq[MessageRow]] = {
    db.run(Message.result)
  }

  def deleteById(id:Int) : Future[Unit] = {
    db.run(Message.filter(_.id === id).delete).map(_=>())
  }



}
