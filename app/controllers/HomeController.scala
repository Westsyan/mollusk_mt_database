package controllers

import dao.GenomeDao
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import utils.TableUtils

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,genomeDao:GenomeDao) extends AbstractController(cc) {


  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request=>
    Ok(views.html.english.home.home())
  }


  def homeUS = Action{ implicit request=>
    Ok(views.html.english.home.home())
  }

  def homeCN = Action{ implicit request=>
    Ok(views.html.chinese.home.home())
  }

  def aboutPage = Action{implicit request=>
    Ok(views.html.english.about.aboutPage())
  }

  def test =Action{implicit request=>
    Ok(views.html.english.genome.test())
  }

  def getClassesSpecies = Action{implicit request=>
    val json =TableUtils.genomeRow.groupBy(_.classes).toArray.sortBy(_._1).map{x=>
      val classes = "<a href='/US/mollusk/genome/browserPage?classes=" + x._1 +
      "' target='_blank'>" + x._1 + "</a>"
      Json.obj("classes" -> classes,"species"->x._2.length)
    }

    val pie = TableUtils.genomeRow.groupBy(_.classes).map{x=>
      if(x._1 == "Bivalvia"){
        Json.obj("name"-> x._1, "y"-> x._2.length, "sliced"-> "true", "selected"->"true")
      }else{
        Json.obj("name"-> x._1, "y"-> x._2.length)
      }
    }


    Ok(Json.obj("table"->json,"pie"->pie))
  }

}
