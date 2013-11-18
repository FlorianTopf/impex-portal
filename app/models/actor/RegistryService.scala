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
case class GetRepositories(val databases: Map[String, Database]) extends RegistryMessage

// @TODO attention when updating the configuration!
// @TODO improve error messages
class RegistryService extends Actor {
 implicit val timeout = Timeout(10 seconds)

  def receive = {
    case reg: RegisterProvider => sender ! register(reg)
    case GetRepositories(dbs: Map[String, Database]) => sender ! getRepositories(dbs)
    //case _ => sender ! Json.obj("error" -> "message not found")
    case _ => sender ! <error>message not found</error>
  }
  
  private def register(msg: RegisterProvider) = {
    val provider: ActorRef = context.actorOf(msg.props, msg.name)
    // @TODO initial delay will be switched later
    Akka.system.scheduler.schedule(5.minutes, 24.hours, provider, UpdateTrees)
  }

  // gets repositories and datacenters (move getRepository to DataProvider?)
  private def getRepositories(databases: Map[String, Database]) = {
   databases map {database =>
    val provider: ActorRef = context.actorFor(database._2.name)
    Await.result((provider ? GetTrees()).mapTo[Seq[(Databasetype, Any)]], 10.seconds)}
  }
  
}

// @TODO we must provider exception handling here! => timeouts!
object RegistryService {
  implicit val timeout = Timeout(1 minute)
  
  val registry: ActorRef = Akka.system.actorFor("user/registry")
  
  def register(props: Props, name: String) = {
    (registry ? RegisterProvider(props, name))
  }

  // @TODO merge multiple trees here (in xml)
  def getTreeXML(providerName: Option[String] = None): Future[Seq[NodeSeq]] = {
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Map[String, Database]]
      provider <- { 
        providerName match {
          case Some(p: String) if databases.contains(p) =>
          //@TODO maybe improve this path
          val provider: ActorRef = Akka.system.actorFor("user/registry/"+providerName.get)
          (provider ? GetTrees(Some("xml"))).mapTo[Seq[NodeSeq]]
          //case None => // get all dataproviders
          case _ => future { <error>provider not found</error> }
        }
      }
    } yield provider
  }
  
  def getTree(providerName: String) = {
    //@TODO maybe improve this path
    val provider: ActorRef =  Akka.system.actorFor("user/registry/"+providerName)
    (provider ? GetTrees()).mapTo[Seq[(Databasetype, Any)]]
  }
  
  def getRepositories = {
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Map[String, Database]]
      provider <-  { 
        (registry ? GetRepositories(databases)).mapTo[Seq[(Databasetype, Any)]]
      }
    } yield provider
  } 
  
}