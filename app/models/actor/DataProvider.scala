package models.actor

import models.binding._
import play.api._
import play.api.libs.json._
import akka.actor._
import scala.concurrent._
import scala.concurrent.duration._
import play.api.libs.ws._
import scala.xml._
import models.provider._
import scala.language.postfixOps
import akka.util.Timeout
import play.libs.Akka
import akka.pattern.ask
import play.api.libs.concurrent.Execution.Implicits._

// container for XML content
case class Trees(var content: Seq[NodeSeq])
case class Methods(var content: Seq[NodeSeq])

// @TODO provide a basic trait and then two for each provider (obs/sim)
trait Provider {
  val dataTree: Trees
  val accessMethods: Methods
  val dbType: Databasetype

  // predefined methods
  protected def getTreeXML = dataTree.content
  protected def getMethodsXML = accessMethods.content
}

//@TODO integrate scheduled dump to hard disk, and use this then! block updating!
class DataProvider(val dataTree: Trees, val accessMethods: Methods,
  val dbType: Databasetype) extends Actor {
  import models.actor.DataProvider._
  import models.actor.ConfigService._
  
  //println(self.path.name)
  // @TODO unified error messages
  def receive = {
    // @TODO return unified tree in XML
    case GetTrees(Some("xml")) => sender ! getTreeXML
    case GetTrees(None) => sender ! getTreeObjects
    case GetMethods => sender ! getMethodsXML
    case GetRepository => sender ! getRepository
    case UpdateTrees => {
      //@TODO integrate exception handling here
      dataTree.content = updateTrees
      println("finished")
    }
    //case _ => sender ! Json.obj("error" -> "message not found")
    case _ => sender ! <error>message not found in data provider</error>
  }

  private def getTreeXML = dataTree.content
  private def getMethodsXML = accessMethods.content

  // @TODO improve this!
  private def getMetaData: Database =
    Await.result(ConfigService.request(
      GetDatabase(self.path.name)).mapTo[Database],
      1.second)

  private def getTreeObjects: Seq[(Databasetype, Any)] = {
    dataTree.content map { tree =>
      dbType match {
        case Simulation => (dbType, scalaxb.fromXML[Spase](tree))
        case Observation => (dbType, scalaxb.fromXML[DataRoot](tree))
      }
    }
  }

  private def getRepository: Seq[(Databasetype, Any)] = {
    getTreeObjects flatMap { tree =>
      dbType match {
        case Simulation => {
          tree._2.asInstanceOf[Spase].ResourceEntity.filter(c => c.key.get == "Repository") map {
            //@TODO still the same problem with some XML elements
            repo => (dbType, scalaxb.fromXML[Repository](repo.value.asInstanceOf[NodeSeq]))
          }
        }
        case Observation => {
          tree._2.asInstanceOf[DataRoot].dataCenter map {
            repo => (dbType, DataCenter(repo.datacenteroption, repo.available, repo.desc,
                repo.group, repo.id1, repo.isSimulation, repo.name, repo.id))
          }
        }
      }
    }
  }

  private def updateTrees: Seq[NodeSeq] = {
    val dns: String = getMetaData.databaseoption.head.value
    val treeURLs: Seq[String] = UrlProvider.getUrls(dns, getMetaData.tree)
    treeURLs flatMap {
      treeURL =>
        val promise = WS.url(treeURL).get()
        try {
          Await.result(promise, 1.minute).xml
        } catch {
          case e: TimeoutException =>
            scala.xml.XML.loadFile(
              PathProvider.getTreePath(treeURL, getMetaData.name))
        }
    }
  }

}

object DataProvider {
  implicit val timeout = Timeout(10 seconds)

  // message formats
  case class GetTrees(val format: Option[String] = None)
  case object GetMethods
  case object GetRepository
  case object UpdateTrees

  def getTreeXML(provider: ActorSelection) = {
    (provider ? GetTrees(Some("xml"))).mapTo[Seq[NodeSeq]]
  }

  def getMethodsXML(provider: ActorSelection) = {
    (provider ? GetMethods).mapTo[Seq[NodeSeq]]
  }

  def getRepository(provider: ActorSelection) = {
    (provider ? GetRepository).mapTo[Seq[(Databasetype, Any)]]
  }

  // @TODO we need that later for updating the trees dynamically (on admin request)
  def updateTrees(provider: ActorSelection) = {
    (provider ? UpdateTrees)
  }

}
