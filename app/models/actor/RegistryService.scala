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
    Akka.system.scheduler.schedule(30.minutes, 24.hours, provider, UpdateTrees)
  }
}

// @TODO we must improve exception handling here! => timeouts + responses
// @TODO rebuilt access with assumptions of Week 3 lectures 
//       => maybe use try instead of future? we have no error recovery here!
object RegistryService {
  implicit val timeout = Timeout(1 minute)
  
  private val registry: ActorRef = Akka.system.actorFor("user/registry")
  
  def registerChild(props: Props, name: String) = {
    (registry ? RegisterProvider(props, name))
  }
  
  private def getChilds(databases: Seq[Database]): Seq[ActorRef] = {
    databases map { database => getChild(database.name)}
  }
  
  // @TODO check if child exists
  private def getChild(name: String): ActorRef = {
    Akka.system.actorFor(registry.path.child(name))
  }

  // @TODO merge multiple trees here (in xml)
  def getTreeXML(pName: Option[String] = None): Future[Seq[NodeSeq]] = {
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- { 
        pName match {
          case Some(p: String) if databases.exists(d => d.name == p) =>
            val provider: ActorRef = getChild(p)
            DataProvider.getTreeXML(provider)
          //case None => // get all dataproviders
          case _ => future { Seq(<error>provider not found</error>) }
        }
      }
    } yield provider
  }
  
  def getMethodsXML(pName: String): Future[Seq[NodeSeq]] = {
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- { 
        pName match {
          case p: String if databases.exists(d => d.name == p) =>
            val provider: ActorRef = getChild(p)
            DataProvider.getMethodsXML(provider)
          case _ => future { Seq(<error>provider not found</error>) }
        }
      }
    } yield provider
  }
  
  // @TODO this routine needs to be improved (no Await/Blocking if possible)
  def getRepository(pName: Option[String] = None): Future[Seq[(Databasetype, Any)]] = {
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <-
        pName match {
          case Some(p: String) if databases.exists(d => d.name == p) =>
            val provider: ActorRef = getChild(p)
            DataProvider.getRepository(provider)
          case None => future {
            getChilds(databases) flatMap { provider =>
        	Await.result(DataProvider.getRepository(provider), 10.seconds) }
          }
          case _ => future { Seq((Simulation, <error>provider not found</error>)) }
       }
    } yield provider
  }
  
  // @TODO this routine needs to be improved (no Await/Blocking if possible)
  def getRepositoryType(dType: Databasetype): Future[Seq[(Databasetype, Any)]] = {
    for {
      databases <- ConfigService.request(GetDatabaseType(dType)).mapTo[Seq[Database]]
      provider <- future {
        getChilds(databases) flatMap { provider =>
        Await.result(DataProvider.getRepository(provider), 10.seconds)
        }
      }
    } yield provider
  }
  
}