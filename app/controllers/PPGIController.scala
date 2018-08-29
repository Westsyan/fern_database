package controllers

import java.io.File
import javax.inject.Inject

import models.Tables._
import dao._
import org.apache.commons.io.FileUtils
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import sun.misc.BASE64Encoder
import utils.Utils

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global


class PPGIController @Inject()(ppgidao: ppgiDao,photodao: photoDao) extends Controller {


  def toPPGI = Action{implicit request=>
    Ok(views.html.ppgi.ppgi())
    }


  def insertPPGI = Action{
    val buffer = FileUtils.readLines(new File("E:\\蕨类植物数据库平台开发内容\\蕨类植物数据库平台开发内容\\fern.txt"),"Unicode").asScala
    val fern =buffer.drop(1).map{x=>
      val y = x.split("\t")
      val z =if(y.size < 3){
        "null"
      }else{
        if(y.size == 5){
          "null"
        }else{
          x
        }
      }
      z
    }.distinct.toArray.diff(Array("null"))

    var classes = ""
    var orderes = ""
    var families = ""
    var subfamilies = ""
    var genus = ""
    var species = ""

    val rows = fern.map{x=>
      val y = x.split("\t")
      classes = if(y.head != ""){y.head}else{classes}
      orderes = if(y(1) != ""){y(1)}else{orderes}
      families = if(y(2) != ""){y(2)}else{families}
      subfamilies = if(y(3) != ""){y(3)}else{subfamilies}
      genus = if(y(4) != ""){y(4)}else{genus}
      species = if(y(5) != ""){y(5)}else{species}
      PpgiRow(1,classes,orderes,families,subfamilies,genus,species)
    }
    Await.result(ppgidao.insertPPGI(rows),Duration.Inf)
    Ok(Json.toJson("success"))
  }

  def getJson(html:Seq[String]) : Seq[String] = {
    val ele = Seq("","","","","","")
    val size = html.size
    val json = if(size<7){
      val spaceNum = 7-size
      val space = ele.take(spaceNum)
      html ++  space
    }else{
      html
    }
    json
  }

  def getAllClasses = Action.async{implicit request=>
    ppgidao.getAllFern.map{x =>
     val json = x.map(_.classes).distinct.map{y=>
       val headId = x.filter(_.classes == y).head.id
       val html = "<a href='javascript:void(0)' onclick='getOrderes(this)' id='"+headId+"'>" + y + "</a>"
       html
    }
      val size = json.size
      val jsons = getJson(json).map{z=>
        Json.obj("classes" -> z)
      }
      Ok(Json.obj("array" -> jsons,"number" -> size))
    }
  }

  def getOrders(id: String) = Action.async{implicit request=>
    ppgidao.getFernById(id.toInt).flatMap { fern =>
      ppgidao.getOrderes(fern.classes).map { x =>
        val json = x.map(_.orderes).distinct.map { y =>
          val headId = x.filter(_.orderes == y).head.id
          val html = "<a href='javascript:void(0)' onclick='getFamilies(this)' id='" + headId + "'>" + y + "</a>"
          html
        }
        val size = json.size
        val jsons = getJson(json).map{z=>
          Json.obj("orderes" -> z)
        }
        Ok(Json.obj("array" -> jsons, "number" -> size))
      }
    }
  }

  def getFamilies(id: String) = Action.async{implicit request=>
    ppgidao.getFernById(id.toInt).flatMap { fern =>
      ppgidao.getFamilies(fern.classes,fern.orderes).map { x =>
        val json = x.map(_.families).distinct.map { y =>
          val headId = x.filter(_.families == y).head.id
          val html = "<a href='javascript:void(0)' onclick='getSubFamilies(this)' id='" + headId + "'>" + y + "</a>"
          html
        }
        val size = json.size
        val jsons = getJson(json).map{z=>
          Json.obj("families" -> z)
        }
        Ok(Json.obj("array" -> jsons, "number" -> size))
      }
    }
  }

  def getSubFamilies(id: String) = Action.async{implicit request=>
    ppgidao.getFernById(id.toInt).flatMap { fern =>
      ppgidao.getSubFamilies(fern.classes,fern.orderes,fern.families).map { x =>
        val json = x.map(_.subfamilies).distinct.map { y =>
          val headId = x.filter(_.subfamilies == y).head.id
          val html = "<a href='javascript:void(0)' onclick='getGenus(this)' id='" + headId + "'>" + y + "</a>"
          html
        }
        val size = json.size
        val jsons = getJson(json).map{z=>
          Json.obj("subfamilies" -> z)
        }
        Ok(Json.obj("array" -> jsons, "number" -> size))
      }
    }
  }

