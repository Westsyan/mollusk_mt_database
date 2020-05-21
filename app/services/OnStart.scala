package services

import java.io.File

import config.{MyConfig, Search}
import dao._
import javax.inject._
import play.Logger
import play.api.libs.json.Json
import utils.{TableUtils, Utils}

@Singleton
class OnStart @Inject()(genomeDao: GenomeDao,
                        geneDao: GeneDao) extends MyConfig{


  Logger.info("开启成功！")

  val genomeRow = genomeDao.getAllInfo.toAwait

  TableUtils.genomeToId = genomeRow.map{x=> x.geneid-> x.id}.toMap

  val geneRow = geneDao.getAllGene.toAwait

  TableUtils.geneidToId = geneRow.map{x=> x.geneid -> x.id}.toMap

  TableUtils.searchSeq = genomeRow.map{x=>
    (x.id,Seq(x.geneid,x.organism,x.phylum,x.classes,x.order,x.family,x.genus,x.journal,x.title,x.author,x.position,
      x.collected,x.locality,x.description,x.citation).mkString("\t"))
  }

  val js = new File(Utils.path + "/config/world.js")
  println(js.getAbsolutePath)
  val line = Utils.readLines(js).filter(_.indexOf("type") != -1)
  TableUtils.mmap = line.map { x =>
    val p = x.indexOf("properties")
    val g = x.indexOf("geometry")
    val m = x.slice(p + 14, g - 4)
    val map = m.replaceAll("\"", "").split(",").map { y =>
      val e = y.split(":")
      (e.head.trim, e.last.trim)
    }.toMap
    (map("continent"), map("name"), map("hc-key"))
  }
  TableUtils.maxmap = TableUtils.mmap.groupBy(_._1)

  Logger.info("地图数据初始化成功")

  Search.searchConfig

  getTreeData


  case class treeData(id: String,  species: String, phylum: String, classes: String,
                      order: String, family: String, genus: String,uid:Int)

  def getTreeData = {

    val genome = genomeDao.getAllInfo.toAwait
    val c = genome.map { x =>
      treeData(x.geneid, x.organism, x.phylum, x.classes, x.order, x.family, x.genus,x.id)
    }

    val x = c

    val gene = x.map { y =>
      val html =  "<a onclick=\"getInfo('" + y.uid + "')\"  style='color: inherit;'>"  + y.id + "</a>"
      (y.genus, y.species, Json.obj("text" -> html, "nodes" -> ""))
    }

    val species = x.map { y =>
      val node = gene.filter(_._1 == y.genus).filter(_._2 == y.species).map(_._3).distinct
      (y.family, y.genus, Json.obj("text" -> y.species, "tags" -> Array(node.size), "nodes" -> node))
    }

    val genus = x.map { y =>
      val node = species.filter(_._1 == y.family).filter(_._2 == y.genus).map(_._3).distinct
      (y.order, y.family, Json.obj("text" -> y.genus, "tags" -> Array(node.size), "nodes" -> node))
    }

    val family = x.map { y =>
      val node = genus.filter(_._1 == y.order).filter(_._2 == y.family).map(_._3).distinct
      (y.classes, y.order, Json.obj("text" -> y.family, "tags" -> Array(node.size), "nodes" -> node))
    }.distinct

    val order = x.map { y =>
      val node = family.filter(_._1 == y.classes).filter(_._2 == y.order).map(_._3).distinct
      (y.phylum, y.classes, Json.obj("text" -> y.order, "tags" -> Array(node.size), "nodes" -> node))
    }.distinct

    val classes = x.map { y =>
      val node = order.filter(_._1 == y.phylum).filter(_._2 == y.classes).map(_._3).distinct
      (y.phylum, Json.obj("text" -> y.classes, "tags" -> Array(node.size), "nodes" -> node))
    }


    val phylumn = x.map(_.phylum).distinct

    val nodes = phylumn.map { y =>
      val node = classes.filter(_._1 == y).map(_._2).distinct
      Json.obj("text" -> y, "tags" -> Array(node.size), "nodes" -> node)
    }

    TableUtils.treeData = nodes

  }
}



