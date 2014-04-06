package models.actor

import models.binding._
import models.provider._
import play.api.libs.ws._
import scala.xml._
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import java.net.URI
import java.io._
import scalaxb.DataRecord


// basic trait for data provider actors (sim / obs)
trait DataProvider {
  var metadata: Database
  var trees: Seq[NodeSeq] = Seq()
  var methods: Seq[NodeSeq] = Seq()

  // predefined methods
  protected def getMetaData: Database = metadata
  protected def getTrees: Seq[NodeSeq] = trees
  protected def getMethods: Seq[NodeSeq] = methods
  protected def getTreeObjects(element: String): Seq[DataRecord[Any]]
  protected def getRepository(id: Option[String] = None): Spase
  
  protected def initData(metadata: Database) = {
    val dns: String = metadata.databaseoption.head.value
    val protocol: String = metadata.protocol.head
    val treeURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, metadata.tree)
    val methodsURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, metadata.methods)
      
    println("fetching files from "+metadata.name+":")
    println("{")
    println("treeURL="+treeURLs)
    println("methodsURL="+methodsURLs)
    println("}")

    trees = getFiles(treeURLs, "trees")
    methods = getFiles(methodsURLs, "methods")
  }
  
  private def getFiles(URLs: Seq[URI], folder: String): Seq[NodeSeq] = { 
    URLs map { URL => 
      val id: String = UrlProvider.encodeURI(getMetaData.id)
      val fileName: String = PathProvider.getPath(folder, id, URL.toString)
      
      // @TODO we do not download in DEVELOPMENT
//      val fileDir = new File(folder+"/"+id)
//      if(!fileDir.exists) fileDir.mkdir()
//      val file = new File(fileName)
//      if(!file.exists) file.createNewFile()
//      
//      // sometimes there is no file (e.g. at AMDA)
//      // this must be recreated by calling getObsDataTree
//      val promise = WS.url(URL.toString).get()
//      
//      try {
//        val result = Await.result(promise, 1.minute).xml
//        scala.xml.XML.save(fileName, result, "UTF-8")
//        result
//      } catch {
//        case e: TimeoutException => {
//          println("timeout: fallback on local file at "+getMetaData.name)
          scala.xml.XML.load(fileName)
//        }
//      }
    }
  }
  
  // @FIXME update metadata from config service before the files
  protected def updateData(metadata: Database) = {
    val dns: String = metadata.databaseoption.head.value
    val protocol: String = metadata.protocol.head
    val treeURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, metadata.tree)
    val methodsURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, metadata.methods)
    
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
  case object ERepository extends Element
  trait SimElement extends Element
  case object ESimulationModel extends SimElement
  case object ESimulationRun extends SimElement
  case object ENumericalOutput extends SimElement
  case object EGranule extends SimElement
  trait ObsElement extends Element
  case object EObservatory extends ObsElement
  case object EInstrument extends ObsElement
  case object ENumericalData extends ObsElement
  
  // generic message for elements
  case class GetElement(
      val dType: Element, 
      val id: Option[String], 
      val recursive: Boolean = false
  )
  
  // @TODO we need that later for updating the trees dynamically (on admin request)
  // and when updating the config
  def updateData(provider: ActorSelection) = {
    (provider ? UpdateData)
  }
  
}