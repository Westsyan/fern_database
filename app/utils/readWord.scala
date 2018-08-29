package utils

import org.apache.poi.POIXMLDocument
import org.apache.poi.xwpf.extractor.XWPFWordExtractor

class readWord {

  def readWordFromPath(path:String) :String = {
    val opcPackage = POIXMLDocument.openPackage(path)
    val extractor = new XWPFWordExtractor(opcPackage)
    val word = extractor.getText
    word
  }

}
