package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class newsDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._


  def insertNews(row:Seq[NewsRow]) : Future[Unit] = {
    db.run(News ++= row).map(_=>())
  }

  def getAllByUserid(userid: Int) : Future[Seq[NewsRow]] = {
    db.run(News.filter(_.userid === userid).result)
  }

  def getById(id:Int) : Future[NewsRow] = {
    db.run(News.filter(_.id === id).result.head)
  }

  def getAllNews : Future[Seq[NewsRow]] = {
    db.run(News.result)
  }

  def deleteById(id:Int) : Future[Unit] = {
    db.run(News.filter(_.id === id).delete).map(_=>())
  }

  def updateTitle(id:Int,title:String) : Future[Unit] = {
    db.run(News.filter(_.id === id).map(_.title).update(title)).map(_=>())
  }

}
