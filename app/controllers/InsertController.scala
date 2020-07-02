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
class InsertController @Inject()(genomeDao: GenomeDao,

                                 geneDao: GeneDao,
                                 cc: ControllerComponents)
                                (implicit exec: ExecutionContext) extends AbstractController(cc) with MyConfig {


  def insertGemome = Action {
    if (Utils.isWindow) {
      val tax = xlsx2Lines("D:\\软体动物线粒体数据库/6_17tax.xlsx".toFile).map(_.replaceAll("\n", " "))
      val sample = xlsx2Lines("D:\\软体动物线粒体数据库/6_17sample.xlsx".toFile).map(_.replaceAll("\n", " "))
      val loc = "D:\\软体动物线粒体数据库/gps信息(1).txt".readLines
      val citation = "D:\\软体动物线粒体数据库/citation(1).txt".readLines

      val sampleM = sample.map(_.split("\t")).map(x => (x.head, x))
      val locM = loc.map(_.split("\t")).map(x => (x.head, x(2))).toMap
      val citationM = citation.map(_.split("\t")).map{x=>
        println(x.head)
        (x.head,x(2) + "|" + x(3).replaceAll("No information",""))}.toMap
      val row = tax.map(_.split("\t")).map { l =>
        val ss = sampleM.find(_._1 == l.head)
        val s = ss.get._2
        val geneid = getOrNull(l, 0)
        val organism = getOrNull(l, 1)
        val phylum = getOrNull(l, 2)
        val classes = getOrNull(l, 3)
        val order = getOrNull(l, 4)
        val family = getOrNull(l, 5)
        val genus = getOrNull(l, 6)
        val length = getOrNull(l, 7)
        val ath = getOrNull(l, 8)

        println(geneid)
        val pubmed = getOrNull(s, 2)
        val journal = getOrNull(s, 3)
        val title = getOrNull(s, 4)
        val author = getOrNull(s, 5)
        val position = getOrNull(s, 6)
        val collected = getOrNull(s, 7)
        val locality = locM(geneid)
        val description = getOrNull(s, 9)
        val citation = citationM(geneid)
        val photo = getOrNull(s, 12)
        val link = getOrNull(s, 13)
        val stu = getOrNull(s, 14)

        GenomeRow(0, geneid, organism, phylum, classes, order, family, genus, length, ath, pubmed, journal, title, author,
          position, collected, locality, description, citation, link, photo, stu,"","")
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
        val rRNA = line.count(x => x(2) == "rRNA")
        val tRNA = line.count(x => x(2) == "tRNA")
        val orf = gFile.count(_.contains("protein_id"))
        val atp = gFile.count(_.contains("ATP synthase"))
        val nadh = gFile.count(_.contains("NADH dehydrogenase"))
        val ribos = gFile.count(_.contains("ribosomal protein"))
        val gene = line.count(x => x(2) == "gene")
        val other = gene - rRNA - tRNA
        val seq = l.tail.mkString
        (0, seq.length, getGC(seq).formatted("%.2f") + "%", atp.toString, "", nadh.toString,
          ribos.toString, orf.toString, tRNA.toString, rRNA.toString, gene.toString)
      } else {
        if (fa.count(_.getName.startsWith(name)) != 0) {
          val l = Utils.readLines(fa.find(_.getName.startsWith(name)).get)
          val seq = l.tail.mkString
          (0, seq.length, getGC(seq).formatted("%.2f") + "%", "", "", "", "", "", "", "", "")
        } else {
          (0, 0, "0", "", "", "", "", "", "", "", "")
        }
      }
    }

    Ok(Json.toJson("1"))
  }

  def insertGene = Action.async {

    val gff = new File("D:\\软体动物线粒体数据库\\gff").listFiles()
    genomeDao.getAllInfo.map { x =>
      x.foreach { cp =>
        val f = gff.find(_.getName.startsWith(cp.geneid))
        println(f.get.getName)
        println(f.isDefined)
        if (f.isDefined) {
          val l = Utils.readLines(f.get)
          val fasta = l.indexOf("##FASTA")
          val gl = l.take(fasta).filter(!_.startsWith("#")).map(_.split("\t")).
            filter(x => x(2) == "gene")
          val row = gl.map { gene =>
            val last = gene.last.split(";")
            val id = last.find(_.startsWith("ID")).get.drop(3)
            val info = last.filter(!_.startsWith("ID")).mkString(";")
            GeneRow(0, id, cp.id, gene(2), gene(3).toInt, gene(4).toInt, gene(6), info)
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

  def getLocation(loc: String) = {
    if (loc.trim.toLowerCase() == "no information") {
      "No information"
    } else {
      loc.split("[|]").map { x =>
        val e = x.split(";").flatMap { y =>
          println(y)
          val re = y.split(",")
          if (re.length > 2) {
            re.grouped(2).map { r =>
              val lon = r.head.toDouble
              val lo = if (lon > 180) {
                lon - 360
              } else if (lon < -180) {
                lon + 360
              } else {
                lon
              }
              val lat = r.last.toDouble
              val la = if (lat > 90) {
                lat - 180
              } else if (lat < -90) {
                lat + 180
              } else {
                lat
              }
              (lo, la)
            }
          } else {
            val lon = re.head.toDouble
            val lo = if (lon > 180) {
              lon - 360
            } else if (lon < -180) {
              lon + 360
            } else {
              lon
            }
            val lat = re.last.toDouble
            val la = if (lat > 90) {
              lat - 180
            } else if (lat < -90) {
              lat + 180
            } else {
              lat
            }
            Array((lo, la))
          }
        }

        val sortX = e.sortBy(_._1)
        val sL = sortX.take(2)
        val sR = sortX.takeRight(2)
        val lh = sL.maxBy(_._2)
        val lb = sL.minBy(_._2)
        val rh = sR.maxBy(_._2)
        val rb = sR.minBy(_._2)

        val other = sortX.drop(2).dropRight(2).map { x =>
          val x1 = (4, (x._1 - lh._1).abs + (x._2 - lh._2).abs)
          val x2 = (1, (x._1 - lb._1).abs + (x._2 - lb._2).abs)
          val x3 = (2, (x._1 - rb._1).abs + (x._2 - rb._2).abs)
          val x4 = (3, (x._1 - rh._1).abs + (x._2 - rh._2).abs)
          val w = Array(x1, x2, x3, x4).minBy(_._2)
          (w._1, x)
        }

        def getOther(id: Int) = {
          other.filter(_._1 == id).map(_._2).sortBy(_._1)
        }

        val lhTolb = getOther(1)
        val lbTorb = getOther(2)
        val rbTorh = getOther(3)
        val rhTolh = getOther(4)

        val result = Array(lh) ++ lhTolb ++ Array(lb) ++ lbTorb ++ Array(rb) ++ rbTorh ++ Array(rh) ++ rhTolh
        result.map(x => x._1 + "," + x._2).mkString(";")
      }.distinct.mkString("|")
    }
  }


  def addStatus = Action{
    val status = xlsx2Lines("D:\\软体动物线粒体数据库/序列状态(1)(1).xlsx".toFile).map(_.replaceAll("\n", " "))

    val statusM = status.map(_.split("\t")).map(x => (x.head, x))

    val genome = genomeDao.getAllInfo.toAwait
    genome.map{x=>
      val geneid = x.geneid
      val r = statusM.find(_._1.startsWith(geneid)).get._2
      val assembly = r(1)
      val ncbi = r(2)
      genomeDao.addStatus(x.id,assembly,ncbi)

    }
    Ok(Json.toJson("1"))
  }

  def updateGeographical = Action{
    val position = xlsx2Lines("D:\\软体动物线粒体数据库/地理分布.xlsx".toFile).map(_.replaceAll("\n"," "))
    val positionM = position.map(_.split("\t")).map(x=> (x.head,x.last))
    val genome = genomeDao.getAllInfo.toAwait
    genome.foreach{x=>
      val geneid = x.geneid
      val po = positionM.find(_._1.startsWith(geneid)).get._2
      genomeDao.updatePosition(x.id,po).toAwait
    }
    Ok(Json.toJson("1"))
  }

  def updateCitation = Action{
    val file = "D:\\软体动物线粒体数据库\\软体动物citation".toFile.listFiles()
    val genome = genomeDao.getAllInfo.toAwait
    genome.foreach{x=>
      val f = file.find(_.getName.startsWith(x.geneid)).get.readLines.tail.mkString("|")
      println(f)
       genomeDao.updateCitation(x.id,f)
    }

    Ok(Json.toJson("1"))
  }


  def updateLocationRussia = Action{
    val genome = genomeDao.getAllInfo.toAwait
    genome.foreach{x=>
      val positionArray = x.position.split(";").map(_.trim)
      if(positionArray.indexOf("Russian") != -1){
        val index = positionArray.indexOf("Russian")
        positionArray(index) = "Russia"
        genomeDao.updatePosition(x.id,positionArray.mkString(";"))
      }else if(positionArray.indexOf("Russian Federation")!= -1){
        val index = positionArray.indexOf("Russian Federation")
        positionArray(index) = "Russia"
        genomeDao.updatePosition(x.id,positionArray.mkString(";"))
      }
    }

    Ok(Json.toJson("1"))
  }


  def updateLink = Action{
    val genome = genomeDao.getAllInfo.toAwait
    val file = "D:\\软体动物线粒体数据库\\Link_file(1).txt".readLines

    val linkMap = file.map(_.split("\t").map(_.trim)).map{x=>
      (x.head,x.drop(2).mkString("\t"))
    }.toMap

    genome.foreach{x=>
      genomeDao.updateLink(x.id,linkMap(x.geneid))
    }
    Ok(Json.toJson("1"))


  }

}
