package controllers

import config.Search
import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.TableUtils
import utils.TableUtils._

import scala.concurrent.ExecutionContext

@Singleton
class SearchController @Inject()(cc: ControllerComponents)
                                (implicit exec: ExecutionContext) extends AbstractController(cc) {


  def searchBefore(input:String) = Action{implicit request=>
    Ok(views.html.english.search.search(input))
  }

  case class searchData( input: String)

  val searchForm = Form(
    mapping(
      "input" -> text
    )(searchData.apply)(searchData.unapply)
  )


  def searchByDbInput(input:String) = Action { implicit request =>
    val page = pageForm.bindFromRequest.get
    val option = Array( "ID","Organism","Phylum", "Class", "Order", "Family", "Genus","Journal","Title","Author",
      "Geographical position", "Collected", "Locality （GPS）","Description","Citation")

    if(Search.searchMap.getOrElse(input,"") == ""){
      Search.getSearchMap(input)
    }

    Search.searchTimeMap.put(input, System.currentTimeMillis())

    val result =  Search.searchMap(input)

    val total = result.size
    val tmpX = result.slice(page.offset, page.offset + page.limit)

    val keys = input.trim.split(" ").map(_.trim)
    val row = tmpX.map{x=>
      val r = TableUtils.searchSeq.find(_._1 == x).get

      val text = r._2.split("\t")
      val result = keys.map{k=>
        val results= text.zipWithIndex.find(_._1.toLowerCase.indexOf(k.toLowerCase) != -1).get
       Json.obj("option" -> option(results._2),"result" -> Search.getKeyWord(results._1,input))
      }.distinct
      Json.obj("id" -> r._1, "geneid" -> text.head, "species" -> "Mollusk",  "result" -> result)
    }

    Ok(Json.obj("rows" -> row, "total" -> total))
  }



}
