package test

import java.io.File

import org.apache.commons.io.FileUtils

import scala.collection.mutable
import scala.collection.JavaConverters._

object getGeneByFastaAndGff {

  def main(args: Array[String]): Unit = {

  val gffs = new File("D:\\软体动物线粒体数据库\\gff").listFiles()
  val fas = new File("D:\\软体动物线粒体数据库\\fasta").listFiles()

    gffs.foreach{x=>
      val name = x.getName.split('.').head
     val n= x.getName.split('.').dropRight(2).mkString(".")
      println(n)
      val fa = fas.find(_.getName.startsWith(name)).head
      getGene(x.getAbsolutePath,fa.getAbsolutePath,s"D:/软体动物线粒体数据库\\gene/$n.gene")

    }

  }


  def getGene(gffPath:String,fastaPath:String,outPath:String) = {


      val gff =FileUtils.readLines(new File(gffPath)).asScala
      val fasta = FileUtils.readLines(new File(fastaPath)).asScala

      val fa = fasta.tail.mkString

      val atcg = Map("A"->"T","C"->"G","N"->"N","T"->"A","G"->"C",
        "R" -> "Y", "Y" -> "R", "M" -> "K", "K" -> "M",
        "S" -> "S", "W" -> "W", "H" -> "D", "D" -> "H", "B" -> "V", "V" -> "B")

      val genes = gff.filter(!_.startsWith("#")).filter(_.contains("GenBank")).map(_.split("\t")).
        filter(x=> x(2) == "gene").map{x=>
        val id = x.last.split(";").head.drop(3)
        val start = x(3).toInt
        val end = x(4).toInt
        val strand = x(6)

        val gene = fa.slice(start,end+1)
        val seq = if(strand == "+"){
          gene
        }else{
          gene.reverse.map{y=>
            atcg(y.toString)
          }.mkString
        }
        (s">$id",seq)
      }.groupBy(_._1).flatMap{y=>
        if(y._2.length == 1){
          y._2.flatMap(z=>mutable.Buffer(z._1.replaceAll(" ","_"),z._2))
        }else{
          y._2.zipWithIndex.flatMap{z=>
            val geneids = z._1._1
            val geneid = geneids.replaceAll(" ","_")+"_" + (z._2+1)
            mutable.Buffer(s"$geneid",z._1._2)
          }
        }

      }.toBuffer


      FileUtils.writeLines(new File(outPath),genes.asJava)

    }

}
