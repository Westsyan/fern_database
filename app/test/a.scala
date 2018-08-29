package test

import java.io.File

import org.apache.commons.io.FileUtils

object a {


  def main(args: Array[String]): Unit = {

   FileUtils.writeStringToFile(new File("D:/852.txt"),"abc")
  }
}
