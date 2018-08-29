package dao

import javax.inject.Inject

import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

class photoDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile]{

  import profile.api._

  def getAllInfo : Future[Seq[PhotoInfoRow]] = {
    db.run(PhotoInfo.result)
  }

  def selectByPosition(order:Option[String],family:Option[String],genus:Option[String]) : Future[Seq[PhotoInfoRow]] = {
    val o = order.isEmpty
    val f = family.isEmpty
    val g = genus.isEmpty
    db.run(PhotoInfo.filter(_.order === order.getOrElse("") ||  o).filter(_.family === family.getOrElse("") || f).
      filter(_.genus === genus.getOrElse("") || g).result)
  }

  def selectById(id:Int) : Future[PhotoInfoRow] = {
    db.run(PhotoInfo.filter(_.id === id).result.head)
  }

}
