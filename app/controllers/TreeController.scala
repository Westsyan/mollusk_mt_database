package controllers

import dao.GenomeDao
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.TableUtils

import scala.concurrent.ExecutionContext

class TreeController @Inject()(cc:ControllerComponents,genomeDao:GenomeDao)(implicit exec:ExecutionContext) extends AbstractController(cc) {

  def tree = Action { implicit request =>
    Ok(views.html.english.tree.tree())
  }

  def getTreeJson = Action {
    val nodes = TableUtils.treeData
    Ok(Json.toJson(nodes))
  }


  def getTreeData(id:Int) = Action.async{implicit request=>
    genomeDao.getById(id).map{x=>
      val html =
        s"""
          |<table>
          |<tbody>
          |<tr>
          |<th>ID</th>
          |<td>${x.geneid}</td>
          |</tr>
          |<tr>
          |                            <th>Organism</th>
          |                            <td>${x.organism}</td>
          |                        </tr>
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
          |                            <td><img src="/mollusk/utils/getGemomeImg?id=${x.id}"
          |                                style='margin: 0 auto;
          |                                    max-height: 300px;max-width:300px'/></td>
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


}
