package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import com.github.tototoshi.slick.MySQLJodaSupport._
  import org.joda.time.DateTime
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Gene.schema ++ Genesum.schema ++ Genome.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Gene
   *  @param id Database column id SqlType(INT), AutoInc
   *  @param geneid Database column geneid SqlType(VARCHAR), Length(255,true)
   *  @param gbid Database column gbid SqlType(INT)
   *  @param rna Database column rna SqlType(VARCHAR), Length(255,true)
   *  @param start Database column start SqlType(INT)
   *  @param end Database column end SqlType(INT)
   *  @param strand Database column strand SqlType(TEXT)
   *  @param product Database column product SqlType(TEXT) */
  case class GeneRow(id: Int, geneid: String, gbid: Int, rna: String, start: Int, end: Int, strand: String, product: String)
  /** GetResult implicit for fetching GeneRow objects using plain SQL queries */
  implicit def GetResultGeneRow(implicit e0: GR[Int], e1: GR[String]): GR[GeneRow] = GR{
    prs => import prs._
    GeneRow.tupled((<<[Int], <<[String], <<[Int], <<[String], <<[Int], <<[Int], <<[String], <<[String]))
  }
  /** Table description of table gene. Objects of this class serve as prototypes for rows in queries. */
  class Gene(_tableTag: Tag) extends profile.api.Table[GeneRow](_tableTag, Some("mollusk_mt_db"), "gene") {
    def * = (id, geneid, gbid, rna, start, end, strand, product) <> (GeneRow.tupled, GeneRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(geneid), Rep.Some(gbid), Rep.Some(rna), Rep.Some(start), Rep.Some(end), Rep.Some(strand), Rep.Some(product))).shaped.<>({r=>import r._; _1.map(_=> GeneRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc */
    val id: Rep[Int] = column[Int]("id", O.AutoInc)
    /** Database column geneid SqlType(VARCHAR), Length(255,true) */
    val geneid: Rep[String] = column[String]("geneid", O.Length(255,varying=true))
    /** Database column gbid SqlType(INT) */
    val gbid: Rep[Int] = column[Int]("gbid")
    /** Database column rna SqlType(VARCHAR), Length(255,true) */
    val rna: Rep[String] = column[String]("rna", O.Length(255,varying=true))
    /** Database column start SqlType(INT) */
    val start: Rep[Int] = column[Int]("start")
    /** Database column end SqlType(INT) */
    val end: Rep[Int] = column[Int]("end")
    /** Database column strand SqlType(TEXT) */
    val strand: Rep[String] = column[String]("strand")
    /** Database column product SqlType(TEXT) */
    val product: Rep[String] = column[String]("product")

    /** Primary key of Gene (database name gene_PK) */
    val pk = primaryKey("gene_PK", (id, geneid))
  }
  /** Collection-like TableQuery object for table Gene */
  lazy val Gene = new TableQuery(tag => new Gene(tag))

  /** Entity class storing rows of table Genesum
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param length Database column length SqlType(INT)
   *  @param gc Database column gc SqlType(VARCHAR), Length(255,true)
   *  @param atp Database column atp SqlType(TEXT)
   *  @param evt Database column evt SqlType(TEXT)
   *  @param nadh Database column nadh SqlType(TEXT)
   *  @param protein Database column protein SqlType(TEXT)
   *  @param orf Database column orf SqlType(TEXT)
   *  @param trna Database column tRna SqlType(TEXT)
   *  @param rrna Database column rRna SqlType(TEXT)
   *  @param total Database column total SqlType(TEXT) */
  case class GenesumRow(id: Int, length: Int, gc: String, atp: String, evt: String, nadh: String, protein: String, orf: String, trna: String, rrna: String, total: String)
  /** GetResult implicit for fetching GenesumRow objects using plain SQL queries */
  implicit def GetResultGenesumRow(implicit e0: GR[Int], e1: GR[String]): GR[GenesumRow] = GR{
    prs => import prs._
    GenesumRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table genesum. Objects of this class serve as prototypes for rows in queries. */
  class Genesum(_tableTag: Tag) extends profile.api.Table[GenesumRow](_tableTag, Some("mollusk_mt_db"), "genesum") {
    def * = (id, length, gc, atp, evt, nadh, protein, orf, trna, rrna, total) <> (GenesumRow.tupled, GenesumRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(length), Rep.Some(gc), Rep.Some(atp), Rep.Some(evt), Rep.Some(nadh), Rep.Some(protein), Rep.Some(orf), Rep.Some(trna), Rep.Some(rrna), Rep.Some(total))).shaped.<>({r=>import r._; _1.map(_=> GenesumRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column length SqlType(INT) */
    val length: Rep[Int] = column[Int]("length")
    /** Database column gc SqlType(VARCHAR), Length(255,true) */
    val gc: Rep[String] = column[String]("gc", O.Length(255,varying=true))
    /** Database column atp SqlType(TEXT) */
    val atp: Rep[String] = column[String]("atp")
    /** Database column evt SqlType(TEXT) */
    val evt: Rep[String] = column[String]("evt")
    /** Database column nadh SqlType(TEXT) */
    val nadh: Rep[String] = column[String]("nadh")
    /** Database column protein SqlType(TEXT) */
    val protein: Rep[String] = column[String]("protein")
    /** Database column orf SqlType(TEXT) */
    val orf: Rep[String] = column[String]("orf")
    /** Database column tRna SqlType(TEXT) */
    val trna: Rep[String] = column[String]("tRna")
    /** Database column rRna SqlType(TEXT) */
    val rrna: Rep[String] = column[String]("rRna")
    /** Database column total SqlType(TEXT) */
    val total: Rep[String] = column[String]("total")
  }
  /** Collection-like TableQuery object for table Genesum */
  lazy val Genesum = new TableQuery(tag => new Genesum(tag))

  /** Entity class storing rows of table Genome
   *  @param id Database column id SqlType(INT), AutoInc
   *  @param geneid Database column geneid SqlType(VARCHAR), Length(255,true)
   *  @param organism Database column organism SqlType(TEXT)
   *  @param phylum Database column phylum SqlType(TEXT)
   *  @param classes Database column classes SqlType(TEXT)
   *  @param order Database column order SqlType(TEXT)
   *  @param family Database column family SqlType(TEXT)
   *  @param genus Database column genus SqlType(TEXT)
   *  @param length Database column length SqlType(TEXT)
   *  @param at Database column at SqlType(TEXT)
   *  @param pubmed Database column pubmed SqlType(TEXT)
   *  @param journal Database column journal SqlType(TEXT)
   *  @param title Database column title SqlType(TEXT)
   *  @param author Database column author SqlType(TEXT)
   *  @param position Database column position SqlType(TEXT)
   *  @param collected Database column collected SqlType(TEXT)
   *  @param locality Database column locality SqlType(TEXT)
   *  @param description Database column description SqlType(TEXT)
   *  @param citation Database column citation SqlType(TEXT)
   *  @param link Database column link SqlType(TEXT) */
  case class GenomeRow(id: Int, geneid: String, organism: String, phylum: String, classes: String, order: String, family: String, genus: String, length: String, at: String, pubmed: String, journal: String, title: String, author: String, position: String, collected: String, locality: String, description: String, citation: String, link: String)
  /** GetResult implicit for fetching GenomeRow objects using plain SQL queries */
  implicit def GetResultGenomeRow(implicit e0: GR[Int], e1: GR[String]): GR[GenomeRow] = GR{
    prs => import prs._
    GenomeRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table genome. Objects of this class serve as prototypes for rows in queries. */
  class Genome(_tableTag: Tag) extends profile.api.Table[GenomeRow](_tableTag, Some("mollusk_mt_db"), "genome") {
    def * = (id, geneid, organism, phylum, classes, order, family, genus, length, at, pubmed, journal, title, author, position, collected, locality, description, citation, link) <> (GenomeRow.tupled, GenomeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(geneid), Rep.Some(organism), Rep.Some(phylum), Rep.Some(classes), Rep.Some(order), Rep.Some(family), Rep.Some(genus), Rep.Some(length), Rep.Some(at), Rep.Some(pubmed), Rep.Some(journal), Rep.Some(title), Rep.Some(author), Rep.Some(position), Rep.Some(collected), Rep.Some(locality), Rep.Some(description), Rep.Some(citation), Rep.Some(link))).shaped.<>({r=>import r._; _1.map(_=> GenomeRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get, _19.get, _20.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc */
    val id: Rep[Int] = column[Int]("id", O.AutoInc)
    /** Database column geneid SqlType(VARCHAR), Length(255,true) */
    val geneid: Rep[String] = column[String]("geneid", O.Length(255,varying=true))
    /** Database column organism SqlType(TEXT) */
    val organism: Rep[String] = column[String]("organism")
    /** Database column phylum SqlType(TEXT) */
    val phylum: Rep[String] = column[String]("phylum")
    /** Database column classes SqlType(TEXT) */
    val classes: Rep[String] = column[String]("classes")
    /** Database column order SqlType(TEXT) */
    val order: Rep[String] = column[String]("order")
    /** Database column family SqlType(TEXT) */
    val family: Rep[String] = column[String]("family")
    /** Database column genus SqlType(TEXT) */
    val genus: Rep[String] = column[String]("genus")
    /** Database column length SqlType(TEXT) */
    val length: Rep[String] = column[String]("length")
    /** Database column at SqlType(TEXT) */
    val at: Rep[String] = column[String]("at")
    /** Database column pubmed SqlType(TEXT) */
    val pubmed: Rep[String] = column[String]("pubmed")
    /** Database column journal SqlType(TEXT) */
    val journal: Rep[String] = column[String]("journal")
    /** Database column title SqlType(TEXT) */
    val title: Rep[String] = column[String]("title")
    /** Database column author SqlType(TEXT) */
    val author: Rep[String] = column[String]("author")
    /** Database column position SqlType(TEXT) */
    val position: Rep[String] = column[String]("position")
    /** Database column collected SqlType(TEXT) */
    val collected: Rep[String] = column[String]("collected")
    /** Database column locality SqlType(TEXT) */
    val locality: Rep[String] = column[String]("locality")
    /** Database column description SqlType(TEXT) */
    val description: Rep[String] = column[String]("description")
    /** Database column citation SqlType(TEXT) */
    val citation: Rep[String] = column[String]("citation")
    /** Database column link SqlType(TEXT) */
    val link: Rep[String] = column[String]("link")

    /** Primary key of Genome (database name genome_PK) */
    val pk = primaryKey("genome_PK", (id, geneid))
  }
  /** Collection-like TableQuery object for table Genome */
  lazy val Genome = new TableQuery(tag => new Genome(tag))
}
