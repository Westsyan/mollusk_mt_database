package tools

import java.io.File

import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.language.implicitConversions


object Read {
  implicit val encoding:String = "UTF-8"
  implicit def readPath(path:String) : ReadPath = new ReadPath(path)
  implicit def readFile(file:File) : ReadFile = new ReadFile(file)

}

class ReadPath(val path:String)(implicit encoding: String) {
  def readLine:mutable.Buffer[String] = {
    FileUtils.readLines(new File(path),encoding).asScala
  }

  def readAll:String={
    FileUtils.readFileToString(new File(path),encoding)
  }
}

class ReadFile(val file:File)(implicit encoding: String){
  def readLine:mutable.Buffer[String] = {
    FileUtils.readLines(file,encoding).asScala
  }

  def readAll:String = {
    FileUtils.readFileToString(file,encoding)
  }
}





