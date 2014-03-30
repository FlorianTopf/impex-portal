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


// container for XML content
case class Trees(var content: Seq[NodeSeq])
case class Methods(var content: Seq[NodeSeq])

// basic trait for data provider actors (sim / obs)
trait DataProvider[A] {
  val dataTree: Trees
  val accessMethods: Methods

  // predefined methods
  protected def getTreeXML: NodeSeq
  protected def getMethodsXML: Seq[NodeSeq] = accessMethods.content
  protected def getTreeObjects: Seq[A]
  protected def getMetaData: Database
  protected def getRepository: Spase
  
  // @FIXME update methods too!
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
  case class GetTrees(val format: Option[String] = None)
  case object GetMethods
  // elements of the data model
  trait Element
  trait SimElement extends Element
  case object SimulationModel extends SimElement
  case object SimulationRun extends SimElement
  case object NumericalOutput extends SimElement
  case object Granule extends SimElement
  // @TODO maybe needed 
  case object SimParameter extends SimElement
  trait ObsElement extends Element
  case object Observatory extends ObsElement
  case object Instrument extends ObsElement
  case object NumericalData extends ObsElement
  // @TODO maybe needed
  case object ObsParameter extends ObsElement 
  // generic message for elements
  case object GetRepository 
  case class GetElement(val dType: Element, val id: Option[String])
  case object UpdateTrees 
  
  // @TODO we need that later for updating the trees dynamically (on admin request)
  def updateTrees(provider: ActorSelection) = {
    (provider ? UpdateTrees)
  }
  
}