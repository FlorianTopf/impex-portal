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

// container for content
case class Trees(var content: Seq[NodeSeq])
case class Methods(var content: Seq[NodeSeq])

// message formats
case class GetTrees(val format: Option[String] = None)
object GetMethods
object UpdateTrees
object GetType

class DataProvider(val dataTree: Trees, val accessMethods: Methods) extends Actor {
	//println(self.path.name)
    // @TODO unified error messages
    def receive = {
        // @TODO return unified tree in XML
        case GetTrees(Some("xml")) => sender ! getTreeXMLs
        case GetTrees(None) => sender ! getTreeObjects
        case GetMethods => sender ! getMethodsXMLs
        case UpdateTrees => { 
          dataTree.content = updateTrees
          println("finished")
        }
        //case GetType => getMetaData.typeValue.get
        //case _ => sender ! Json.obj("error" -> "message not found")
        case _ => sender ! <error>message not found</error>
    }
	
	private def getMetaData: Database =
		Await.result(ConfigService.request(
		    GetDatabase(self.path.name)).mapTo[Database], 
		    1.second)
    
    private def getTreeXMLs = dataTree.content
    
    private def getMethodsXMLs = accessMethods.content
    
    private def getTreeObjects: Seq[(Databasetype, Any)] = {
      dataTree.content map { tree => 
         getMetaData.typeValue.get match {
            case Simulation => (Simulation, scalaxb.fromXML[Spase](tree))
            case Observation => (Observation, scalaxb.fromXML[DataRoot](tree))
          }
      }
    }
    
    private def updateTrees: Seq[NodeSeq] = {
      val dns: String = getMetaData.databaseoption.head.value
      val treeURLs: Seq[String] = UrlProvider.getUrls(dns, getMetaData.asInstanceOf[Database].tree)
      treeURLs flatMap { 
          treeURL => 
        	val promise = WS.url(treeURL).get()
        	try {
        	  Some(Await.result(promise, 1.minute).xml)
        	} catch {
        	  case e: TimeoutException => None
        	}
        }
    }
    
}

object DataProvider {
    implicit val timeout = Timeout(1 second)
    
    // @TODO we need that later for updating the trees dynamically
    def updateTrees(provider: String) = {
    	val actor: ActorRef = Akka.system.actorFor("user/registry"+provider)
    	Await.result((actor ? UpdateTrees), Duration.Inf)
    }
    
}
