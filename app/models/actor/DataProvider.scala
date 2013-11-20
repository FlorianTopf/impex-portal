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
object GetRepository
object UpdateTrees

// @TODO provide a basic trait and then two for each provider (obs/sim)
trait Provider {
  val dataTree: Trees
  val accessMethods: Methods
  val dbType: Databasetype
  
  // predefined methods
  protected def getTreeXML = dataTree.content
  protected def getMethodsXML = accessMethods.content
}

class DataProvider(val dataTree: Trees, val accessMethods: Methods, 
    val dbType: Databasetype) extends Actor {
	//println(self.path.name)
    // @TODO unified error messages
    def receive = {
        // @TODO return unified tree in XML
        case GetTrees(Some("xml")) => sender ! getTreeXML
        case GetTrees(None) => sender ! getTreeObjects
        case GetMethods => sender ! getMethodsXML
        case GetRepository => sender ! getRepository
        case UpdateTrees => { 
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
	          repository => (dbType, scalaxb.fromXML[Repository](repository.value.asInstanceOf[NodeSeq]))
	        }
	      }
	      case Observation => {
	        val repository = tree._2.asInstanceOf[DataRoot].dataCenter
	        Seq((dbType, DataCenter(Nil, repository.desc, repository.name, repository.id)))
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
        	  Some(Await.result(promise, 1.minute).xml)
        	} catch {
        	  case e: TimeoutException => None
        	}
        }
    }
    
}

// @TODO maybe move more stuff here
object DataProvider {
    implicit val timeout = Timeout(10 seconds)
    
    def getTreeXML(provider: ActorRef) = {
      (provider ? GetTrees(Some("xml"))).mapTo[Seq[NodeSeq]]
    }
    
    def getMethodsXML(provider: ActorRef) = {
      (provider ? GetMethods).mapTo[Seq[NodeSeq]]
    }
    
    def getRepository(provider: ActorRef) = {
      (provider ? GetRepository).mapTo[Seq[(Databasetype, Any)]]
    }
    
    // @TODO we need that later for updating the trees dynamically (on admin request
    def updateTrees(provider: ActorRef) = {
    	(provider ? UpdateTrees)
    }
    
}
