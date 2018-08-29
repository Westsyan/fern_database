package controllers

import java.io.File
import javax.inject.Inject

import dao._
import models.Tables._
import org.apache.commons.io.FileUtils
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import utils.Utils

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration


class MapController  @Inject()(mapdao : mapDao) extends Controller {


  def insert = Action{
    val buffer = FileUtils.readLines(new File("F:\\database\\fern/data.txt"),"Unicode").asScala
    var family_ch = ""
    var family_en = ""
    var genus_ch = ""
    var genus_en = ""
    var chinese_name = ""
    val row = buffer.drop(1).map{x=>
      val y = x.split("\t")
      family_ch = if(y(1) != ""){y(1)}else{family_ch}
      family_en = if(y(2) != ""){y(2)}else{family_en}
      genus_ch = if(y(3) != ""){y(3)}else{genus_ch}
      genus_en = if(y(4) != ""){y(4)}else{genus_en}
      chinese_name = if(y(5) != ""){y(5)}else{chinese_name}
      val scientific_name = if(y.size < 7){""}else{y(6)}
      val scientific_abbr = if(y.size < 7){""}else{y(7)}
      val quote = if(y.size < 9 ) {""}else{y(8)}
      val location = if(y.size == 10 ) {y(9).split("：").drop(1).mkString("：")}else{""}
      val z =y.head + "\t" + family_ch + "\t" + family_en + "\t" + genus_ch + "\t" + genus_en + "\t" + chinese_name +
        "\t" + scientific_name + "\t" + scientific_abbr + "\t" + quote + "\t" + location
      GeographicalDataRow(y.head.toInt,family_ch,family_en,genus_ch,genus_en,chinese_name,scientific_name,scientific_abbr,quote,location)
    }

    Await.result(mapdao.insertInfo(row),Duration.Inf)
    Ok(Json.toJson("1"))
  }

  def insertLocation = Action{
    val info = Await.result(mapdao.getAll,Duration.Inf)
    info.map { x =>
      if (x.location != "") {
        val loca = x.location.split("。").head
        val z = loca.split("、").map(_.toCharArray.take(2).mkString)
        val row = z.filter(q=>(Utils.loaction.contains(q))).map { y =>
            LocationRow(0, y, x.id)
        }
        Await.result(mapdao.insertLoaction(row), Duration.Inf)
      }
    }
    Ok(Json.toJson("1"))
  }

  def getByLocation = Action{
    val row =Utils.loaction.map{x=>
      val z = Await.result(mapdao.getByLocation(x),Duration.Inf)
      x + ":" + z.size + "个物种"
    }
    Ok(Json.toJson(row))
  }


  def toMaps = Action{
    Ok(views.html.map.map())
  }

  def getInfoByLocation(location:String) = Action.async{
    mapdao.getByLocation(location.toCharArray.take(2).mkString).flatMap{x=>
      mapdao.getByIds(x.map(_.dataId)).map{y=>
        val id = Await.result(mapdao.getSidByLocation(location),Duration.Inf)

        val row = y.map{z=>
          val link = s"""<a target="_blank" href="/fern/map/toInfoById?id=${z.id}">${z.scientificName}</a>"""
          Json.obj("id" -> z.id , "family_ch" -> z.familyCh, "family_en" -> z.familyEn,"genus_ch" -> z.genusCh ,
            "genus_en" -> z.genusEn, "chinesename" -> z.chinesename , "scientific_name" -> link , "abbr" -> z.scientificAbbr,
            "quote" -> z.quote, "location" -> z.location)
        }

        Await.result(mapdao.updateNumber(id,row.size),Duration.Inf)
        Utils.addSpeciesInfo(id,row.size)

        Ok(Json.toJson(row))
      }
    }
  }


  def getMapData = Action{
    val json = FileUtils.readFileToString(new File(Utils.path,"china.json"))
    Ok(Json.parse(json))
  }

  def toInfoById(id:Int) = Action.async{
    mapdao.getById(id).map{x=>
      Ok(views.html.map.moreInfo(x,id))
    }
  }

  def getLocation(id:Int) = Action.async{
    mapdao.getById(id).map{x=>
      val json = x.location.split("。").head.split("、").map{y=>
        Json.obj("name"->y.split("").take(2).mkString)

      }
      Ok(Json.toJson(json))
    }
  }

  def toSearch = Action{
    Ok(views.html.map.search())
  }

  def getAllFamilyCh = Action.async{
    mapdao.getAll.map{x=>
      val family = x.map(_.familyCh).distinct
      Ok(Json.toJson(family))
    }
  }

  def getAllFamilyEn = Action.async{
    mapdao.getAll.map{x=>
      val family = x.map(_.familyEn).distinct
      Ok(Json.toJson(family))
    }
  }

  def getAllGenusCh = Action.async{
    mapdao.getAll.map{x=>
      val genus = x.map(_.genusCh).distinct
      Ok(Json.toJson(genus))
    }
  }

  def getAllGenusEn = Action.async{
    mapdao.getAll.map{x=>
      val genus = x.map(_.genusEn).distinct
      Ok(Json.toJson(genus))
    }
  }

  def getAllChinese = Action.async{
    mapdao.getAll.map{x=>
      val chinesename = x.map(_.chinesename).distinct
      Ok(Json.toJson(chinesename))
    }
  }

  def getAllAbbr = Action.async{
    mapdao.getAll.map{x=>
      val abbr = x.map(_.scientificAbbr).distinct.diff(Seq(""))
      Ok(Json.toJson(abbr))
    }
  }

  case class positionData(family_ch:Option[String], family_en:Option[String], genus_ch:Option[String],
                          genus_en:Option[String],chinese:Option[String], abbr:Option[String])

  val positionForm = Form(
    mapping(
      "family_ch" -> optional(text),
      "family_en" -> optional(text),
      "genus_ch" -> optional(text),
      "genus_en" -> optional(text),
      "chinese" -> optional(text),
      "abbr" -> optional(text)
    )(positionData.apply)(positionData.unapply)
  )

  def getByPosition = Action.async{implicit request=>
    val data = positionForm.bindFromRequest.get
    mapdao.selectByLocation(data.family_ch,data.family_en,data.genus_ch,data.genus_en,data.chinese,data.abbr).map{x=>
      val row = x.filter(_.location != "").map{z=>
        val link = s"""<a target="_blank" href="/fern/map/toInfoById?id=${z.id}">${z.scientificName}</a>"""
        Json.obj("id" -> z.id , "chinesename" -> z.chinesename , "scientific_name" -> link ,
          "location" -> z.location)
      }
      Ok(Json.toJson(row))
    }
  }
}
