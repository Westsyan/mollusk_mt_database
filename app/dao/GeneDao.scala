package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class GeneDao   @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                         (implicit exec:ExecutionContext)extends
  HasDatabaseConfigProvider[JdbcProfile]  {

  import profile.api._

  def insertGene(row:Seq[GeneRow]) : Future[Unit] = {
    db.run(Gene ++= row).map(_=>())
  }

  def getAllGene : Future[Seq[GeneRow]] = {
    db.run(Gene.result)
  }

  def getByGbId(id:Int):Future[Seq[GeneRow]] = {
    db.run(Gene.filter(_.gbid === id).result)
  }

  def getById(id:Int) : Future[GeneRow] = {
    db.run(Gene.filter(_.id === id).result.head)
  }

}
