package test

import java.io.File

import config.MyConfig
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._

object getBlastData extends MyConfig{

  def main(args: Array[String]): Unit = {
    getGenome
    getGene
    getPep
    getBlastId
  }

  def getGenome = {
    new File("I:\\mollusk_mt_database\\fasta\\genome").listFiles().filter(_.getName.endsWith("fasta")).
      foreach{x=>
        val lines = x.readFileToString
        val name = x.getName.split('.').init.mkString(".")
        println(name)
        val row =  lines.split(">").tail.map{y=>
          val l = y.split("\n")
          ">" + l.head.split(" ").head +"-" +name + "\n" + l.tail.mkString
        }.mkString("\n") + "\n"
     FileUtils.writeStringToFile("I:\\mollusk_mt_database\\blastData\\genome/mollusk.fasta".toFile,row,true)
    }
  }

  def getGene = {
    new File("I:\\mollusk_mt_database\\fasta\\gene").listFiles().filter(_.getName.endsWith("gene")).
      foreach{x=>
        val lines = x.readLines
        val name = x.getName.split('.').init.mkString(".")
        val row =  lines.map{y=>
          if(y.startsWith(">")){
            y.split(" ").head + "-" + name
          }else{
            y.trim
          }
        }
        FileUtils.writeLines("I:\\mollusk_mt_database\\blastData\\gene/mollusk.fasta".toFile,row.asJava,true)
      }
  }

  def getCds = {
    new File("I:\\mollusk_mt_database\\fasta\\cds").listFiles().filter(_.getName.endsWith("cds")).
      foreach{x=>
        val lines = x.readLines
        val name = x.getName.split('.').head
        val row =  lines.map{y=>
          if(y.startsWith(">")){
            y.split(" ").head + "-" + name
          }else{
            y.trim
          }
        }
        FileUtils.writeLines("I:\\mollusk_mt_database\\blastData\\cds/mollusk.fasta".toFile,row.asJava,true)
      }
  }

  def getPep = {
    new File("I:\\mollusk_mt_database\\fasta\\pep").listFiles().filter(_.getName.endsWith("pep")).
      foreach{x=>
        val lines = x.readLines
        val name = x.getName.split('.').dropRight(2).mkString(".")
        val row =  lines.map{y=>
          if(y.startsWith(">")){
            y.split(" ").head + "-" + name
          }else{
            y.trim
          }
        }
        FileUtils.writeLines("I:\\mollusk_mt_database\\blastData\\pep/mollusk.fasta".toFile,row.asJava,true)
      }
  }

  def getBlastId = {
    val path = "I:\\mollusk_mt_database\\blastData"
    getID("gene")
   // getID("cds")
    getID("pep")
    getID("genome")

    def getID(types: String) = {
      s"$path/$types/mollusk.fasta".readLines.foreach { x =>
        if (x.startsWith(">")) {
          FileUtils.writeStringToFile(s"I:\\mollusk_mt_database\\blastData/${types}Id.txt".toFile, x.tail+"\n", true)
        }
      }
    }
  }


}
