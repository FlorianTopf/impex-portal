package models.actor

import models.binding._
import play.libs._
import scala.concurrent._
import scala.concurrent.duration._
import scala.xml._
import akka.actor._
import akka.actor.SupervisorStrategy._
import akka.pattern._
import akka.util.Timeout

import play.api.libs.concurrent.Execution.Implicits._

class RegistryService extends Actor {  
  import models.actor.RegistryService._
  import models.actor.DataProvider._
  
  implicit val timeout = Timeout(10.seconds)
  
  override val supervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException => Stop
    case _: ActorKilledException => Stop
    case _: Exception => Restart
  }

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

// @TODO we must improve exception handling here! 
//       => timeouts + responses => use promises here!
// @TODO refactor access methods!
//       => maybe use try instead of future? we have no error recovery here!
object RegistryService {
  import models.actor.ConfigService._
  
  implicit val timeout = Timeout(1.minute)
  
  // message formats
  trait RegistryMessage
  case class RegisterProvider(val props: Props, val name: String) extends RegistryMessage
  case class GetProviderTree(val name: Option[String]) extends RegistryMessage
  case class GetRepositories(val databases: Seq[(String, Database)]) extends RegistryMessage

  private val registry: ActorSelection = Akka.system.actorSelection("user/registry")
  
  def registerChild(props: Props, name: String) = {
    (registry ? RegisterProvider(props, name))
  }

  private def getChilds(databases: Seq[Database]): Seq[(Databasetype, ActorSelection)] = {
    databases map { database => (database.typeValue, getChild(database.name)) }
  }

  // @TODO check if child exists and is alive
  private def getChild(name: String): ActorSelection = {
    Akka.system.actorSelection("user/registry/" + name)
  }

  // @TODO merge multiple trees here (in xml)
  def getTreeXML(pName: Option[String] = None): Future[Seq[NodeSeq]] = {
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- {
        pName match {
          case Some(p: String) if databases.exists(d => d.name == p) => {
            val provider: ActorSelection = getChild(p)
            DataProvider.getTreeXML(provider)
          }
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
          case p: String if databases.exists(d => d.name == p) => {
            val provider: ActorSelection = getChild(p)
            DataProvider.getMethodsXML(provider)
          }
          case _ => future { Seq(<error>provider not found</error>) }
        }
      }
    } yield provider
  }

  def getRepository(pName: String): Future[Seq[Any]] = {
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- pName match {
        case p: String if databases.exists(d => d.name == p && d.typeValue == Simulation) => {
          val provider: ActorSelection = getChild(p)
          DataProvider.getRepository(provider, Simulation)
        }
        case p: String if databases.exists(d => d.name == p && d.typeValue == Observation) => {
          val provider: ActorSelection = getChild(p)
          DataProvider.getRepository(provider, Observation)
        }
        case _ => future { Seq(<error>provider not found</error>) }
      }
    } yield provider
  }

  def getRepositoryType(dbType: Databasetype): Future[Seq[Any]] = {
    for {
      databases <- ConfigService.request(GetDatabaseType(dbType)).mapTo[Seq[Database]]
      providers <- Future.sequence(getChilds(databases) map { provider =>
        DataProvider.getRepository(provider._2, dbType)
      })
    } yield providers.flatten
  }

}