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
  lazy val schema: profile.SchemaDescription = Array(Chloroplast.schema, GeographicalData.schema, Location.schema, Message.schema, News.schema, Password.schema, PhotoInfo.schema, Ppgi.schema, RnaInfo.schema, SpeciesNum.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Chloroplast
   *  @param id Database column id SqlType(INT), AutoInc
   *  @param sample Database column sample SqlType(VARCHAR), Length(255,true) */
  final case class ChloroplastRow(id: Int, sample: String)
  /** GetResult implicit for fetching ChloroplastRow objects using plain SQL queries */
  implicit def GetResultChloroplastRow(implicit e0: GR[Int], e1: GR[String]): GR[ChloroplastRow] = GR{
    prs => import prs._
    ChloroplastRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table chloroplast. Objects of this class serve as prototypes for rows in queries. */
  class Chloroplast(_tableTag: Tag) extends profile.api.Table[ChloroplastRow](_tableTag, Some("fern_database"), "chloroplast") {
    def * = (id, sample) <> (ChloroplastRow.tupled, ChloroplastRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(sample)).shaped.<>({r=>import r._; _1.map(_=> ChloroplastRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc */
    val id: Rep[Int] = column[Int]("id", O.AutoInc)
    /** Database column sample SqlType(VARCHAR), Length(255,true) */
    val sample: Rep[String] = column[String]("sample", O.Length(255,varying=true))

    /** Primary key of Chloroplast (database name chloroplast_PK) */
    val pk = primaryKey("chloroplast_PK", (id, sample))
  }
  /** Collection-like TableQuery object for table Chloroplast */
  lazy val Chloroplast = new TableQuery(tag => new Chloroplast(tag))

  /** Entity class storing rows of table GeographicalData
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param familyCh Database column family_ch SqlType(VARCHAR), Length(255,true)
   *  @param familyEn Database column family_en SqlType(VARCHAR), Length(255,true)
   *  @param genusCh Database column genus_ch SqlType(VARCHAR), Length(255,true)
   *  @param genusEn Database column genus_en SqlType(VARCHAR), Length(255,true)
   *  @param chinesename Database column chinesename SqlType(VARCHAR), Length(255,true)
   *  @param scientificName Database column scientific_name SqlType(VARCHAR), Length(2550,true)
   *  @param scientificAbbr Database column scientific_abbr SqlType(VARCHAR), Length(255,true)
   *  @param quote Database column quote SqlType(VARCHAR), Length(255,true)
   *  @param location Database column location SqlType(VARCHAR), Length(255,true) */
  final case class GeographicalDataRow(id: Int, familyCh: String, familyEn: String, genusCh: String, genusEn: String, chinesename: String, scientificName: String, scientificAbbr: String, quote: String, location: String)
  /** GetResult implicit for fetching GeographicalDataRow objects using plain SQL queries */
  implicit def GetResultGeographicalDataRow(implicit e0: GR[Int], e1: GR[String]): GR[GeographicalDataRow] = GR{
    prs => import prs._
    GeographicalDataRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table geographical_data. Objects of this class serve as prototypes for rows in queries. */
  class GeographicalData(_tableTag: Tag) extends profile.api.Table[GeographicalDataRow](_tableTag, Some("fern_database"), "geographical_data") {
    def * = (id, familyCh, familyEn, genusCh, genusEn, chinesename, scientificName, scientificAbbr, quote, location) <> (GeographicalDataRow.tupled, GeographicalDataRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(familyCh), Rep.Some(familyEn), Rep.Some(genusCh), Rep.Some(genusEn), Rep.Some(chinesename), Rep.Some(scientificName), Rep.Some(scientificAbbr), Rep.Some(quote), Rep.Some(location)).shaped.<>({r=>import r._; _1.map(_=> GeographicalDataRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column family_ch SqlType(VARCHAR), Length(255,true) */
    val familyCh: Rep[String] = column[String]("family_ch", O.Length(255,varying=true))
    /** Database column family_en SqlType(VARCHAR), Length(255,true) */
    val familyEn: Rep[String] = column[String]("family_en", O.Length(255,varying=true))
    /** Database column genus_ch SqlType(VARCHAR), Length(255,true) */
    val genusCh: Rep[String] = column[String]("genus_ch", O.Length(255,varying=true))
    /** Database column genus_en SqlType(VARCHAR), Length(255,true) */
    val genusEn: Rep[String] = column[String]("genus_en", O.Length(255,varying=true))
    /** Database column chinesename SqlType(VARCHAR), Length(255,true) */
    val chinesename: Rep[String] = column[String]("chinesename", O.Length(255,varying=true))
    /** Database column scientific_name SqlType(VARCHAR), Length(2550,true) */
    val scientificName: Rep[String] = column[String]("scientific_name", O.Length(2550,varying=true))
    /** Database column scientific_abbr SqlType(VARCHAR), Length(255,true) */
    val scientificAbbr: Rep[String] = column[String]("scientific_abbr", O.Length(255,varying=true))
    /** Database column quote SqlType(VARCHAR), Length(255,true) */
    val quote: Rep[String] = column[String]("quote", O.Length(255,varying=true))
    /** Database column location SqlType(VARCHAR), Length(255,true) */
    val location: Rep[String] = column[String]("location", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table GeographicalData */
  lazy val GeographicalData = new TableQuery(tag => new GeographicalData(tag))

  /** Entity class storing rows of table Location
   *  @param id Database column id SqlType(INT), AutoInc
   *  @param location Database column location SqlType(VARCHAR), Length(255,true)
   *  @param dataId Database column data_id SqlType(INT) */
  final case class LocationRow(id: Int, location: String, dataId: Int)
  /** GetResult implicit for fetching LocationRow objects using plain SQL queries */
  implicit def GetResultLocationRow(implicit e0: GR[Int], e1: GR[String]): GR[LocationRow] = GR{
    prs => import prs._
    LocationRow.tupled((<<[Int], <<[String], <<[Int]))
  }
  /** Table description of table location. Objects of this class serve as prototypes for rows in queries. */
  class Location(_tableTag: Tag) extends profile.api.Table[LocationRow](_tableTag, Some("fern_database"), "location") {
    def * = (id, location, dataId) <> (LocationRow.tupled, LocationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(location), Rep.Some(dataId)).shaped.<>({r=>import r._; _1.map(_=> LocationRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc */
    val id: Rep[Int] = column[Int]("id", O.AutoInc)
    /** Database column location SqlType(VARCHAR), Length(255,true) */
    val location: Rep[String] = column[String]("location", O.Length(255,varying=true))
    /** Database column data_id SqlType(INT) */
    val dataId: Rep[Int] = column[Int]("data_id")

    /** Primary key of Location (database name location_PK) */
    val pk = primaryKey("location_PK", (id, location))
  }
  /** Collection-like TableQuery object for table Location */
  lazy val Location = new TableQuery(tag => new Location(tag))

  /** Entity class storing rows of table Message
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param phone Database column phone SqlType(VARCHAR), Length(255,true)
   *  @param mail Database column mail SqlType(VARCHAR), Length(255,true)
   *  @param ip Database column ip SqlType(VARCHAR), Length(255,true)
   *  @param message Database column message SqlType(LONGTEXT), Length(2147483647,true) */
  final case class MessageRow(id: Int, name: String, phone: String, mail: String, ip: String, message: String)
  /** GetResult implicit for fetching MessageRow objects using plain SQL queries */
  implicit def GetResultMessageRow(implicit e0: GR[Int], e1: GR[String]): GR[MessageRow] = GR{
    prs => import prs._
    MessageRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table message. Objects of this class serve as prototypes for rows in queries. */
  class Message(_tableTag: Tag) extends profile.api.Table[MessageRow](_tableTag, Some("fern_database"), "message") {
    def * = (id, name, phone, mail, ip, message) <> (MessageRow.tupled, MessageRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(phone), Rep.Some(mail), Rep.Some(ip), Rep.Some(message)).shaped.<>({r=>import r._; _1.map(_=> MessageRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column phone SqlType(VARCHAR), Length(255,true) */
    val phone: Rep[String] = column[String]("phone", O.Length(255,varying=true))
    /** Database column mail SqlType(VARCHAR), Length(255,true) */
    val mail: Rep[String] = column[String]("mail", O.Length(255,varying=true))
    /** Database column ip SqlType(VARCHAR), Length(255,true) */
    val ip: Rep[String] = column[String]("ip", O.Length(255,varying=true))
    /** Database column message SqlType(LONGTEXT), Length(2147483647,true) */
    val message: Rep[String] = column[String]("message", O.Length(2147483647,varying=true))
  }
  /** Collection-like TableQuery object for table Message */
  lazy val Message = new TableQuery(tag => new Message(tag))

  /** Entity class storing rows of table News
   *  @param id Database column id SqlType(INT), AutoInc
   *  @param userid Database column userid SqlType(INT)
   *  @param title Database column title SqlType(VARCHAR), Length(255,true)
   *  @param path Database column path SqlType(VARCHAR), Length(255,true)
   *  @param createdate Database column createdate SqlType(DATETIME) */
  final case class NewsRow(id: Int, userid: Int, title: String, path: String, createdate: DateTime)
  /** GetResult implicit for fetching NewsRow objects using plain SQL queries */
  implicit def GetResultNewsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[DateTime]): GR[NewsRow] = GR{
    prs => import prs._
    NewsRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<[DateTime]))
  }
  /** Table description of table news. Objects of this class serve as prototypes for rows in queries. */
  class News(_tableTag: Tag) extends profile.api.Table[NewsRow](_tableTag, Some("fern_database"), "news") {
    def * = (id, userid, title, path, createdate) <> (NewsRow.tupled, NewsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userid), Rep.Some(title), Rep.Some(path), Rep.Some(createdate)).shaped.<>({r=>import r._; _1.map(_=> NewsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc */
    val id: Rep[Int] = column[Int]("id", O.AutoInc)
    /** Database column userid SqlType(INT) */
    val userid: Rep[Int] = column[Int]("userid")
    /** Database column title SqlType(VARCHAR), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column path SqlType(VARCHAR), Length(255,true) */
    val path: Rep[String] = column[String]("path", O.Length(255,varying=true))
    /** Database column createdate SqlType(DATETIME) */
    val createdate: Rep[DateTime] = column[DateTime]("createdate")

    /** Primary key of News (database name news_PK) */
    val pk = primaryKey("news_PK", (id, userid))
  }
  /** Collection-like TableQuery object for table News */
  lazy val News = new TableQuery(tag => new News(tag))

  /** Entity class storing rows of table Password
   *  @param id Database column id SqlType(INT), AutoInc
   *  @param user Database column user SqlType(VARCHAR), Length(255,true)
   *  @param password Database column password SqlType(VARCHAR), Length(255,true) */
  final case class PasswordRow(id: Int, user: String, password: String)
  /** GetResult implicit for fetching PasswordRow objects using plain SQL queries */
  implicit def GetResultPasswordRow(implicit e0: GR[Int], e1: GR[String]): GR[PasswordRow] = GR{
    prs => import prs._
    PasswordRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table password. Objects of this class serve as prototypes for rows in queries. */
  class Password(_tableTag: Tag) extends profile.api.Table[PasswordRow](_tableTag, Some("fern_database"), "password") {
    def * = (id, user, password) <> (PasswordRow.tupled, PasswordRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(user), Rep.Some(password)).shaped.<>({r=>import r._; _1.map(_=> PasswordRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc */
    val id: Rep[Int] = column[Int]("id", O.AutoInc)
    /** Database column user SqlType(VARCHAR), Length(255,true) */
    val user: Rep[String] = column[String]("user", O.Length(255,varying=true))
    /** Database column password SqlType(VARCHAR), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))

    /** Primary key of Password (database name password_PK) */
    val pk = primaryKey("password_PK", (id, user))
  }
  /** Collection-like TableQuery object for table Password */
  lazy val Password = new TableQuery(tag => new Password(tag))

  /** Entity class storing rows of table PhotoInfo
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param order Database column order SqlType(VARCHAR), Length(255,true)
   *  @param family Database column family SqlType(VARCHAR), Length(255,true)
   *  @param genus Database column genus SqlType(VARCHAR), Length(255,true)
   *  @param chinesename Database column chinesename SqlType(VARCHAR), Length(255,true)
   *  @param author Database column author SqlType(VARCHAR), Length(255,true)
   *  @param place Database column place SqlType(VARCHAR), Length(255,true)
   *  @param photo Database column photo SqlType(VARCHAR), Length(255,true) */
  final case class PhotoInfoRow(id: Int, name: String, order: String, family: String, genus: String, chinesename: String, author: String, place: String, photo: String)
  /** GetResult implicit for fetching PhotoInfoRow objects using plain SQL queries */
  implicit def GetResultPhotoInfoRow(implicit e0: GR[Int], e1: GR[String]): GR[PhotoInfoRow] = GR{
    prs => import prs._
    PhotoInfoRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table photo_info. Objects of this class serve as prototypes for rows in queries. */
  class PhotoInfo(_tableTag: Tag) extends profile.api.Table[PhotoInfoRow](_tableTag, Some("fern_database"), "photo_info") {
    def * = (id, name, order, family, genus, chinesename, author, place, photo) <> (PhotoInfoRow.tupled, PhotoInfoRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(order), Rep.Some(family), Rep.Some(genus), Rep.Some(chinesename), Rep.Some(author), Rep.Some(place), Rep.Some(photo)).shaped.<>({r=>import r._; _1.map(_=> PhotoInfoRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column order SqlType(VARCHAR), Length(255,true) */
    val order: Rep[String] = column[String]("order", O.Length(255,varying=true))
    /** Database column family SqlType(VARCHAR), Length(255,true) */
    val family: Rep[String] = column[String]("family", O.Length(255,varying=true))
    /** Database column genus SqlType(VARCHAR), Length(255,true) */
    val genus: Rep[String] = column[String]("genus", O.Length(255,varying=true))
    /** Database column chinesename SqlType(VARCHAR), Length(255,true) */
    val chinesename: Rep[String] = column[String]("chinesename", O.Length(255,varying=true))
    /** Database column author SqlType(VARCHAR), Length(255,true) */
    val author: Rep[String] = column[String]("author", O.Length(255,varying=true))
    /** Database column place SqlType(VARCHAR), Length(255,true) */
    val place: Rep[String] = column[String]("place", O.Length(255,varying=true))
    /** Database column photo SqlType(VARCHAR), Length(255,true) */
    val photo: Rep[String] = column[String]("photo", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table PhotoInfo */
  lazy val PhotoInfo = new TableQuery(tag => new PhotoInfo(tag))

  /** Entity class storing rows of table Ppgi
   *  @param id Database column id SqlType(INT), AutoInc
   *  @param classes Database column classes SqlType(VARCHAR), Length(255,true)
   *  @param orderes Database column orderes SqlType(VARCHAR), Length(255,true)
   *  @param families Database column families SqlType(VARCHAR), Length(255,true)
   *  @param subfamilies Database column subfamilies SqlType(VARCHAR), Length(255,true)
   *  @param genus Database column genus SqlType(VARCHAR), Length(255,true)
   *  @param species Database column species SqlType(VARCHAR), Length(255,true) */
  final case class PpgiRow(id: Int, classes: String, orderes: String, families: String, subfamilies: String, genus: String, species: String)
  /** GetResult implicit for fetching PpgiRow objects using plain SQL queries */
  implicit def GetResultPpgiRow(implicit e0: GR[Int], e1: GR[String]): GR[PpgiRow] = GR{
    prs => import prs._
    PpgiRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table ppgi. Objects of this class serve as prototypes for rows in queries. */
  class Ppgi(_tableTag: Tag) extends profile.api.Table[PpgiRow](_tableTag, Some("fern_database"), "ppgi") {
    def * = (id, classes, orderes, families, subfamilies, genus, species) <> (PpgiRow.tupled, PpgiRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(classes), Rep.Some(orderes), Rep.Some(families), Rep.Some(subfamilies), Rep.Some(genus), Rep.Some(species)).shaped.<>({r=>import r._; _1.map(_=> PpgiRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc */
    val id: Rep[Int] = column[Int]("id", O.AutoInc)
    /** Database column classes SqlType(VARCHAR), Length(255,true) */
    val classes: Rep[String] = column[String]("classes", O.Length(255,varying=true))
    /** Database column orderes SqlType(VARCHAR), Length(255,true) */
    val orderes: Rep[String] = column[String]("orderes", O.Length(255,varying=true))
    /** Database column families SqlType(VARCHAR), Length(255,true) */
    val families: Rep[String] = column[String]("families", O.Length(255,varying=true))
    /** Database column subfamilies SqlType(VARCHAR), Length(255,true) */
    val subfamilies: Rep[String] = column[String]("subfamilies", O.Length(255,varying=true))
    /** Database column genus SqlType(VARCHAR), Length(255,true) */
    val genus: Rep[String] = column[String]("genus", O.Length(255,varying=true))
    /** Database column species SqlType(VARCHAR), Length(255,true) */
    val species: Rep[String] = column[String]("species", O.Length(255,varying=true))

    /** Primary key of Ppgi (database name ppgi_PK) */
    val pk = primaryKey("ppgi_PK", (id, classes))
  }
  /** Collection-like TableQuery object for table Ppgi */
  lazy val Ppgi = new TableQuery(tag => new Ppgi(tag))

  /** Entity class storing rows of table RnaInfo
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param sampleId Database column sample_id SqlType(VARCHAR), Length(255,true)
   *  @param speciesName Database column species_name SqlType(VARCHAR), Length(255,true)
   *  @param chineseName Database column chinese_name SqlType(VARCHAR), Length(255,true)
   *  @param order Database column order SqlType(VARCHAR), Length(255,true)
   *  @param subOrder Database column sub_order SqlType(VARCHAR), Length(255,true)
   *  @param family Database column family SqlType(VARCHAR), Length(255,true)
   *  @param subFamily Database column sub_family SqlType(VARCHAR), Length(255,true)
   *  @param genus Database column genus SqlType(VARCHAR), Length(255,true)
   *  @param tissue Database column tissue SqlType(VARCHAR), Length(255,true)
   *  @param samplingLocation Database column sampling_location SqlType(VARCHAR), Length(255,true)
   *  @param instrument Database column instrument SqlType(VARCHAR), Length(255,true)
   *  @param layout Database column layout SqlType(VARCHAR), Length(255,true)
   *  @param rawData Database column raw_data SqlType(VARCHAR), Length(255,true)
   *  @param q30 Database column q30 SqlType(VARCHAR), Length(255,true)
   *  @param numberOfContigs Database column number_of_contigs SqlType(VARCHAR), Length(255,true)
   *  @param n50 Database column n50 SqlType(VARCHAR), Length(255,true)
   *  @param buscoCompletedRate Database column busco_completed_rate SqlType(VARCHAR), Length(255,true)
   *  @param biosampleIdInNcbi Database column biosample_id_in_ncbi SqlType(VARCHAR), Length(255,true)
   *  @param href Database column href SqlType(VARCHAR), Length(255,true) */
  final case class RnaInfoRow(id: Int, sampleId: String, speciesName: String, chineseName: String, order: String, subOrder: String, family: String, subFamily: String, genus: String, tissue: String, samplingLocation: String, instrument: String, layout: String, rawData: String, q30: String, numberOfContigs: String, n50: String, buscoCompletedRate: String, biosampleIdInNcbi: String, href: String)
  /** GetResult implicit for fetching RnaInfoRow objects using plain SQL queries */
  implicit def GetResultRnaInfoRow(implicit e0: GR[Int], e1: GR[String]): GR[RnaInfoRow] = GR{
    prs => import prs._
    RnaInfoRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table rna_info. Objects of this class serve as prototypes for rows in queries. */
  class RnaInfo(_tableTag: Tag) extends profile.api.Table[RnaInfoRow](_tableTag, Some("fern_database"), "rna_info") {
    def * = (id, sampleId, speciesName, chineseName, order, subOrder, family, subFamily, genus, tissue, samplingLocation, instrument, layout, rawData, q30, numberOfContigs, n50, buscoCompletedRate, biosampleIdInNcbi, href) <> (RnaInfoRow.tupled, RnaInfoRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(sampleId), Rep.Some(speciesName), Rep.Some(chineseName), Rep.Some(order), Rep.Some(subOrder), Rep.Some(family), Rep.Some(subFamily), Rep.Some(genus), Rep.Some(tissue), Rep.Some(samplingLocation), Rep.Some(instrument), Rep.Some(layout), Rep.Some(rawData), Rep.Some(q30), Rep.Some(numberOfContigs), Rep.Some(n50), Rep.Some(buscoCompletedRate), Rep.Some(biosampleIdInNcbi), Rep.Some(href)).shaped.<>({r=>import r._; _1.map(_=> RnaInfoRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get, _19.get, _20.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column sample_id SqlType(VARCHAR), Length(255,true) */
    val sampleId: Rep[String] = column[String]("sample_id", O.Length(255,varying=true))
    /** Database column species_name SqlType(VARCHAR), Length(255,true) */
    val speciesName: Rep[String] = column[String]("species_name", O.Length(255,varying=true))
    /** Database column chinese_name SqlType(VARCHAR), Length(255,true) */
    val chineseName: Rep[String] = column[String]("chinese_name", O.Length(255,varying=true))
    /** Database column order SqlType(VARCHAR), Length(255,true) */
    val order: Rep[String] = column[String]("order", O.Length(255,varying=true))
    /** Database column sub_order SqlType(VARCHAR), Length(255,true) */
    val subOrder: Rep[String] = column[String]("sub_order", O.Length(255,varying=true))
    /** Database column family SqlType(VARCHAR), Length(255,true) */
    val family: Rep[String] = column[String]("family", O.Length(255,varying=true))
    /** Database column sub_family SqlType(VARCHAR), Length(255,true) */
    val subFamily: Rep[String] = column[String]("sub_family", O.Length(255,varying=true))
    /** Database column genus SqlType(VARCHAR), Length(255,true) */
    val genus: Rep[String] = column[String]("genus", O.Length(255,varying=true))
    /** Database column tissue SqlType(VARCHAR), Length(255,true) */
    val tissue: Rep[String] = column[String]("tissue", O.Length(255,varying=true))
    /** Database column sampling_location SqlType(VARCHAR), Length(255,true) */
    val samplingLocation: Rep[String] = column[String]("sampling_location", O.Length(255,varying=true))
    /** Database column instrument SqlType(VARCHAR), Length(255,true) */
    val instrument: Rep[String] = column[String]("instrument", O.Length(255,varying=true))
    /** Database column layout SqlType(VARCHAR), Length(255,true) */
    val layout: Rep[String] = column[String]("layout", O.Length(255,varying=true))
    /** Database column raw_data SqlType(VARCHAR), Length(255,true) */
    val rawData: Rep[String] = column[String]("raw_data", O.Length(255,varying=true))
    /** Database column q30 SqlType(VARCHAR), Length(255,true) */
    val q30: Rep[String] = column[String]("q30", O.Length(255,varying=true))
    /** Database column number_of_contigs SqlType(VARCHAR), Length(255,true) */
    val numberOfContigs: Rep[String] = column[String]("number_of_contigs", O.Length(255,varying=true))
    /** Database column n50 SqlType(VARCHAR), Length(255,true) */
    val n50: Rep[String] = column[String]("n50", O.Length(255,varying=true))
    /** Database column busco_completed_rate SqlType(VARCHAR), Length(255,true) */
    val buscoCompletedRate: Rep[String] = column[String]("busco_completed_rate", O.Length(255,varying=true))
    /** Database column biosample_id_in_ncbi SqlType(VARCHAR), Length(255,true) */
    val biosampleIdInNcbi: Rep[String] = column[String]("biosample_id_in_ncbi", O.Length(255,varying=true))
    /** Database column href SqlType(VARCHAR), Length(255,true) */
    val href: Rep[String] = column[String]("href", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table RnaInfo */
  lazy val RnaInfo = new TableQuery(tag => new RnaInfo(tag))

  /** Entity class storing rows of table SpeciesNum
   *  @param id Database column id SqlType(INT)
   *  @param location Database column location SqlType(VARCHAR), Length(255,true)
   *  @param speciesNum Database column species_num SqlType(INT) */
  final case class SpeciesNumRow(id: Int, location: String, speciesNum: Int)
  /** GetResult implicit for fetching SpeciesNumRow objects using plain SQL queries */
  implicit def GetResultSpeciesNumRow(implicit e0: GR[Int], e1: GR[String]): GR[SpeciesNumRow] = GR{
    prs => import prs._
    SpeciesNumRow.tupled((<<[Int], <<[String], <<[Int]))
  }
  /** Table description of table species_num. Objects of this class serve as prototypes for rows in queries. */
  class SpeciesNum(_tableTag: Tag) extends profile.api.Table[SpeciesNumRow](_tableTag, Some("fern_database"), "species_num") {
    def * = (id, location, speciesNum) <> (SpeciesNumRow.tupled, SpeciesNumRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(location), Rep.Some(speciesNum)).shaped.<>({r=>import r._; _1.map(_=> SpeciesNumRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT) */
    val id: Rep[Int] = column[Int]("id")
    /** Database column location SqlType(VARCHAR), Length(255,true) */
    val location: Rep[String] = column[String]("location", O.Length(255,varying=true))
    /** Database column species_num SqlType(INT) */
    val speciesNum: Rep[Int] = column[Int]("species_num")

    /** Primary key of SpeciesNum (database name species_num_PK) */
    val pk = primaryKey("species_num_PK", (id, location))
  }
  /** Collection-like TableQuery object for table SpeciesNum */
  lazy val SpeciesNum = new TableQuery(tag => new SpeciesNum(tag))
}
