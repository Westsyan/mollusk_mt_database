package controllers

import java.io.File
import java.nio.file.Files

import config.MyConfig
import dao.{GeneDao, GenomeDao}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.{ExecCommand, TableUtils, Utils}

import scala.concurrent.ExecutionContext

@Singleton
class GenomeController@Inject()(cc:ControllerComponents,
                                genomeDao:GenomeDao,
                                geneDao:GeneDao)
                               (implicit exec:ExecutionContext) extends AbstractController(cc) with MyConfig {


  def browserPage = Action{implicit request=>
    Ok(views.html.english.genome.browserPage())
  }

  def getGenomeInfo = Action.async{implicit request=>
    genomeDao.getAllInfo.map{x=>
      val json = x.map{y=>
        Json.obj("id"-> y.id,"geneid" -> y.geneid,"organism" -> y.organism,"phylum" -> y.phylum,
          "class" -> y.classes, "order" -> y.order, "family" -> y.family, "genus" -> y.genus,"length"->y.length,
        "at"->y.at,"pubmed"->y.pubmed,"journal"->y.journal,"title"->y.title,"author"->y.author,"position"->y.position,
        "collected"->y.collected,"locality"->y.locality,"description"->y.description,"citation"->y.citation,
          "link"->y.link)
      }
      Ok(Json.toJson(json))
    }
  }

  def genomeInfoPage(id:Int) = Action.async{implicit request=>
    genomeDao.getById(id).map{x=>
      Ok(views.html.english.genome.genomeInfoPage(x))
    }
  }

  def getAllGeneById(id: Int) = Action.async { implicit request =>
    geneDao.getByGbId(id).map { x =>
      val json = x.map { y =>
        Json.obj("id" -> y.id, "geneid" -> y.geneid, "types" -> y.rna, "start" -> y.start,
          "end" -> y.end, "strand" -> y.strand, "product" -> y.product)
      }
      Ok(Json.toJson(json))
    }
  }

  def getWorldPostion(id: Int) = Action {
    val mmap = TableUtils.mmap
    val maxmap = TableUtils.maxmap
    val position = genomeDao.getById(id).toAwait.position

    val world = position.replaceAll(";", "、").replaceAll(",","、").
      split("、").flatMap { x =>
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
    }.distinct
    val json = world.map { y =>
      Json.obj("key" -> y)
    }
    Ok(Json.toJson(json))
  }

  def genePage(id:Int) = Action.async{implicit request=>
    geneDao.getById(id).flatMap{x=>
      genomeDao.getById(x.gbid).map{y=>
        Ok(views.html.english.genome.genePage(x,y.geneid))
      }
    }
  }

  def getSeqs(gb: String, range: String) = Action { implicit request =>
    val tmp = Files.createTempDirectory("tmpDir")

    try {
      val exec = new ExecCommand()
      val name = gb.split('.').head

      val pepF = new File(s"${Utils.path}/fasta/pep/$name.pep")
      val cdsF = new File(s"${Utils.path}/fasta/cds/$name.cds")
      val geneF = new File(s"${Utils.path}/fasta/gene/$name.gene")

      val geneCmd = Utils.samtools + "faidx " + geneF.getAbsolutePath + " "  + range.split('.').head
      val cdsCmd = Utils.samtools + "faidx " + cdsF.getAbsolutePath  + " " + range.split('.').head
      val pepCmd = Utils.samtools + "faidx " + pepF.getAbsolutePath + " "  + range.split('.').head
      exec.exect(Array(geneCmd,cdsCmd, pepCmd),tmp.toString)
      val out = exec.getOutStr.split(">")
      val gene = out(1).split("\n").tail.mkString.trim
      val cds = out(2).split("\n").tail.mkString.trim
      val pep = out.last.split("\n").tail.mkString.trim
      Ok(Json.obj("gene" -> gene,"cds" -> cds, "pep" -> pep,"valid"->"true"))
    }catch {
      case e:Exception=>Ok(Json.obj("valid"->"false"))
    }finally {
      tmp.toFile.deleteOnExit()
    }
  }

  def genomeInfoPageByGeneid(geneid:String) :Action[AnyContent]= Action{ implicit request=>
    val id =TableUtils.genomeToId(geneid)
    Redirect(routes.GenomeController.genomeInfoPage(id))
  }

  def geneInfoPageByGeneid(geneid:String) :Action[AnyContent]= Action{ implicit request=>
    val id =TableUtils.geneidToId(geneid)
    Redirect(routes.GenomeController.genePage(id))
  }

}
