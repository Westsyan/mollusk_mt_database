package test

import java.io.File

import utils.Utils

object getWorld {


  def main(args: Array[String]): Unit = {
    val js = new File("D:\\藻类细胞器/world.js")


    val line = Utils.readLines(js).filter(_.indexOf("type") != -1)

    val mmap = line.map { x =>

      val p = x.indexOf("properties")
      val g = x.indexOf("geometry")
      val m = x.slice(p + 14, g - 4)
      val map = m.replaceAll("\"", "").split(",").map { y =>
        val e = y.split(":")
        (e.head.trim, e.last.trim)
      }.toMap
      (map("continent"), map("name"), map("hc-key"))
    }
    val maxmap = mmap.groupBy(_._1)

    val po = "Europe: Britain、Greece、Netherlands 、North America: Florida、South America: Brazil、South-west Asia: Kuwait、South-east Asia: Java、Australia and New Zealand: Tasmania"
    po.replaceAll(" and ","、").split("、").map { x =>
      if (maxmap.getOrElse(x.trim,"") != "") {
        maxmap(x.trim).map(_._3).mkString(",")
      } else {
        if (x.indexOf(":") != -1) {
          mmap.find(_._2 == x.split(":").last.trim).getOrElse{
            mmap.find(_._2 == x.split(":").head.trim).getOrElse("")
          }
        } else {
          mmap.find(_._2 == x.trim).getOrElse("")
        }
      }
    }.foreach(println)


  }


}
