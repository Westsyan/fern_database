package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global


class mapDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._


  def insertInfo (row : Seq[GeographicalDataRow]) : Future[Unit] = {
    db.run(GeographicalData ++= row).map(_ => ())
  }

  def getAll : Future[Seq[GeographicalDataRow]] = {
    db.run(GeographicalData.result)
  }

  def getByIds(id: Seq[Int]) : Future[Seq[GeographicalDataRow]] = {
    db.run(GeographicalData.filter(_.id.inSetBind(id)).result)
  }

  def getById(id:Int) : Future[GeographicalDataRow] = {
    db.run(GeographicalData.filter(_.id === id).result.head)
  }

  def selectByLocation(f_ch:Option[String],f_en:Option[String],g_ch:Option[String],g_en:Option[String],c:Option[String],
                       a:Option[String]) : Future[Seq[GeographicalDataRow]] = {
    val fc = f_ch.isEmpty
    val fe = f_en.isEmpty
    val gc = g_ch.isEmpty
    val ge = g_en.isEmpty
    val cp = c.isEmpty
    val ap = a.isEmpty
    db.run(GeographicalData.filter(_.familyCh === f_ch.getOrElse("") || fc).filter(_.familyEn === f_en.getOrElse("") || fe).
      filter(_.genusCh === g_ch.getOrElse("") || gc).filter(_.genusEn === g_en.getOrElse("") || ge).
      filter(_.chinesename === c.getOrElse("") || cp).filter(_.scientificAbbr === a.getOrElse("") || ap).result)
  }

  def insertLoaction(row : Seq[LocationRow]) : Future[Unit] = {
    db.run(Location ++= row).map(_ => ())
  }

  def getByLocation(l:String) : Future[Seq[LocationRow]] = {
    db.run(Location.filter(_.location === l).result)
  }

  def getSidByLocation(location:String) : Future[Int] = {
    db.run(SpeciesNum.filter(_.location === location).map(_.id).result.head)
  }

  def updateNumber(id:Int,num:Int) : Future[Unit] = {
    db.run(SpeciesNum.filter(_.id === id).map(_.speciesNum).update(num)).map(_ =>())
  }


}
