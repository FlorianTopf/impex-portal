package models.actor

import scala.concurrent._
import scala.concurrent.duration._
import models.binding._
import akka.actor._
import play.libs._
import akka.pattern.ask
import akka.util.Timeout
import scala.xml.NodeSeq
import scala.language.postfixOps
import play.api.libs.concurrent.Execution.Implicits._

// message formats
trait RegistryMessage
case class RegisterProvider(val props: Props, val name: String) extends RegistryMessage
case class GetProviderTree(val name: Option[String]) extends RegistryMessage

// @TODO attention when updating the configuration!
// @TODO improve error messages
class RegistryService extends Actor {
  implicit val timeout = Timeout(1 second)

  def receive = {
    case reg: RegisterProvider => sender ! register(reg)
    // @TODO this must be safer => string
    case GetProviderTree(Some(p)) => sender ! getTreeXML(p)
    //case GetProviderTree(None) => sender ! getTreeXMLs
    //case _ => sender ! Json.obj("error" -> "message not found")
    case _ => sender ! <error>message not found</error>
  }
  
  private def register(msg: RegisterProvider) = {
    val provider: ActorRef = context.actorOf(msg.props, msg.name)
    // @TODO initial delay will be switched later
    Akka.system.scheduler.schedule(5.minutes, 24.hours, provider, UpdateTrees)
  }
  
  private def updateTrees = {
    
  }
  
  private def getTreeXMLs = {

  }
  
  private def getTreeXML(providerName: String): Seq[NodeSeq] = {
    val provider: ActorRef = context.actorFor(providerName)
    Await.result((provider ? GetTrees(Some("xml"))).mapTo[Seq[NodeSeq]], 1.second)
  }
  
  private def getTree(providerName: String) = {
    val provider: ActorRef = context.actorFor(providerName)
    Await.result((provider ? GetTrees()).mapTo[Seq[(Databasetype, Any)]], 1.second)
  }
  
  private def getRepositories = {
    
  }
  
  
}

object RegistryService {
  implicit val timeout = Timeout(1 second)
  
  val registry: ActorRef = Akka.system.actorFor("user/registry")

  def register(props: Props, name: String) = {
    (registry ? RegisterProvider(props, name))
  }

  // @TODO this gets the data tree only in xml => and merged?
  def requestTreeXML(providerName: Option[String] = None): Future[Seq[NodeSeq]] = {
    val databases = Await.result(
        ConfigService.request(GetDatabases).mapTo[Map[String, Database]], 1.second)
    
   providerName match {
      case Some(p: String) if databases.contains(p) => {
          (registry ? GetProviderTree(providerName)).mapTo[Seq[NodeSeq]]
      }
      //case None => // get all dataproviders
      case _ => future { <error>provider not found</error> }
    }
  }

}