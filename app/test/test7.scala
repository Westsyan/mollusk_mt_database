package test

import java.io.File

import org.apache.commons.io.FileUtils
import utils.Utils

import scala.collection.JavaConverters._

object test7 {

  def main(args: Array[String]): Unit = {

   val line = Utils.readLines(new File("I:\\PODB\\blastData\\cds/plastid.cds"))

   val cp = line.filter(_.endsWith("Chloroplast")).map(_.tail)
   val mt = line.filter(_.endsWith("Mitochondria")).map(_.tail)

    FileUtils.writeLines(new File("I:\\PODB\\blastData\\cpGene.txt"),cp.asJava)
    FileUtils.writeLines(new File("I:\\PODB\\blastData\\mtGene.txt"),mt.asJava)
  }

  def getGemomeId ={
    val line = Utils.readLines(new File("I:\\PODB\\blastData\\genome/plastid.genome"))

    val cp = line.filter(_.endsWith("Chloroplast")).map(_.tail)
    val mt = line.filter(_.endsWith("Mitochondria")).map(_.tail)

    FileUtils.writeLines(new File("I:\\PODB\\blastData\\cpChr.txt"),cp.asJava)
    FileUtils.writeLines(new File("I:\\PODB\\blastData\\mtChr.txt"),mt.asJava)
  }

  def getGeneId = {
    val line = Utils.readLines(new File("I:\\PODB\\blastData\\geneid.txt"))

    val cp = line.filter(_.endsWith("Chloroplast"))
    val mt = line.filter(_.endsWith("Mitochondria"))

    FileUtils.writeLines(new File("I:\\PODB\\blastData\\cp.txt"),cp.asJava)
    FileUtils.writeLines(new File("I:\\PODB\\blastData\\mt.txt"),mt.asJava)
  }

  def getBlastData = {
    new File("I:\\PODB\\fasta\\cp\\pep").listFiles().foreach { x =>
      val line = FileUtils.readFileToString(x).split(">")
      if (line.nonEmpty) {
        val l= line.tail.map { y =>
          val lines =  y.split("\n").map(_.trim)

          val head = lines.head.split("  ").head.split(" ").mkString
          val gb = x.getName.dropRight(4)
          val name =  s">$head-$gb-Chloroplast\n"
          val seq = lines.tail.mkString

          name + seq + "\n"
        }.mkString
        FileUtils.writeStringToFile(new File("I:\\PODB\\blastData/plastid.pep"),l,true)
      }
    }

    new File("I:\\PODB\\fasta\\mt\\pep").listFiles().foreach { x =>
      val line = FileUtils.readFileToString(x).split(">")
      if (line.nonEmpty) {
        val l= line.tail.map { y =>
          val lines =  y.split("\n").map(_.trim)

          val head = lines.head.split("  ").head.split(" ").mkString
          val gb = x.getName.dropRight(4)
          val name =  s">$head-$gb-Mitochondria\n"
          val seq = lines.tail.mkString

          name + seq + "\n"
        }.mkString
        FileUtils.writeStringToFile(new File("I:\\PODB\\blastData/plastid.pep"),l,true)

      }
    }
  }

  def getBlast = {
    val path = "I:\\PODB\\blastData\\cp"
    new File(path).listFiles().filter(_.getName.endsWith("genome")).foreach { x =>
      val lines = Utils.readLines(x).map { y =>
        if (y.startsWith(">")) {
          y + "-Chloroplast"
        } else {
          y
        }
      }
      FileUtils.writeLines(new File("I:\\PODB\\blastData/cp.genome"), lines.asJava, true)
    }

  }
}
