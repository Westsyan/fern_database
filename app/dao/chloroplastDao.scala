package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class chloroplastDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._

  def insertSample(row: Seq[ChloroplastRow]) : Future[Unit] = {
    db.run(Chloroplast ++= row).map(_=>())
  }

  def getBySample(sample:String) : Future[Seq[ChloroplastRow]] = {
    db.run(Chloroplast.filter(_.sample === sample).result)
  }

  def getById(id:Int) : Future[ChloroplastRow] = {
    db.run(Chloroplast.filter(_.id === id).result.head)
  }

  def getAllSample : Future[Seq[ChloroplastRow]] = {
    db.run(Chloroplast.result)
  }

  def deleteById(id:Int) : Future[Unit] ={
    db.run(Chloroplast.filter(_.id === id).delete).map(_=>())
  }

  def updateSample(id:Int,sample:String) : Future[Unit] = {
    db.run(Chloroplast.filter(_.id === id).map(_.sample).update(sample)).map(_=>())
  }
}
