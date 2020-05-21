package test

import java.io.File

import org.apache.commons.io.FileUtils
import utils.Utils

import scala.collection.JavaConverters._

object test6 {


  def main(args: Array[String]): Unit = {

    getBlastName
  }

  def getBlastName = {
    new File("I:\\PODB\\blastData\\cp").listFiles().filter(_.getName.endsWith("genome")).foreach { x =>
      val name = x.getName.dropRight(7)
      println("<option value=\"/cp/" + name + ".genome\">" + name + " - Chloroplast</option>")
    }

    new File("I:\\PODB\\blastData\\mt").listFiles().filter(_.getName.endsWith("genome")).foreach { x =>
      val name = x.getName.dropRight(7)
      println("<option value=\"/mt/" + name + ".genome\">" + name + " - Mitochondria</option>")
    }

  }

  def test = {

    val f = new File("D:\\藻类细胞器\\cpJbrowser\\AB002583.1/AB002583.1.gff")
    val line = Utils.readLines(f)

    val row = line.map(_.split("\t")).filter(x => x(2) == "gene").map { x =>

      val last = x.last.split(";").filter(y => y.startsWith("ID"))
      x.init.mkString("\t").replaceAll("gene", "mRNA") + "\t" + last.mkString(";")
    }


    FileUtils.writeLines(new File("D:\\藻类细胞器\\cpJbrowser\\AB002583.1/AB002583.2.gff"), row.asJava)

  }

  def test2 = {

    val f = new File("D:\\藻类细胞器\\cpJbrowser\\AB002583.1/AB002583.1.gff")
    val line = Utils.readLines(f)

    val row = line.filter(x => x.contains("gene") || x.contains("mRNA") || x.contains("tRNA") || x.contains("rRNA") ||
      x.contains("exon") || x.contains("CDS") || x.contains("ncRNA")).map { x =>
      val l = x.split("\t")

      val last = if (l(2).contains("RNA")) {
        l.last.split(";").filter(y => y.startsWith("ID"))
      } else {
        l.last.split(";").filter(y => y.startsWith("ID") || y.startsWith("Parent"))
      }

      (l.init.mkString("\t") + "\t" + last.mkString(";"))
    }

    val r = row.map(_.split("\t")).groupBy(x => (x(3), x(4))).flatMap { x =>
      if (x._2.length > 1) {
        x._2.filter(x => x(2) != "gene").map(_.mkString("\t"))
      } else {
        x._2.map(_.mkString("\t").replace("gene", "mRNA"))
      }
    }.toBuffer

println(456)
    FileUtils.writeLines(new File("D:\\藻类细胞器\\cpJbrowser\\AB002583.1/AB002583.2.gff"), r.asJava)

  }
}
