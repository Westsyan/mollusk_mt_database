package test

import utils.Utils

object GETgc {

  def main(args: Array[String]): Unit = {
    val f = Utils.readLines("D:\\藻类细胞器\\cpfa/AB002583.1.fasta")

    val seq = f.tail.mkString("").split("").map(_.toLowerCase())

    val c = seq.count(_ == "c")
    val g = seq.count(_ == "g")

    val per = (c.toDouble+g.toDouble)*100/seq.length.toDouble

    println(per)
    println(per.formatted("%.2f"))
  }

}
