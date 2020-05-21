package test

import java.io.File

import org.apache.commons.io.FileUtils
import utils.{ExecCommand, Utils}

import scala.collection.JavaConverters._

object getCpData {


  def main(args: Array[String]): Unit = {
    new File("D:\\软体动物线粒体数据库\\demogff").listFiles().foreach{x=>
      val name = x.getName.split('.').head
      val line = FileUtils.readLines(x).asScala
      var mRNA = ""
      val row = line.filter(_.contains("GenBank")).map(_.split("\t")).filter(y=>y(2) == "gene" || y(2) == "CDS" || y(2) == "exon").map{y=>
        val last = if(y(2) == "gene"){
          y.last.split(";").find(_.startsWith("ID")).get.split('.').head + ";"
        }else{
          y.last.split(";").find(_.startsWith("Parent")).get.split('.').head + ";"
        }
        (y(2),last.split('=').last.init,y.init.mkString("\t") + "\t" + last)
      }
      val gene = row.filter(_._1 == "gene").map(_._2)
      val lasts = row.filter(y=> gene.contains(y._2)).map(_._3)
      println(lasts)

      FileUtils.writeLines(new File(s"D:\\软体动物线粒体数据库\\demojb/$name.gff"),lasts.asJava)
    }
  }

  /**
   * GB转GFF，用bp_genbank2gff3，南昌大学服务器可以运行
   */

  def getMtImg = {
    new File("D:\\藻类细胞器\\mtimg").listFiles.foreach { x =>
      val n = x.getName.split("-").head.toInt + 1
      FileUtils.moveFile(x, new File("D:\\藻类细胞器\\mtimg/mt" + n + ".png"))
    }
  }

  /**
   * 在南昌大学服务器运行
   */
  def getOgdrawCmd = {
    new File("D:\\软体动物线粒体数据库/getOgdraw.sh").delete()
    new File("D:\\软体动物线粒体数据库\\demogb").listFiles().foreach { x =>

      val name = x.getName.split('.').head

      val com = "drawgenemap --infile /root/project/tmp/ogdraw/modb/" + x.getName + "  --format jpg --outfile=/root/project/tmp/ogdraw/modb/" + name + " --force_circular;\n"

      FileUtils.writeStringToFile(new File("D:\\软体动物线粒体数据库/getOgdraw.sh"), com, true)
    }
  }


  def getCpGb(path: String) = {
    val n = new File(s"$path/gb").listFiles().map { x =>
      val l = Utils.readLines(x)
      val name = l.find(_.startsWith("ACCESSION")).get.split(" ").last
      // println(name)
      FileUtils.copyFile(x, new File(s"$path/gb3/" + name + ".gb"))
      (name, x.getName)
    }
    println(n.length)

    n.groupBy(_._1).filter(_._2.length > 1).map { x =>
      println(x._1, x._2.mkString("\t"))
    }
  }

  def getFasta(path: String) = {
    new File(s"$path\\fa").listFiles().foreach { x =>
      val l = Utils.readLines(x)
      val name = l.head.drop(1).split('.').head
      val line = Array(">" + name) ++ l.tail
      FileUtils.writeLines(new File(s"$path\\fa3/" + name + ".fasta"), line.toBuffer.asJava)
    }
  }

  def getCpPepAndCds(path: String) = {
    new File(path).listFiles().foreach { x =>
      val command = "perl D:/软体动物线粒体数据库/genbank_parser_v4.0.pl --type all " + x.getAbsolutePath + ""
      val exec = new ExecCommand()
      exec.exect(Array(command), path)
    }
  }

  def getJbGff = {
    new File("D:\\软体动物线粒体数据库\\demogff").listFiles().foreach { x =>
      val line = FileUtils.readFileToString(x)
      val name = x.getName.split('.').head
      val lines = line.split("##FASTA").head.split("\n").filter(!_.startsWith("#"))
      val l = lines.map(_.split("\t")).filter(y => y(2) == "mRNA" || y(2) == "CDS" || y(2) == "exon").map(_.mkString("\t"))
      FileUtils.writeLines(new File(s"D:\\软体动物线粒体数据库\\demojb/$name.gff"), l.toBuffer.asJava)
    }
  }

  def getJbDir = {
    val files = new File("D:\\软体动物线粒体数据库\\demojb").listFiles()
    val f = files.map(_.getName.split('.').head).distinct
    f.foreach { x =>
      new File(s"D:\\软体动物线粒体数据库\\demojb/$x").mkdir()
      val fs = files.filter(_.getName.startsWith(x))
      fs.foreach { y =>
        FileUtils.moveFile(y, new File(s"D:\\软体动物线粒体数据库\\demojb/$x/${y.getName}"))
      }
    }
  }

  def reName = {
    val files = new File("D:\\软体动物线粒体数据库\\demojb").listFiles()
    files.foreach { x =>
      val fs = x.listFiles()
      fs.foreach { y =>
        val name = y.getName.split('.')
        val fname = name.head + "." + name.last
        if (!new File(s"${x.getAbsolutePath}/$fname").exists()) {
          FileUtils.moveFile(y, new File(s"${x.getAbsolutePath}/$fname"))
        }
      }
    }
  }


  def reGetJbDir = {
    val path = "D:\\藻类细胞器\\cpjb/"
    new File(path).listFiles().foreach { x =>

      try {

        val name = x.getName.split('.').head

        FileUtils.moveDirectory(x, new File(path + name))
      } catch {
        case e =>
          println(x.getName)
      }

    }
  }

  def getJbCmd = {

    new File("D:\\软体动物线粒体数据库/getDemoJB.sh").delete()

    new File("D:\\软体动物线粒体数据库\\demojb").listFiles().foreach { x =>

      val n = x.getName
      val sh = s"cd /var/www/jb/my_data/MODB/$n;\n" +
        s"../../../bin/prepare-refseqs.pl -fasta ./*.fasta;\n" +
        s"../../../bin/prepare-refseqs.pl --conf data/trackList.json;\n" +
        s"../../../bin/flatfile-to-json.pl --gff ./*.gff --trackType FeatureTrack --trackLabel Annotation;\n" +
        s"../../../bin/biodb-to-json.pl --conf data/trackList.json ;\n"

      FileUtils.writeStringToFile(new File("D:\\软体动物线粒体数据库/getDemoJB.sh"), sh, true)
    }
  }


}
