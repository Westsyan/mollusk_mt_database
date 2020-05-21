package config

import java.util.concurrent.{Executors, TimeUnit}

import play.Logger
import utils.{TableUtils, Utils}

import scala.collection.mutable

object Search {



  var searchMap: mutable.HashMap[String, Seq[Int]] = mutable.HashMap()

  var searchTimeMap: mutable.HashMap[String, Long] = mutable.HashMap()

  def searchConfig = {
    val runnable = new Runnable {
      override def run() = {
        val time = System.currentTimeMillis()
        val clean = Utils.searchTimeMap.filter(x => (time - x._2) / 1000.0 > 3600)
        clean.keys.foreach { x =>
          searchMap.remove(x)
        }
        searchTimeMap = clean
        Logger.info(Utils.date2 + "清理完成")
      }
    }

    val service = Executors.newSingleThreadScheduledExecutor()
    // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
    service.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.HOURS)
  }

  def getSearchMap(input:String):Unit = {
    val keys = input.toLowerCase.trim.split(" ")
    var content = TableUtils.searchSeq
    keys.foreach {x=>
      content = content.filter(_._2.toLowerCase.indexOf(x.toLowerCase) != -1)
    }
    val result = content.map(_._1)
    Search.searchMap.put(input,result)
  }

  def getKeyWord(input: String, key: String) = {
    val regex = ("(?i)" + key.split(" ").mkString("|")).r
    regex.replaceAllIn(input,m=>"<span style='color:red;'>" + m + "</span>")
  }


}
