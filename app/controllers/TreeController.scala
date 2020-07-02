package controllers

import java.io.File

import config.MyConfig
import dao.GenomeDao
import javax.inject.Inject
import org.apache.commons.io.FileUtils
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.{TableUtils, Utils}

import scala.collection.JavaConverters._

import scala.concurrent.ExecutionContext

class TreeController @Inject()(cc: ControllerComponents, genomeDao: GenomeDao)(implicit exec: ExecutionContext) extends AbstractController(cc) with MyConfig {

  def tree = Action { implicit request =>
    Ok(views.html.english.tree.tree())
  }

  def getTreeJson = Action {
    val nodes = TableUtils.treeData
    Ok(Json.toJson(nodes))
  }


  def getTreeData(id: Int) = Action.async { implicit request =>
    genomeDao.getById(id).map { x =>
      val imgs = s"${Utils.path}/images/".toFile.listFiles()
      val index = imgs.filter(_.getName.split("-").head == x.geneid).map(_.getName)

      val photo = if (index.length == 0) {
        "<h2>Still Collecting Photos</h2>"
      } else {

          s"""
             |   <img src="/mollusk/utils/getGenomeImgByName?name=${index.head}"
             |       style="margin: 0 auto;
             |      max-height: 400px;
             |   width: 260px"/>
             |
             |""".stripMargin

      }

      val html =
        s"""
           |<table>
           |<tbody>
           |<tr>
           |                            <th>Organism</th>
           |                            <td><i>${x.organism}</i></td>
           |                        </tr>
           |<tr>
           |<th>ID</th>
           |<td>${x.geneid}</td>
           |</tr>

           |                        <tr>
           |                            <th>Phylumn</th>
           |                            <td>${x.phylum}</td>
           |                        </tr>
           |                        <tr>
           |                            <th>Class</th>
           |                            <td>${x.classes}</td>
           |                        </tr>
           |                        <tr>
           |                            <th>Order</th>
           |                            <td>${x.order}</td>
           |                        </tr>
           |                        <tr>
           |                            <th>Family</th>
           |                            <td>${x.family}</td>
           |                        </tr>
           |                        <tr>
           |                            <th>Genus</th>
           |                            <td>${x.genus}</td>
           |                        </tr>
           |                        <tr>
           |                            <th>Photo</th>
           |                            <td style="vertical-align: middle;
           |                                    text-align: center">
           |                                         $photo
           |                            </td>
           |                        </tr>
           |
           |</tbody>
           |</table>
           |<a href="/US/mollusk/genome/genomeInfoPage?id=${x.id}" target="_blank">
           |<button type='button' class="button blue" style="float:right">More Info</button>
           |</a>
           |""".stripMargin
      Ok(Json.obj("html" -> html))
    }
  }


  def treeOfLife = Action { implicit request =>
    Ok(views.html.english.tree.treeOfLife())
  }

  def plot = Action {
    val x = FileUtils.readFileToString(new File(Utils.path + "/tree1/time_tree.tree"))
    Ok(Json.obj("tree" -> x))
  }

  def replaceGbToSpecies = Action {
    val tree = s"${Utils.path}/tree/tree.newick".readFileToString
    val genome = genomeDao.getAllInfo.toAwait
    var tree2 = tree
    genome.foreach { x =>
      tree2 = tree2.replaceAll(x.geneid.split('.').head, x.organism.replaceAll(" ", "_"))
    }

    val order = genome.groupBy(_.order).zipWithIndex.flatMap { x =>
      x._1._2.map { y =>
        (Array(x._2.toString, y.order), Array(y.organism.replaceAll(" ", "_"), x._2.toString))
      }
    }

    val tran = order.values.transpose.map { x =>
      x.mkString(",")
    }.toBuffer

    val orde = order.keys.toArray.groupBy(_.head).map(x => Array(x._1, x._2.map(_.last).head)).toArray
      .sortBy(_.head.toInt).transpose.map { x =>
      x.mkString(",")
    }.toBuffer


    FileUtils.writeLines(s"${Utils.path}/tree1/tree2.csv".toFile, tran.asJava)
    FileUtils.writeLines(s"${Utils.path}/tree1/tree4.csv".toFile, orde.asJava)


    FileUtils.writeStringToFile(s"${Utils.path}/tree1/tree2.newick".toFile, tree2)
    Ok(Json.toJson("1"))
  }


  def treeTest = Action { implicit request =>
    Ok(views.html.english.tree.test())
  }


}
