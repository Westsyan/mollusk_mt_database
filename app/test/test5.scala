package test

import java.io.File

import org.apache.commons.io.FileUtils
import utils.Utils

import scala.collection.JavaConverters._


object test5 {


  def main(args: Array[String]): Unit = {


    new File("D:\\藻类细胞器\\cpcsv").listFiles().foreach { x =>
      val l = Utils.readLines(x)

      val header = l.head
      val index = header.split(",").indexOf("Sequence Name")

      val i = if(index != -1){
        index
      }else{
        header.split(",").indexOf("Document Name")
      }


      val name =  if(i != -1){
         l(2).split(",")(i)
      }else{
        x.getName.split("_").last.dropRight(4)
      }


      FileUtils.copyFile(x, new File("D:/藻类细胞器\\cpcsv2/" + name +  ".csv"))
    }
  }


  def getOgdrawCmd = {
    new File("D:/藻类细胞器\\cpgb2/").listFiles().foreach { x =>

      val name = x.getName.dropRight(3)

      val com = "drawgenemap --infile /mnt/sdb/platform/tmp/cpgb2/" + x.getName + "  --format jpg --outfile=/mnt/sdb/platform/tmp/cpogdraw/" +name + " --force_circular;\n"

      FileUtils.writeStringToFile(new File("D:/藻类细胞器/getOgdraw.sh"),com,true)
    }
  }

  def getGb = {
    new File("D:\\藻类细胞器\\cpgb").listFiles().foreach { x =>
      val l = Utils.readLines(x)
      val name = l.find(_.startsWith("VERSION")).get.split(" ").last
      println(name)
      FileUtils.copyFile(x, new File("D:/藻类细胞器\\cpgb2/" + name + ".gb"))
    }

  }

  def getFasta = {
    new File("D:\\藻类细胞器\\fasta").listFiles().foreach { x =>
      val l = Utils.readLines(x)
      val name = l.head.drop(1).split(" ").head
      val line = Array(">" + name) ++ l.tail
      FileUtils.writeLines(new File("D:\\藻类细胞器\\cpfa/" + name + ".fasta"), line.toBuffer.asJava)
    }
  }

}
