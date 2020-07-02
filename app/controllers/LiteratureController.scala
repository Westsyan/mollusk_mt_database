package controllers

import config.MyConfig
import dao.GenomeDao
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class LiteratureController@Inject()(cc:ControllerComponents,
                                    genomeDao:GenomeDao)
                                   (implicit exec:ExecutionContext) extends AbstractController(cc) with MyConfig{


  def literaturePage = Action{implicit request=>
    Ok(views.html.english.reference.literaturePage())
  }

  def citationPage = Action{implicit request=>
    Ok(views.html.english.reference.citationPage())
  }

  def getAllLiterature = Action.async { implicit request =>
    genomeDao.getAllInfo.map{x=>
      val json = x.map{y=>
        ((y.id,y.organism),(y.pubmed,y.journal,y.title,y.author))
      }.filter(y=> y._2._1 !="" && y._2._2 != "" && y._2._3 != "" && y._2._4 != "").map{y=>
        val gene = "<a href='/US/mollusk/genome/genomeInfoPage?id=" + y._1._1+
            "'>" + y._1._2 + "</a>"
        Json.obj("pubmed" -> y._2._1, "journal" -> y._2._2,
          "title" -> y._2._3, "authors" -> y._2._4,"organism" -> gene)
      }
      Ok(Json.toJson(json))
    }
  }

  def citationInfo(id:Int) = Action{implicit request=>
    val x = genomeDao.getById(id).toAwait
    Ok(views.html.english.reference.citationInfo(x))
  }

}
