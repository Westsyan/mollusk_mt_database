package utils

import java.lang.reflect.Field

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.JsObject

case class PageData(limit: Int, offset: Int, order: String, search: Option[String], sort: Option[String])

object TableUtils {

  var treeData : Seq[JsObject] = Seq()

  var mmap : Seq[(String,String,String)] = Seq()
  var maxmap : Map[String,Seq[(String,String,String)]] = Map()

  var genomeToId : Map[String,Int] =  Map()
  var geneidToId : Map[String,Int] =  Map()

  var searchSeq : Seq[(Int,String)] = Seq()


  /**
    * int:Id,String:Types,String:content
    */
  var mtSearch : Seq[(Int,String,String)] = Seq()
  var cpSearch : Seq[(Int,String,String)] = Seq()

  var mtgb : Seq[String] = Seq()


  val pageForm = Form(
    mapping(
      "limit" -> number,
      "offset" -> number,
      "order" -> text,
      "search" -> optional(text),
      "sort" -> optional(text)
    )(PageData.apply)(PageData.unapply)
  )

  def dealDataByPage[T](x: Seq[T], page: PageData): Seq[T] = {
    val searchX = x.filter { y =>
      page.search match {
        case None => true
        case Some(text) =>
          val array = text.split("\\s+").map(_.toUpperCase).toBuffer
          val texts = y.getClass.getDeclaredFields.toBuffer.map { x: Field =>
            x.setAccessible(true)
            x.get(y).toString
          }.init
          array.forall { z =>
            texts.map(_.toUpperCase.indexOf(z) != -1).reduce(_ || _)
          }
      }
    }

    val sortX = page.sort match {
      case None => searchX
      case Some(y) =>
        val b = searchX.take(1000).forall { tmpData =>
          val method = tmpData.getClass.getDeclaredMethod(y)
          val value = method.invoke(tmpData).toString
          Utils.isDouble(value)
        }
        if (b) {
          searchX.sortBy { z =>
            val method = z.getClass.getDeclaredMethod(y)
            method.invoke(z).toString.toDouble
          }
        } else {
          searchX.sortBy { z =>
            val method = z.getClass.getDeclaredMethod(y)
            method.invoke(z).toString
          }
        }
    }

    val orderX = page.order match {
      case "asc" => sortX
      case "desc" => sortX.reverse
    }

    orderX

  }

}


