package models.actor

import models.binding._
import models.provider._
import play.api.Play.current
import play.api.libs.ws._
import play.api.libs.json._
import play.api.Logger
import scala.xml._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{Success, Failure}
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import java.net.URI
import java.io._
import scalaxb.{
  DataRecord,
  ParserFailure
}
import org.joda.time._
import java.net.ConnectException


// basic trait for data provider actors (sim / obs)
trait DataProvider {
  import models.actor.DataProvider._
  
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
  
  protected def getTreeObjects: Spase = {
    val spase = getTrees flatMap { tree =>
    	scalaxb.fromXML[Spase](tree).ResourceEntity
  	}
    Spase(Number2u462u462, spase, "en")
  }
  
  protected def getStatus: Status = {
    Status(metadata.id, lastUpdate, lastError, isNotFound, isInvalid)
  }
  
  protected def getTreeObjects(element: String): Seq[DataRecord[Any]] = {
    getTreeObjects.ResourceEntity.filter(_.key.get == element)
  }
  
  protected def getRepository(id: Option[String]): Spase = {
    //println("RepositoryID="+id)
    val records = getTreeObjects("Repository") map {
	  repo => scalaxb.fromXML[Repository](repo.as[NodeSeq])
    }
    val repos = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
	Spase(Number2u462u462, repos.map(r => DataRecord(None, Some("Repository"), r)), "en")
  }
  
  protected def initData: Unit = {
    val dns: String = getMetaData.databaseoption.head.value
    val protocol: String = getMetaData.protocol.head
    val treeURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, getMetaData.tree)
    val methodsURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, getMetaData.methods)
      
    Logger.debug("\n fetching files from "+getMetaData.name+": \n { \n treeURL="+
    		treeURLs+" \n methodsURL="+methodsURLs+" \n }")

    trees = getFiles(treeURLs, "trees")
    methods = getFiles(methodsURLs, "methods")
  }
  
  protected def updateData: Unit = {
    import models.actor.ConfigService._
    val future = ConfigService.request(GetDatabaseById(getMetaData.id)).mapTo[Database]
    val Success(database: Database) = future.value.getOrElse(Success(getMetaData))
    
    val dns: String = database.databaseoption.head.value
    val protocol: String = database.protocol.head
    val treeURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, database.tree)
    val methodsURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, database.methods)   
    
    Logger.debug("\n updating files from "+getMetaData.name)
    
    metadata = database
    trees = getFiles(treeURLs, "trees")
    methods = getFiles(methodsURLs, "methods")
  }
  
  private def getFiles(URLs: Seq[URI], folder: String): Seq[NodeSeq] = { 
    // resetting status variables
    isNotFound = false
    isInvalid = false
    lastError = None
    
    URLs map { URL => 
      // encoding the id to represent a correct folder name
      val id: String = UrlProvider.encodeURI(getMetaData.id)
      val fileName: String = PathProvider.getPath(folder, id, URL.toString)
      //println(fileName)
      val promise = WS.url(URL.toString).get()

      try {     
        // resolving the promise
        val result = Await.result(promise, 1.minute).xml
        
        // checking the file system
      	val fileDir = new File(folder+"/"+id)
        if(!fileDir.exists) fileDir.mkdir()
        val file = new File(fileName)
        if(!file.exists || file.length() == 0) {
           file.createNewFile()
        } else {
           val oldFile = scala.xml.XML.load(fileName)
           if(oldFile != result) { 
        	 lastUpdate = DateTime.now()
           } 
        }
        
        // only the portal trees are validated
      	if(getMetaData.portal && folder == "trees") {
      		Logger.debug("spase tree found at "+getMetaData.name)
        	val spase = scalaxb.fromXML[Spase](result)
        }
      	
        scala.xml.XML.save(fileName, result, "UTF-8")
        result 
      } catch {
        // happens if there is a request timeout / remotely closed / connection refused error
        case e @ (_:TimeoutException | _:IOException | _:ConnectException) => {
          Logger.error("\n timeout: fallback on local file at "+getMetaData.name+" \n error: "+e)
          isNotFound = true 
          scala.xml.XML.load(fileName)
        }
        // happens if the file is not available remotely (404)
        case e: SAXParseException => {
          Logger.error("\n file not found: excluding file at "+getMetaData.name)
          isNotFound = true
          lastError = Some(DateTime.now())
          scala.xml.XML.load(fileName)
        }
        // happens if the file cannot be validated against data binding
        case e: ParserFailure => {
          Logger.error("\n file invalid: excluding file at "+getMetaData.name)
          isInvalid = true
          lastError = Some(DateTime.now())
          scala.xml.XML.load(fileName)
        }
      }
    }
  }
  

  
}

object DataProvider {
  implicit val timeout = Timeout(10.seconds)
  
  // common message formats
  case object GetTree
  case object GetMethods  
  case object UpdateData 
  case object GetStatus
  
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
      val r: Boolean = false)
  
  // status message
  case class Status(
      val id: java.net.URI,
      val lastUpdate: DateTime,
      val lastError: Option[DateTime],
      val isNotFound: Boolean,
      val isInvalid: Boolean)

  implicit val statusWrites: Writes[Seq[Status]] = new Writes[Seq[Status]] {
    def writes(s: Seq[Status]): JsValue = {
      s.map(p => Json.obj(p.id.toString -> Json.obj(
          "lastUpdate" -> p.lastUpdate.toString(),
          "lastError" -> p.lastError.map(_.toString()),
          "isNotFound" -> p.isNotFound,
          "isInvalid" -> p.isInvalid))).reduce(_++_)
    }
  }
  
  // @TODO we need that for updating the trees dynamically (on admin request)
  def updateData(provider: ActorSelection) = {
    (provider ? UpdateData)
  }
  
}