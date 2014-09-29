package models.actor

import models.binding._
import models.provider._
import play.api.libs.ws._
import scala.xml._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import java.net.URI
import java.io._
import scalaxb.DataRecord
import scala.util.{Success, Failure}
import org.joda.time._
import scalaxb.ParserFailure


// basic trait for data provider actors (sim / obs)
trait DataProvider {
  var metadata: Database
  var trees: Seq[NodeSeq] = Seq()
  var methods: Seq[NodeSeq] = Seq()
  var lastUpdate: DateTime = DateTime.now()
  var isInvalid: Boolean = false
  var isNotFound: Boolean = false
  var lastError: Option[DateTime] = None

  // predefined methods
  protected def getMetaData: Database = metadata
  protected def getTrees: Seq[NodeSeq] = trees
  protected def getMethods: Seq[NodeSeq] = methods
  protected def getTreeObjects: Spase
  protected def getTreeObjects(element: String): Seq[DataRecord[Any]]
  protected def getRepository(id: Option[String] = None): Spase
  
  protected def initData: Unit = {
    val dns: String = getMetaData.databaseoption.head.value
    val protocol: String = getMetaData.protocol.head
    val treeURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, getMetaData.tree)
    val methodsURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, getMetaData.methods)
      
    println("fetching files from "+getMetaData.name+":")
    println("{")
    println("treeURL="+treeURLs)
    println("methodsURL="+methodsURLs)
    println("}")

    trees = getFiles(treeURLs, "trees")
    methods = getFiles(methodsURLs, "methods")
  }
  
  // @TODO we need to introduce some status variables
  private def getFiles(URLs: Seq[URI], folder: String): Seq[NodeSeq] = { 
    URLs map { URL => 
      // encoding the id to represent a correct folder name
      val id: String = UrlProvider.encodeURI(getMetaData.id)
      val fileName: String = PathProvider.getPath(folder, id, URL.toString)
      
      val fileDir = new File(folder+"/"+id)
      if(!fileDir.exists) fileDir.mkdir()
      val file = new File(fileName)
      if(!file.exists) file.createNewFile()
      
      // @TODO sometimes there is no file (e.g. at AMDA)
      // this must be recreated by calling getObsDataTree
      val promise = WS.url(URL.toString).get()
      
      try {
        isNotFound = false
      	isInvalid = false
      	lastError = None
        val result = Await.result(promise, 1.minute).xml
        val oldFile = scala.xml.XML.load(fileName)
        // @TODO validating the simulation trees (later name != "CLWEB")
        // => put disabled list in application.conf
      	if(metadata.typeValue == Simulation && folder == "tree") {
      		println("Simulation Tree")
        	val spase = scalaxb.fromXML[Spase](result)
        }
        // if the file has changed => update timestamp
        if(oldFile != result) { 
        	lastUpdate = DateTime.now()
        }

        scala.xml.XML.save(fileName, result, "UTF-8")
        result
      } catch {
        // happens if there is a request timeout
        case e: TimeoutException => {
          println("timeout: fallback on local file at "+getMetaData.name)
          isNotFound = true
          scala.xml.XML.load(fileName)
        }
        // happens if the file is not available remotely (404)
        case e: SAXParseException => {
          println("file not found: excluding file at "+getMetaData.name)
          isNotFound = true
          lastError = Some(DateTime.now())
          scala.xml.XML.load(fileName)
        }
        // happens if the file cannot be validated against data binding
        case e: ParserFailure => {
          println("file invalid: excluding file at "+getMetaData.name)
          isInvalid = true
          lastError = Some(DateTime.now())
          scala.xml.XML.load(fileName)
        }
      }
    }
  }
  
  protected def updateData: Unit = {
    import models.actor.ConfigService._
    val future = ConfigService.request(GetDatabaseById(getMetaData.id)).mapTo[Database]
    val Success(database: Database) = future.value.getOrElse(Success(getMetaData))
    
    val dns: String = database.databaseoption.head.value
    val protocol: String = database.protocol.head
    val treeURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, database.tree)
    val methodsURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, database.methods)   
    
    println("updating files from "+getMetaData.name)
    
    metadata = database
    trees = getFiles(treeURLs, "trees")
    methods = getFiles(methodsURLs, "methods")
  }
  
}

object DataProvider {
  implicit val timeout = Timeout(10.seconds)
  
  // common message formats
  case object GetTree
  case object GetMethods  
  case object UpdateData 
  
  // elements of the data model
  trait Element
  trait GenElement extends Element
  trait SimElement extends Element
  trait ObsElement extends Element
  // general
  case object ERepository extends GenElement { override def toString = "Repository" }
  // simulations
  case object ESimulationModel extends SimElement { override def toString = "SimulationModel" }
  case object ESimulationRun extends SimElement { override def toString = "SimulationRun" }
  case object ENumericalOutput extends SimElement { override def toString = "NumericalOutput" }
  case object EGranule extends SimElement { override def toString = "Granule" }
  // observations
  case object EObservatory extends ObsElement { override def toString = "Observatory" }
  case object EInstrument extends ObsElement { override def toString = "Instrument" }
  case object ENumericalData extends ObsElement { override def toString = "NumericalData" }
  
  // generic message for elements
  case class GetElement(
      val dType: Element, 
      val id: Option[String], 
      val r: Boolean = false
  )
  
  // @TODO we need that later for updating the trees dynamically (on admin request)
  def updateData(provider: ActorSelection) = {
    (provider ? UpdateData)
  }
  
}