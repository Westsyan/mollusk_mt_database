package controllers

import java.io.File
import java.nio.file.Files

import config.MyConfig
import dao._
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, RangeResult}
import utils.Utils.windowsPath
import utils.{ExecCommand, Utils}

import scala.concurrent.ExecutionContext

@Singleton
class DownloadController @Inject()(cc: ControllerComponents,
                                   genomeDao: GenomeDao)(implicit exec: ExecutionContext) extends AbstractController(cc) with MyConfig {


  def downloadBeforeUS = Action { implicit request =>

    Ok(views.html.english.download.download())
  }


  def getDownloadJson = Action { implicit request =>
    val genome = genomeDao.getAllInfo.toAwait.map { x =>
      Json.obj("id" -> x.id, "geneid" -> x.geneid, "organism" -> x.organism)
    }
    Ok(Json.obj("json" -> genome))
  }


  def downloadExample(example: String) = Action { implicit request =>
    val filename = "\"" + example + "\""
    Ok.sendFile(new File(Utils.path + "/example/" + example)).withHeaders(
      //缓存
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> ("attachment; filename=" + filename),
      CONTENT_TYPE -> "application/x-download"
    )
  }

  //断点续传
  def linkIgvData(path: String) = Action { implicit request =>
    RangeResult.ofFile(new File(Utils.path + "/igvData/" + path), request.headers.get(RANGE), Some("application/octet-stream"))
  }

  def download(file: String) = Action { implicit request =>
    val name = file.split(" ").mkString("_")
    val filename = "\"" + name + "\""
    Ok.sendFile(new File(Utils.path + "/download/" + name)).withHeaders(
      //缓存
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> ("attachment; filename=" + filename),
      CONTENT_TYPE -> "application/x-download"
    )
  }


  def downloadBlastByRange(name: String, range: String, db: String) = Action {
    val fasta = db match {
      case "blastnGenome" => s"${Utils.path}/blastData/genome/mollusk.fasta"
      case "blastn" => s"${Utils.path}/blastData/gene/mollusk.fasta"
      case "blastp" => s"${Utils.path}/blastData/pep/mollusk.fasta"
    }
    val execCommand = new ExecCommand
    range.split("Range").tail.foreach { x =>
      val r = x.trim.split(":").last.split("to").map(_.trim).sortBy(_.toInt)
      val ra = name + ":" + r.mkString("-")
      val command = if (new File(windowsPath).exists()) {
        Utils.path + "/tools/samtools-0.1.19/samtools.exe faidx " + fasta + " " + ra
      } else {
        "samtools faidx " + fasta + " " + ra
      }
      execCommand.exec(command)
    }

    val seq = execCommand.getOutStr
    Ok(seq).withHeaders(
      //缓存
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> ("attachment; filename=" + name + "_aligned.fasta"),
      CONTENT_TYPE -> "application/x-download"
    )

  }

  def downloadBlastByName(name: String, db: String) = Action {
    val fasta = db match {
      case "blastnGenome" => s"${Utils.path}/blastData/genome/mollusk.fasta"
      case "blastn" => s"${Utils.path}/blastData/gene/mollusk.fasta"
      case "blastp" => s"${Utils.path}/blastData/pep/mollusk.fasta"
    }
    val execCommand = new ExecCommand
    val command = if (new File(windowsPath).exists()) {
      Utils.path + "/tools/samtools-0.1.19/samtools.exe faidx " + fasta + " " + name
    } else {
      "samtools faidx " + fasta + " " + name
    }

    val tmpDir = Files.createTempDirectory("tmpDir").toString
    val seqFile = new File(tmpDir + "/seq.fasta")
    execCommand.execo(command, seqFile)

    println(execCommand.getErrStr)
    Ok.sendFile(seqFile).withHeaders(
      //缓存
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> ("attachment; filename=" + name + "_complete.fasta"),
      CONTENT_TYPE -> "application/x-download"
    )

  }

  def downloadSelect(id: String, fa: String) = Action { implicit request =>
    val ids = id.split(",")

    val suffix = if (fa == "gb") ".gb" else ".fasta"
    val csv = ids.map { id =>
      Utils.readFileToString(Utils.path + "/download/" + fa + "/" + id + suffix).trim
    }.mkString("\n")
    val file = if (ids.length == 1) {
      genomeDao.getById(id.toInt).toAwait.geneid
    } else {
      "select"
    }
    Ok(csv).withHeaders(
      //缓存
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> (s"attachment; filename=$file.$fa"),
      CONTENT_TYPE -> "application/x-download"
    )
  }


  def downloadType(name: String, fa: String) = Action { implicit request =>
    val suffix = fa match {
      case "gb" => ".gb"
      case "gene" => ".gene"
      case "genome" => ".fasta"
      case "cds" => ".gb.cds"
      case "pep" => ".gb.pep"
    }
    val file = Utils.path + "/fasta/" + fa + "/" + name + suffix
    val text = Utils.readFileToString(file).trim
    Ok(text).withHeaders(
      //缓存
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> (s"attachment; filename=$name.$fa"),
      CONTENT_TYPE -> "application/x-download"
    )
  }


}
