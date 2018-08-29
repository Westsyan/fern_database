package dao


import models.Tables._
import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class ppgiDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._

  def insertPPGI(rows : Seq[PpgiRow]) : Future[Unit] = {
    db.run(Ppgi ++= rows).map(_ =>())
  }

  def getFernById(id : Int) : Future[PpgiRow] = {
   db.run(Ppgi.filter(_.id === id).result.head)
  }

  def getAllFern : Future[Seq[PpgiRow]] = {
    db.run(Ppgi.result)
  }

  def getOrderes(classes:String) : Future[Seq[PpgiRow]] = {
    db.run(Ppgi.filter(_.classes === classes).result)
  }

  def getFamilies(classes:String,orderes:String) : Future[Seq[PpgiRow]] = {
    db.run(Ppgi.filter(_.classes === classes).filter(_.orderes === orderes).result)
  }

    def getSubFamilies(classes:String,orderes:String,families:String) : Future[Seq[PpgiRow]] = {
    db.run(Ppgi.filter(_.classes === classes).filter(_.orderes === orderes).filter(_.families === families).result)
  }

  def getGenus(classes:String,orderes:String,families:String,subfamilies:String) : Future[Seq[PpgiRow]] = {
    db.run(Ppgi.filter(_.classes === classes).filter(_.orderes === orderes).filter(_.families === families).filter(_.subfamilies === subfamilies).result)
  }
}
