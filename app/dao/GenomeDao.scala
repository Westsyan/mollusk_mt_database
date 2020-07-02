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

  def getByClasses(classes:String) : Future[Seq[GenomeRow]] = {
    db.run(Genome.filter(_.classes === classes).result)
  }

  def addStatus(id:Int,assembly:String,ncbi:String) : Future[Unit] = {
    db.run(Genome.filter(_.id === id).map(x=>(x.assembly,x.ncbi)).update((assembly,ncbi))).map(_=>())
  }

  def updatePosition(id:Int,position:String) : Future[Unit] = {
    db.run(Genome.filter(_.id === id).map(_.position).update(position)).map(_=>())
  }

  def updateCitation(id:Int,citation:String) : Future[Unit] = {
    db.run(Genome.filter(_.id === id).map(_.citation).update(citation)).map(_=>())
  }

  def updateLink(id:Int,link:String):Future[Unit] = {
    db.run(Genome.filter(_.id === id).map(_.link).update(link)).map(_=>())
  }

}
