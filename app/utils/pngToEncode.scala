package utils

import java.io.File

import org.apache.commons.io.FileUtils
import sun.misc.BASE64Encoder

object pngToEncode {

  def pngToBase64(path:File) : Unit ={
    val file = path.listFiles.map(_.getAbsolutePath)
    val files = file.filter(_.split('.').last == "files").head
    val fi = new File(files)
    val html = file.filter(_.split('.').last == "html").head
    var htmlText =FileUtils.readFileToString(new File(html))
    fi.listFiles.foreach{x=>
      //得到src路径
      val src = x.getAbsolutePath.replaceAll("\\\\","/").split("/").takeRight(2).mkString("/")
      if(x.getAbsolutePath.split('.').last == "png"){
        //修改路径，便于split识别
        val srcs = src.split("[{]").mkString("[{]").split("[}]").mkString("[}]")
        val  in = FileUtils.readFileToByteArray(x)
        val encoder = new BASE64Encoder
        val img =encoder.encode(in) //返回Base64编码过的字节数组字符串
        val text = htmlText.split(srcs)
        htmlText = text.head + "data:image/png;base64," + img + text.last
      }
    }
    FileUtils.writeStringToFile(new File(path + "/news.html"),htmlText)
    FileUtils.deleteDirectory(fi)
    new File(html).delete()
  }

}
