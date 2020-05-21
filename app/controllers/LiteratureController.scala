package controllers

import dao.GenomeDao
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class LiteratureController@Inject()(cc:ControllerComponents,
                                    genomeDao:GenomeDao)
                                   (implicit exec:ExecutionContext) extends AbstractController(cc){


  def literaturePage = Action{implicit request=>
    Ok(views.html.english.reference.literaturePage())
  }

  def getAllLiterature = Action.async { implicit request =>
    genomeDao.getAllInfo.map{x=>
      val json = x.map{y=>
        ((y.id,y.geneid),(y.pubmed,y.journal,y.title,y.author))
      }.filter(y=> y._2._1 !="" && y._2._2 != "" && y._2._3 != "" && y._2._4 != "").groupBy(_._2).map{y=>
        val gene = y._2.map{z=>
          "<a href='/US/mollusk/genome/genomeInfoPage?id=" + z._1._1+
            "'>" + z._1._2 + "</a>"
        }.mkString(" ")
        Json.obj("pubmed" -> y._1._1, "journal" -> y._1._2,
          "title" -> y._1._3, "authors" -> y._1._4,"gene" -> gene)
      }
      Ok(Json.toJson(json))
    }
  }

}
