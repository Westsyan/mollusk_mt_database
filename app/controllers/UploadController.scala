package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class UploadController@Inject()(cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc)  {


  def uploadBefore = Action{implicit request=>
    Ok(views.html.english.upload.upload())
  }




}
