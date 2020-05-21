package test

import utils.Utils

object test1 {


  def main(args: Array[String]): Unit = {
    val js = Utils.readLines("D:\\藻类细胞器/world.js")
    js.tail.init.foreach{x=>
      val hc =x.split(",").find(_.contains("hc-key")).get
      println(hc.slice(12,hc.length).init)
    }

  }

}
