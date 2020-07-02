package controllers

import java.io.File
import java.nio.file.Files

import config.MyConfig
import dao.{GeneDao, GenomeDao}
import javax.inject.{Inject, Singleton}
import org.apache.commons.io.FileUtils
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.TableUtils.pageForm
import utils.{ExecCommand, TableUtils, Utils}

import scala.concurrent.ExecutionContext

@Singleton
class GenomeController @Inject()(cc: ControllerComponents,
                                 genomeDao: GenomeDao,
                                 geneDao: GeneDao)
                                (implicit exec: ExecutionContext) extends AbstractController(cc) with MyConfig {


  def browserPage(classes: String) = Action { implicit request =>
    Ok(views.html.english.genome.browserPage(classes))
  }

  def getSataNums(classes:String) = Action{implicit request=>
    val genomeRow = classes match{
      case ""=> TableUtils.genomeRow
      case _ => TableUtils.genomeRow.filter(_.classes == classes)
    }
    val phy = "Phylum(" + genomeRow.map(_.phylum).distinct.length + ")"
    val cla = "Class(" + genomeRow.map(_.classes).distinct.length + ")"
    val order = "Order(" + genomeRow.map(_.order).distinct.length + ")"
    val fa = "Family(" + genomeRow.map(_.family).distinct.length +")"
    val genus = "Genus(" + genomeRow.map(_.genus).distinct.length + ")"
    Ok(Json.obj("phylum" -> phy,"classes"->cla,"order" ->order,"family" ->fa,"genus" -> genus))
  }

  def getGenomeInfo(classes: String) = Action { implicit request =>
    val page = pageForm.bindFromRequest.get


    val x = if (classes == "") {
      genomeDao.getAllInfo.toAwait
    } else {
      genomeDao.getByClasses(classes).toAwait
    }
    val orderX = TableUtils.dealDataByPage(x, page)
    val total = orderX.size
    val tmpX = orderX.slice(page.offset, page.offset + page.limit)
    val list = s"${Utils.path}/images".toFile.listFiles()


    val json = tmpX.map { y =>
      val path = list.find(_.getName.split("-").head == y.geneid.toString)

      Json.obj("id" -> y.id, "geneid" -> y.geneid, "organism" -> y.organism, "phylum" -> y.phylum,
        "class" -> y.classes, "order" -> y.order, "family" -> y.family, "genus" -> y.genus, "length" -> y.length,
        "ath" -> y.ath, "pubmed" -> y.pubmed, "journal" -> y.journal, "title" -> y.title, "author" -> y.author, "position" -> y.position,
        "collected" -> y.collected, "locality" -> y.locality, "description" -> y.description, "citation" -> y.citation,
        "link" -> y.link,"assembly"->y.assembly,"ncbi"->y.ncbi,"photos"->path.nonEmpty.toString)
    }
    Ok(Json.obj("rows" -> json, "total" -> total))
  }

  def genomeInfoPage(id: Int) = Action.async { implicit request =>
    genomeDao.getById(id).map { x =>
      val imgs = s"${Utils.path}/images/".toFile.listFiles()
      val index = imgs.filter(_.getName.split("-").head == x.geneid.toString).map(_.getName)
      Ok(views.html.english.genome.genomeInfoPage(x, index))
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
    val maxmap = TableUtils.maxmap.map(x=>(x._1.toLowerCase,x._2))
    val position = genomeDao.getById(id).toAwait.position

    val world = position.replaceAll(";", "、").replaceAll(",", "、").
      split("、").map(_.toLowerCase).flatMap { x =>
      if (maxmap.getOrElse(x.trim, "") != "") {
        maxmap(x.trim).map(_._3)
      } else {
        if (x.indexOf(":") != -1) {
          val w = mmap.filter(_._2.toLowerCase == x.split(":").last.trim).map(_._3)
          if (w.isEmpty) {
            mmap.filter(_._2.toLowerCase == x.split(":").head.trim).map(_._3)
          } else {
            w
          }
        } else {
          mmap.filter(_._2.toLowerCase == x.trim).map(_._3)
        }
      }
    }.distinct
    val json = world.map { y =>
      Json.obj("key" -> y)
    }
    Ok(Json.toJson(json))
  }

  def genePage(id: Int) = Action.async { implicit request =>
    geneDao.getById(id).flatMap { x =>
      genomeDao.getById(x.gbid).map { y =>
        Ok(views.html.english.genome.genePage(x, y.geneid))
      }
    }
  }

  def getSeqs(gb: String, range: String) = Action { implicit request =>
    val tmp = Files.createTempDirectory("tmpDir")

    try {
      val exec = new ExecCommand()
      val name = gb

      val pepF = new File(s"${Utils.path}/fasta/pep/$name.gb.pep")
      val cdsF = new File(s"${Utils.path}/fasta/cds/$name.gb.cds")
      val geneF = new File(s"${Utils.path}/fasta/gene/$name.gene")

      val geneCmd = Utils.samtools + "faidx " + geneF.getAbsolutePath + " " + range.split('.').head
      val cdsCmd = Utils.samtools + "faidx " + cdsF.getAbsolutePath + " " + range.split('.').head
      val pepCmd = Utils.samtools + "faidx " + pepF.getAbsolutePath + " " + range.split('.').head

      exec.exect(Array(geneCmd, cdsCmd, pepCmd), tmp.toString)
      val out = exec.getOutStr.split(">")
      val gene = out(1).split("\n").tail.mkString.trim
      val cds = out(2).split("\n").tail.mkString.trim
      val pep = out.last.split("\n").tail.mkString.trim
      Ok(Json.obj("gene" -> gene, "cds" -> cds, "pep" -> pep, "valid" -> "true"))
    } catch {
      case e: Exception => Ok(Json.obj("valid" -> "false"))
    } finally {
      tmp.toFile.deleteOnExit()
    }
  }

  def genomeInfoPageByGeneid(geneid: String): Action[AnyContent] = Action { implicit request =>
    val id = TableUtils.genomeToId(geneid)
    Redirect(routes.GenomeController.genomeInfoPage(id))
  }

  def genomeInfoPageByOrganism(organism: String): Action[AnyContent] = Action { implicit request =>
    val id = TableUtils.genomeRow.filter(_.organism == organism.replaceAll("_"," ")).head.id
    Redirect(routes.GenomeController.genomeInfoPage(id))
  }

  def geneInfoPageByGeneid(geneid: String): Action[AnyContent] = Action { implicit request =>
    val id = TableUtils.geneidToId(geneid)
    Redirect(routes.GenomeController.genePage(id))
  }

  def getImg = Action.async {
    genomeDao.getAllInfo.map { x =>
      val path = "D:\\软体动物线粒体数据库\\img2"
      val files = "D:\\软体动物线粒体数据库\\img".toFile.listFiles()
      println(files.length)
      x.foreach { y =>
        val name = y.organism.toLowerCase()
         files.filter(z=>z.getName.toLowerCase().indexOf(name) != -1 || z.getName.toLowerCase().indexOf(name.replaceAll(" ","_")) != -1
           || z.getName.toLowerCase().indexOf(name.replaceAll(" ","-")) != -1).zipWithIndex.foreach { z =>
          println(z._1.getName)
          FileUtils.copyFile(z._1,(path + "/" + y.geneid + "-" + z._2 + "." + z._1.getName.split('.').last).toFile)
          z._1.delete()
        }
      }
      Ok(Json.toJson("1"))
    }
  }


}