  def getGenus(id: String) = Action.async{implicit request=>
    ppgidao.getFernById(id.toInt).flatMap { fern =>
      ppgidao.getGenus(fern.classes,fern.orderes,fern.families,fern.subfamilies).map { x =>
        val json = x.map(_.genus).distinct.map { y =>
          val headId = x.filter(_.genus == y).head.id
          val species = x.filter(_.genus == y).map(_.species).head
          val html = "<a target='_blank' href='/fern/ppgi/toInfo?id=1' id='" + headId  + "'>" + y +"(" + species + ")" + "</a>"
          html
        }
        val size = json.size
        val jsons = getJson(json).map{z=>
          Json.obj("genus" -> z)
        }
        Ok(Json.obj("array" -> jsons, "number" -> size))
      }
    }
  }

  def toSearch = Action{
    Ok(views.html.ppgi.search())
  }


  def getAllOrder = Action.async{
    photodao.getAllInfo.map{x=>
     val order = x.map(_.order).distinct
      Ok(Json.toJson(order))
    }
  }

  def getAllFamily = Action.async{
    photodao.getAllInfo.map{x=>
      val family = x.map(_.family).distinct
      Ok(Json.toJson(family))
    }
  }

  def getAllGenus = Action.async{
    photodao.getAllInfo.map{x=>
      val genus = x.map(_.genus).distinct
      Ok(Json.toJson(genus))
    }
  }

  case class photoData(order:Option[String],family:Option[String],genus:Option[String])

  val photoForm = Form(
    mapping(
      "order" -> optional(text),
      "family" -> optional(text),
      "genus" -> optional(text)
    )(photoData.apply)(photoData.unapply)
  )

  def searchPhotoInfo = Action.async{implicit request=>
    val data = photoForm.bindFromRequest.get
    val order = data.order
    val family = data.family
    val genus = data.genus
    photodao.selectByPosition(order,family,genus).map{x=>
      val encoder = new BASE64Encoder
      val img = encoder.encode(FileUtils.readFileToByteArray(new File(Utils.path +  "/images/1_min.jpg")))
      val photo = s"""<a target="_blank" href="/fern/ppgi/openImg?id=1"><img src="data:image/png;base64,${img}" width="200px"/></a>"""
      val json = x.map{y=>
        val name = s"""<a target="_blank" href="/fern/ppgi/toInfo?id=${y.id}">${y.name}</a>"""
        Json.obj("id" -> y.id,"name" -> name,"order" -> y.order,"family" -> y.family,"genus" ->y.genus,
            "chinesename" -> y.chinesename , "author" -> y.author , "place" -> y.place , "photo" -> photo)
      }
      Ok(Json.toJson(json))
    }
  }

  def openImg(id:String) = Action{
    val encoder = new BASE64Encoder
    val img = encoder.encode(FileUtils.readFileToByteArray(new File(Utils.path +  "/images/"+ id + ".jpg")))
    val photo = s"""<img src="data:image/png;base64,${img}"/>"""
    Ok(photo).as(HTML)
  }

  def toInfo(id:String) = Action.async{
    photodao.selectById(id.toInt).map{x=>
      val encoder = new BASE64Encoder
      val img = encoder.encode(FileUtils.readFileToByteArray(new File(Utils.path +  "/images/"+"1"+"_medium.jpg")))
      val photo = "data:image/png;base64," + img
      Ok(views.html.ppgi.info(x,photo))
    }
  }

  def toTree = Action{
    Ok(views.html.ppgi.tree())
  }

  def getJson = Action.async {
    ppgidao.getAllFern.map{x=>

      val genus = x.map{y=>
        val species = x.filter(_.genus == y.genus).map(_.species).head
        val html = "<a onclick='getPicture()' style='color: inherit;'>" + y.genus + "(" + species + ")"+ "</a>"
        (y.classes,y.orderes,y.families,y.subfamilies, Json.obj("text" -> html ,"nodes" -> ""))
      }

      val subFamily = x.map{y=>
        val node = genus.filter(_._1 == y.classes).filter(_._2 == y.orderes).filter(_._3 == y.families).
                    filter(_._4 == y.subfamilies).map(_._5).distinct
        (y.classes,y.orderes,y.families, Json.obj("text" -> y.subfamilies , "tags" -> Array(node.size), "nodes" -> node))
      }

      val family = x.map{y=>
        val node = subFamily.filter(_._1 == y.classes).filter(_._2 == y.orderes).filter(_._3 == y.families).map(_._4).distinct
        (y.classes,y.orderes, Json.obj("text" -> y.families, "tags" -> Array(node.size), "nodes" -> node))
      }

      val order = x.map{y=>
        val node = family.filter(_._1 == y.classes).filter(_._2 == y.orderes).map(_._3).distinct
        (y.classes,Json.obj("text" -> y.orderes, "tags" -> Array(node.size), "nodes" -> node))
      }

      val classes =  x.map(_.classes).distinct

      val nodes = classes.map{y=>
        val node = order.filter(_._1 == y).map(_._2).distinct
        Json.obj("text" -> y , "tags" -> Array(node.size), "nodes" -> node)
      }
      Ok(Json.toJson(nodes))
    }
  }

}
