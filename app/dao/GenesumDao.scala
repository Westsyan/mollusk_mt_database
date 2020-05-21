package dao

import javax.inject.{Inject, Singleton}
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GenesumDao  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                           (implicit exec:ExecutionContext)extends
  HasDatabaseConfigProvider[JdbcProfile]   {

  import profile.api._

  def insertGeneSum(row:Seq[GenesumRow]) : Future[Unit] = {
    db.run(Genesum ++= row).map(_=>())
  }


}
