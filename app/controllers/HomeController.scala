package controllers

import javax.inject._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request=>
    Redirect(routes.HomeController.homeUS())
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

}
