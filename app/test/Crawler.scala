package test

import java.io.File

import cn.edu.hfut.dmic.webcollector.model.{CrawlDatums, Page}
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._
import scala.collection.mutable

object Crawler {

  var exce = mutable.Buffer[String]()
  var text = mutable.Buffer[String]()

  class Crawler(crawlPath: String, autoParse: Boolean) extends BreadthCrawler(crawlPath, autoParse) {
    override def visit(page: Page, next: CrawlDatums): Unit = {
      try{
        val url = page.url()
        val name = page.selectText("h1", 0)
        text += url + "\t" + name
        if(text.size > 1500){
          FileUtils.writeLines(new File("D:/gbif.txt"), text.asJava,true)
          text = mutable.Buffer[String]()
        }
      }catch {
        case e : Exception =>
          exce += page.url()
          if(exce.size > 1500){
            FileUtils.writeLines(new File("D:/exception.txt"),exce.asJava,true)
            exce = mutable.Buffer[String]()
          }
      }
    }
  }



  def main(args: Array[String]): Unit = {
    new File("D:/gbif.txt").delete()
    new File("D:/exception.txt").delete()
    for(i <- 0 to 599) {
      val code = new Crawler("crawle", false)
      code.setThreads(50)
      for (j <- 1 to 10000) {
        val link = j + i*10000
        code.addSeed("https://www.gbif.org/species/" + link)
      }
      code.start(1)
      println(i)
    }
    FileUtils.writeLines(new File("D:/gbif.txt"), text.asJava,true)
    FileUtils.writeLines(new File("D:/exception.txt"),exce.asJava,true)

    println(text.size)
    println(exce.size)
  }
}

