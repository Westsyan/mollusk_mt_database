package controllers

import config.MyConfig
import dao._
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.TableUtils

import scala.concurrent.ExecutionContext

@Singleton
class LocationController @Inject()(cc: ControllerComponents,
                                   genomeDao: GenomeDao
                                  )
                                  (implicit exec: ExecutionContext) extends AbstractController(cc) with MyConfig {

  def mapPage = Action{implicit request=>
    Ok(views.html.english.genome.mapPage("0"))
  }

  def getWorldData = Action.async{implicit request=>
    val mmap = TableUtils.mmap
    val maxmap = TableUtils.maxmap
    genomeDao.getAllInfo.map { genome =>
      val position = genome.flatMap(_.position.replaceAll(";", "、").
        replaceAll(",","、").split("、")).flatMap { x =>
        if (maxmap.getOrElse(x.trim, "") != "") {
          maxmap(x.trim).map(_._3)
        } else {
          if (x.indexOf(":") != -1) {
            val w = mmap.filter(_._2 == x.split(":").last.trim).map(_._3)
            if (w.isEmpty) {
              mmap.filter(_._2 == x.split(":").head.trim).map(_._3)
            } else {
              w
            }
          } else {
            mmap.filter(_._2 == x.trim).map(_._3)
          }
        }
      }.groupBy(x => x).map { x =>
        Json.obj("key" -> x._1, "num" -> x._2.length)
      }
      Ok(Json.toJson(position))
    }
  }

  def getInfoByLocation(location: String) = Action.async {
    val lo = TableUtils.mmap.find(_._3 == location).get._2
    genomeDao.getAllInfo.map { x =>
      val json = x.filter(_.position.indexOf(lo) != -1).map { y =>
        Json.obj("id"-> y.id,"geneid" -> y.geneid,"organism" -> y.organism,"phylum" -> y.phylum,
          "class" -> y.classes, "order" -> y.order, "family" -> y.family, "genus" -> y.genus
        )
      }
      Ok(Json.toJson(json))
    }
  }

}
