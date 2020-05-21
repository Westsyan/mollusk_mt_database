package tools

import play.api.libs.json.{JsObject, Json}
import tools.Read._
import utils.Utils

import scala.collection.mutable
import scala.language.implicitConversions

object Blast {
  implicit def getDataByTable(path:String) : GetDataByTable = new GetDataByTable(path)
}

class GetDataByTable(val path:String){
  val xlsx:mutable.Buffer[String] = (path+ "/table.xls").readLine
  //  val xlsx = FileUtils.readLines(new File(Utils.path+ "/table.xls"), "UTF-8").asScala

  val line: mutable.Buffer[Array[String]] = xlsx.tail.map(_.split("\t").map(_.trim))

  val sort1 : mutable.Buffer[String] = (path + "/seq.fa").readLine.filter(_.startsWith(">")).map{x=>
    x.tail.split(" ").head
  }

  def getBarData : mutable.Buffer[JsObject] = {
    val le =  (Utils.path + "/length.txt").readLine

    val heightM = le.map(_.split("\t")).map(x => x.head.trim -> x.tail).toMap

    val blastMap = line.map { xlx =>
      (xlx(5), xlx(11).split("-").last, xlx(11).split("-").head, xlx(13), xlx(14))
    }.groupBy(_._1).map { x =>
      (x._1 , x._2.groupBy(_._2).map { y =>
        (y._1 , y._2.map(z => (z._3, z._4, z._2 + "-" + z._3 + ":" + z._4 + "-" + z._5,z._3 + "-" + z._2)))
      })
    }

    val barMap = line.map(x=> (x(5),x(11))).distinct

    val sort = line.map(x=>(x(5), x(11).split("-").last)).distinct

     sort.map(_._1).distinct.zipWithIndex.flatMap { x =>
      sort.filter(_._1 == x._1).zipWithIndex.map { y =>
        val bar = y._1._2
        val height = heightM(bar)
        val s = height.map(_.toInt).max
        val xx = s.toString.take(2).toInt
        var z = xx + 1
        while (z % 5 != 0 && z % 6 != 0) {
          z += 1
        }
        val index = if (z % 5 == 0) 5 else 6
        val e = z + "0" * (s.toString.length - 2)
        val max = e
        val yaxis = index
        val barIndex = barMap.filter(_._1 == x._1).map(_._2).distinct.zipWithIndex

        val gen = blastMap(x._1)(bar).map { g =>
          Json.obj("name" -> x._1, "chr" -> g._1.drop(3), "height" -> g._2, "title" -> g._3,"hitid" -> ((x._2+1) + "-" + (barIndex.filter(_._1 == g._4).head._2+1)))
        }
        Json.obj("seq" -> (sort1.indexOf(x._1)+1),"bar" -> bar, "height" -> height, "max" -> max, "yaxis" -> yaxis, "gen" -> gen)
      }
    }
  }

  def getSyntenyData : Array[JsObject] = {

    line.map{xlx=>
      (xlx(5),xlx(11),xlx(7),xlx(8),xlx(9),xlx(13),xlx(14))
    }.groupBy(_._1).flatMap{x=>
      val sort2 = line.filter(_(5) == x._1).map(_(11)).distinct
      x._2.groupBy(_._2).map{y=>
        val aName = x._1
        val aMin = 1
        val aMax = y._2.head._3.toInt
        val rectA = y._2.map{z=>
          Array("",z._4,z._5)
        }

        val bName = y._1
        val bRange = y._2.flatMap(z=> Array(z._6.toInt,z._7.toInt))
        val bMin = bRange.min
        val bMax = bRange.max
        val rectB = y._2.map{z=>
          Array("",z._6,z._7)
        }

        val relate = y._2.zipWithIndex.map(z=> Array(z._2+1,z._2+1))

        val data = Json.obj("A" -> Json.obj("name" -> aName, "min" -> aMin, "max" -> aMax, "seqType" -> "Query" ,"rect" -> rectA),
          "B" -> Json.obj("name" -> bName, "min" -> bMin, "max" -> bMax ,"seqType" -> "Subject", "rect" -> rectB))

        val i1 = sort1.indexOf(x._1) + 1
        val i2 = sort2.indexOf(y._1) + 1
        Json.obj("data" -> data, "relate"-> relate,"index" -> (i1 + "-" + i2))
      }
    }.toArray
  }

}
