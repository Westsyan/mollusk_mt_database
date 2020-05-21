package controllers

import java.io.File

import config.MyConfig
import dao._
import javax.inject.{Inject, Singleton}
import models.Tables._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, Results}
import utils.ReadExcel._
import utils.Utils

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

@Singleton
class InsertController @Inject()(genomeDao:GenomeDao,
                                 genesumDao: GenesumDao,
                                 geneDao:GeneDao,
                                 cc: ControllerComponents)
                                (implicit exec: ExecutionContext) extends AbstractController(cc) with MyConfig {


  def insertGemome = Action {
    if (Utils.isWindow) {
        val row = xlsx2Lines("D:\\软体动物线粒体数据库/demo.xlsx".toFile).map(_.replaceAll("\n", " ")).map { line =>
          val l = line.split("\t")
          val geneid = getOrNull(l, 0)
          val organism = getOrNull(l, 1)
          val phylum = getOrNull(l, 2)
          val classes = getOrNull(l, 3)
          val order = getOrNull(l, 4)
          val family = getOrNull(l, 5)
          val genus = getOrNull(l, 6)
          val length = getOrNull(l, 7)
          val ath = getOrNull(l, 8)
          val pubmed = getOrNull(l, 9)
          val journal = getOrNull(l, 10)
          val title = getOrNull(l, 11)
          val author = getOrNull(l, 12)
          val position = getOrNull(l, 13)
          val collected = getOrNull(l, 14)
          val locality = getOrNull(l, 15)
          val description = getOrNull(l, 16)
          val citation = getOrNull(l, 17)
          val link = getOrNull(l, 19)
          GenomeRow(0, geneid,organism, phylum, classes, order, family, genus, length,ath,pubmed,journal,title,author,
            position,collected,locality,description,citation,link)
        }
      genomeDao.insertGenome(row).toAwait
      Ok(Json.toJson(1))
    } else {
      Results.BadRequest
    }
  }

  def insertGeneSum = Action {

    val genome = genomeDao.getAllInfo.toAwait


    val fa = new File("D:\\软体动物线粒体数据库\\demofasta").listFiles()
    val gff = new File("D:\\软体动物线粒体数据库\\demogff").listFiles()

    val row = genome.map { x =>

        val name = x.geneid
        val f = fa.find(_.getName.startsWith(name))
        if (f.isDefined) {
          println(name)
          val gFile = Utils.readLines(gff.find(_.getName.startsWith(name)).get)
          val l = Utils.readLines(f.get)
          val fasta = gFile.indexOf("##FASTA")
          val gl = gFile.take(fasta).filter(!_.startsWith("#"))

          val line = gl.map(_.split("\t"))
          val rRNA = line.count(x=>x(2) == "rRNA")
          val tRNA = line.count(x=>x(2) == "tRNA")
          val orf = gFile.count(_.contains("protein_id"))
          val atp = gFile.count(_.contains("ATP synthase"))
          val nadh = gFile.count(_.contains("NADH dehydrogenase"))
          val ribos = gFile.count(_.contains("ribosomal protein"))
          val gene = line.count(x=>x(2)=="gene")
          val other = gene-rRNA-tRNA
          val seq = l.tail.mkString
          GenesumRow(0,  seq.length, getGC(seq).formatted("%.2f") + "%", atp.toString, "" , nadh.toString,
            ribos.toString, orf.toString, tRNA.toString, rRNA.toString,  gene.toString)
        } else {
          if (fa.count(_.getName.startsWith(name)) != 0) {
            val l = Utils.readLines(fa.find(_.getName.startsWith(name)).get)
            val seq = l.tail.mkString
            GenesumRow(0, seq.length, getGC(seq).formatted("%.2f") + "%", "", "", "", "", "", "", "",  "")
          } else {
            GenesumRow(0, 0, "0", "", "", "", "", "", "", "", "")
          }
      }
    }
    genesumDao.insertGeneSum(row)
    Ok(Json.toJson("1"))
  }

  def insertGene = Action.async{

    val gff = new File("D:\\软体动物线粒体数据库\\demogff").listFiles()
    genomeDao.getAllInfo.map { x =>
      x.foreach { cp =>
        val f = gff.find(_.getName.startsWith(cp.geneid))
        println(f.get.getName)
        println(f.isDefined)
        if (f.isDefined) {
          val l = Utils.readLines(f.get)
          val fasta = l.indexOf("##FASTA")
          val gl = l.take(fasta).filter(!_.startsWith("#")).map(_.split("\t")).
            filter(x=> x(2) == "gene")
          val row = gl.map{gene=>
            val last = gene.last.split(";")
            val id = last.find(_.startsWith("ID")).get.drop(3)
            val info = last.filter(!_.startsWith("ID")).mkString(";")
            GeneRow(0, id, cp.id,gene(2), gene(3).toInt, gene(4).toInt, gene(6),info)
          }
          Await.result(geneDao.insertGene(row), Duration.Inf)
        }
      }
      Ok(Json.toJson("1"))
    }
  }

  def getGC(seq: String) = {
    val array = seq.split("").map(_.toUpperCase())
    val c = array.count(_ == "C")
    val g = array.count(_ == "G")
    (c.toDouble + g.toDouble) * 100 / array.length.toDouble
  }

}
