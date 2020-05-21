package test

import java.io.File

import org.apache.commons.io.FileUtils
import utils.Utils
import scala.collection.JavaConverters._

object test4 {


  def main(args: Array[String]): Unit = {

  }

  def getCpGffAndFasta = {
    val fasta = new File("D:\\藻类细胞器\\cpfa").listFiles()
    new File("D:\\藻类细胞器\\cpgff").listFiles().foreach { x =>
      val line = Utils.readLines(x)
      val gff = line.filter(_.indexOf("GenBank") != -1)



      val name = x.getName.dropRight(7)

      val fa = fasta.find(_.getName.startsWith(name)).get
      val path = "D:\\藻类细胞器\\cpJbrowser/" + name

      new File(path).mkdir()

      FileUtils.writeLines(new File(path + "/" + name + ".gff"), gff.asJava)
      FileUtils.copyFile(fa, new File(path + "/" + name + ".fasta"))

    }
  }




  def getImg = {
    new File("D:\\img").listFiles.foreach { x =>
      val n = x.getName.split("-").head.drop(3).toInt + 1

      FileUtils.moveFile(x, new File("D:/img/cp" + n + ".jpg"))
    }
  }
}
