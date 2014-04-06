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
import scalaxb.DataRecord


// container for XML content
case class Trees(var content: Seq[NodeSeq])
case class Methods(var content: Seq[NodeSeq])

// basic trait for data provider actors (sim / obs)
trait DataProvider {
  val dataTree: Trees
  val accessMethods: Methods

  // predefined methods
  protected def getMethods: Seq[NodeSeq] = accessMethods.content
  protected def getMetaData: Database
  protected def getTreeObjects(element: String): Seq[DataRecord[Any]]
  protected def getRepository(id: Option[String] = None): Spase
  
  // @FIXME update methods too and check if there is a tree/method tag
  protected def updateTrees: Seq[NodeSeq] = {
    val dns: String = getMetaData.databaseoption.head.value
    val protocol: String = getMetaData.protocol.head
    val URLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, getMetaData.tree)
    URLs flatMap {
      URL =>
        // sometimes there is no file (e.g. at AMDA)
        // this must be recreated by calling getObsDataTree
        val promise = WS.url(URL.toString).get()
        try {
          val result = Await.result(promise, 2.minutes).xml
          scala.xml.XML.save(
              PathProvider.getPath("trees", getMetaData.name, URL.toString), result, "UTF-8")
          result
        } catch {
          case e: TimeoutException =>
            println("timeout: fallback on local file at "+getMetaData.name)
            scala.xml.XML.loadFile(
              PathProvider.getPath("trees", getMetaData.name, URL.toString))
        }
    }
  }
  
}

object DataProvider {
  implicit val timeout = Timeout(10.seconds)
  
  // common message formats
  case object GetTree
  case object GetMethods  
  case object UpdateTrees 
  
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
  def updateTrees(provider: ActorSelection) = {
    (provider ? UpdateTrees)
  }
  
}