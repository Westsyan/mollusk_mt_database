package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class GenomeDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                          (implicit exec:ExecutionContext)extends
  HasDatabaseConfigProvider[JdbcProfile]   {

  import profile.api._

  def insertGenome(row:Seq[GenomeRow]) : Future[Unit] = {
    db.run(Genome ++= row).map(_=>())
  }

  def getAllInfo : Future[Seq[GenomeRow]] = {
    db.run(Genome.result)
  }

  def getById(id:Int) : Future[GenomeRow] = {
    db.run(Genome.filter(_.id === id).result.head)
  }


}
