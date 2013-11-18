package models.actor

import scala.concurrent._
import scala.concurrent.duration._
import models.binding._
import akka.actor._
import play.libs._
import akka.pattern._
import akka.util.Timeout
import scala.xml.NodeSeq
import scala.language.postfixOps
import play.api.libs.concurrent.Execution.Implicits._

// message formats
trait RegistryMessage
case class RegisterProvider(val props: Props, val name: String) extends RegistryMessage
case class GetProviderTree(val name: Option[String]) extends RegistryMessage
case class GetRepositories(val databases: Seq[(String, Database)]) extends RegistryMessage

class RegistryService extends Actor {
 implicit val timeout = Timeout(10 seconds)
   // @TODO unified error messages
   def receive = {
     case reg: RegisterProvider => sender ! register(reg)
     //case _ => sender ! Json.obj("error" -> "message not found")
     case _ => sender ! <error>message not found in registry</error>
  }
  
  private def register(msg: RegisterProvider) = {
    val provider: ActorRef = context.actorOf(msg.props, msg.name)
    // @TODO initial delay will be switched later
    Akka.system.scheduler.schedule(5.minutes, 24.hours, provider, UpdateTrees)
  }
}

// @TODO we must provider exception handling here! => timeouts!
object RegistryService {
  implicit val timeout = Timeout(1 minute)
  
  private val registry: ActorRef = Akka.system.actorFor("user/registry")
  
  private def getChild(name: String): ActorRef = {
    Akka.system.actorFor(registry.path.child(name))
  }
  
  def registerChild(props: Props, name: String) = {
    (registry ? RegisterProvider(props, name))
  }

  // @TODO merge multiple trees here (in xml)
  def getTreeXML(pName: Option[String] = None): Future[Seq[NodeSeq]] = {
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Map[String, Database]]
      provider <- { 
        pName match {
          case Some(p: String) if databases.contains(p) =>
            val provider: ActorRef = getChild(pName.get)
            DataProvider.getTreeXML(provider)
          //case None => // get all dataproviders
          case _ => future { <error>provider not found</error> }
        }
      }
    } yield provider
  }
  
  def getRepository(pName: String): Future[Seq[(Databasetype, Any)]] = {
    val provider: ActorRef = getChild(pName)
    DataProvider.getRepository(provider)
  }
  
  //@TODO this routine needs to be improved (no Await if possible)
  def getRepositories: Future[Seq[(Databasetype, Any)]] = {
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Map[String, Database]]
      provider <- future {
        databases.toSeq flatMap { database =>
          val provider: ActorRef = getChild(database._1)
          Await.result(DataProvider.getRepository(provider), 10.seconds)
        }
      }
    } yield provider
  } 
  
}