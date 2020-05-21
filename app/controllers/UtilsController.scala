package controllers

import java.io.File
import java.nio.file.Files

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, Headers}
import utils.Utils

import scala.concurrent.ExecutionContext

@Singleton
class UtilsController @Inject()(cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def getImageByPhotoId(name: String) = Action { implicit request =>
    val path = s"${Utils.path}/images/$name"
    val file = new File(path)
    SendImg(file,request.headers)
  }

  def getGemomeImg(id:Int) = Action { implicit request =>
    val path = s"${Utils.path}/images/${id}-1.jpg"
    var file = new File(path)
    if (!file.exists()) {
      file = new File(s"${Utils.path}/images/no_photo.jpg")
    }
    SendImg(file,request.headers)
  }


  def SendImg(file: File,headers:Headers) = {
    val lastModifiedStr = file.lastModified().toString
    val MimeType = "image/jpg"
    val byteArray = Files.readAllBytes(file.toPath)
    val ifModifiedSinceStr = headers.get(IF_MODIFIED_SINCE)
    if (ifModifiedSinceStr.isDefined && ifModifiedSinceStr.get == lastModifiedStr) {
      NotModified
    } else {
      Ok(byteArray).as(MimeType).withHeaders(LAST_MODIFIED -> file.lastModified().toString)
    }
  }


  def getLastzTarfetExample = Action { implicit request =>
    val line = Utils.readFileToString(Utils.path + "/download/cds/1.fasta")
    Ok(Json.toJson(line))
  }

  def downloadLastzTarfetExample = Action { implicit request =>
    Ok.sendFile(new File(Utils.path + "/download/cds/1.fasta")).withHeaders(
      //缓存
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> "attachment; filename=target.fasta",
      CONTENT_TYPE -> "application/x-download"
    )
  }

  def getLastzQueryExample = Action { implicit request =>
    val line = Utils.readFileToString(Utils.path + "/download/cds/2.fasta")
    Ok(Json.toJson(line))
  }

  def downloadLastzQueryExample = Action { implicit request =>
    Ok.sendFile(new File(Utils.path + "/download/cds/2.fasta")).withHeaders(
      //缓存
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> "attachment; filename=query.fasta",
      CONTENT_TYPE -> "application/x-download"
    )
  }






}

