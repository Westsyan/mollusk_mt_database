package controllers

import java.io.File
import java.nio.file.Files

import config.MyConfig
import dao.GenomeDao
import javax.inject.Inject
import org.apache.commons.io.FileUtils
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.{ExecCommand, TableUtils, Utils}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

class CompareController @Inject()(cc: ControllerComponents,
                                  genomeDao: GenomeDao)
                                 (implicit exec: ExecutionContext) extends AbstractController(cc) with MyConfig {


  def comparePage = Action { implicit request =>
    Ok(views.html.english.compare.comparePage())
  }

  def getLocationSpecies = Action { implicit request =>
    val json = TableUtils.genomeRow.filter(_.locality != "NO Locality Information").map(_.organism)
    Ok(Json.toJson(json))
  }

  case class CompareData(organism1:String,organism2:String)

  val CompareForm = Form(
    mapping(
      "organism1" -> text,
      "organism2" -> text
    )(CompareData.apply)(CompareData.unapply)
  )


  def getResult = Action { implicit request =>
    val data = CompareForm.bindFromRequest().get

    val o1 = TableUtils.genomeRow.find(_.organism == data.organism1).get
    val o2 = TableUtils.genomeRow.find(_.organism == data.organism2).get

    val ot1 = s"${Utils.path}/fasta/genome/${o1.geneid}.fasta".readLines.map{x=>
      if (x.startsWith(">")) {
        x.split(" ").head
      } else {
        x
      }
    }

    val ot2 = s"${Utils.path}/fasta/genome/${o2.geneid}.fasta".readLines.map{x=>
      if (x.startsWith(">")) {
        x.split(" ").head
      } else {
        x
      }
    }

    val tmpDir = Files.createTempDirectory("tmpDir").toString
    val seqFile = new File(tmpDir, "seq.fa")

    FileUtils.writeLines(seqFile, (ot1 ++ ot2).asJava)

    val outFile = tmpDir + "/result.txt"
   // FileUtils.copyFile("I:\\mollusk_mt_database/result.txt".toFile ,outFile.toFile)

    val execCommand = new ExecCommand
    val commandBuffer = "muscle -in " + tmpDir + "/seq.fa" + " -verbose -log  -fasta  -out " + outFile + " -group "
    execCommand.exect(commandBuffer, tmpDir)
    if (execCommand.isSuccess) {
      val result = FileUtils.readFileToString(new File(outFile))
      val r = result.split(">").tail.map{x=>
        val line = x.split("\n")
        ">" + line.head + "\n" + line.tail.mkString
      }.mkString("\n")
      Ok(Json.obj("out" -> r,"loc1" -> o1.locality,"loc2" -> o2.locality))
    } else {
      Utils.deleteDirectory(tmpDir)
      Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr))
    }

  }

}
