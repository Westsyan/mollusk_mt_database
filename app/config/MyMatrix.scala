package config

import scala.collection.mutable

trait MyMatrix {

  implicit class myLines(lines:mutable.Buffer[String]) {


    def asHtmlTable:String = {
      val thead = lines.head.split("\t").map{x=>
        "<th>" + x + "</th>"
      }.mkString
      val tbody = lines.tail.map{x=>
        val l = x.split("\t")
        val head = "<th>" + l.head + "</th>"
        val body = l.tail.map(y=> "<td>" + y + "</td>").mkString
        head + body
      }.mkString("</tr><tr>")
      "<thead><tr>" + thead + "</tr></thead>" + "<tbody><tr>" + tbody + "</tr></tbody>"
    }

  }

}
