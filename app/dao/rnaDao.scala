package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class rnaDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._


  def selectAll : Future[Seq[RnaInfoRow]] = {
    db.run(RnaInfo.result)
  }

  def selectById(id:Int) : Future[RnaInfoRow] = {
    db.run(RnaInfo.filter(_.id === id).result.head)
  }

  def insert(row:Seq[RnaInfoRow]) : Future[Unit] = {
    db.run(RnaInfo ++= row).map(_ =>())
  }

  def selectBySample(sampleid:String) : Future[Seq[RnaInfoRow]] ={
    db.run(RnaInfo.filter(_.sampleId === sampleid).result)
  }

  def getByName(species:String) : Future[Seq[RnaInfoRow]] ={
    db.run(RnaInfo.filter(_.speciesName === species).result)
  }

  def updateRow(id:Int,row:RnaInfoRow) : Future[Unit] ={
    db.run(RnaInfo.filter(_.id === id).update(row)).map(_=>())
  }

  def deleteById(id:Int) : Future[Unit] = {
    db.run(RnaInfo.filter(_.id === id).delete).map(_=>())
  }

  def selectByName(name:String) : Future[Seq[RnaInfoRow]] = {
    db.run(RnaInfo.filter(_.speciesName === name).result)
  }

}
