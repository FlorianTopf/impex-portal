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
import java.net.URI
import scalaxb.DataRecord
import models.enums._

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
  import models.actor.DataProvider._
  
  implicit val timeout = Timeout(1.minute)
  
  // message formats
  trait RegistryMessage
  case class RegisterProvider(val props: Props, val name: String) extends RegistryMessage
  case class GetProviderTree(val name: Option[String]) extends RegistryMessage
  case class GetRepositories(val databases: Seq[(String, Database)]) extends RegistryMessage

  private val registry: ActorSelection = Akka.system.actorSelection("user/registry")

  private def getChilds(databases: Seq[Database]): Seq[ActorSelection] = {
    databases map { database => getChild(database.name) }
  }
  
  def registerChild(props: Props, name: String) = {
    (registry ? RegisterProvider(props, name))
  }

  // @TODO check if child exists and is alive
  private def getChild(name: String): ActorSelection = {
    Akka.system.actorSelection("user/registry/" + name)
  }

  // @TODO merge multiple trees here (in xml)
  def getTreeXML(pName: Option[String] = None): Future[Seq[NodeSeq]] = {
    implicit val timeout = Timeout(10.seconds)
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- {
        pName match {
          case Some(p: String) if databases.exists(d => d.name == p) => {
            val provider: ActorSelection = getChild(p)
            (provider ? GetTrees(Some("xml"))).mapTo[Seq[NodeSeq]]
          }
          case _ => future { Seq(<error>provider not found</error>) }
        }
      }
    } yield provider
  }

  def getMethodsXML(pName: String): Future[Seq[NodeSeq]] = {
    implicit val timeout = Timeout(10.seconds)
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- {
        pName match {
          case p: String if databases.exists(d => d.name == p) => {
            val provider: ActorSelection = getChild(p)
            (provider ? GetMethods).mapTo[Seq[NodeSeq]]
          }
          case _ => future { Seq(<error>provider not found</error>) }
        }
      }
    } yield provider
  }
  
  def getRepository(pName: Option[String] = None): Future[Either[Spase, RequestError]] = {
    implicit val timeout = Timeout(10.seconds)
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- pName match {
        case Some(p) if databases.exists(d => d.name == p) => {
          val provider: ActorSelection = getChild(p)
          (provider ? GetRepository).mapTo[Spase] map { entry => Left(entry)}
        }
        case None => {
          val result = Future.sequence(getChilds(databases) map { provider =>
            (provider ? GetRepository).mapTo[Spase] map { entry => entry.ResourceEntity }
          })
          result.map(records => Left(Spase(Number2u462u462, records.flatten, "en")))
        } 
        case _ => future { Right(RequestError(ERequestError.UNKOWN_PROVIDER)) }
      }
    } yield provider
  }

  def getRepositoryType(dbType: Databasetype): Future[Spase] = {
    implicit val timeout = Timeout(10.seconds)
    for {
      databases <- ConfigService.request(GetDatabaseType(dbType)).mapTo[Seq[Database]]
      providers <- { 
        val result = Future.sequence(getChilds(databases) map { provider =>
        	(provider ? GetRepository).mapTo[Spase] map { entry => entry.ResourceEntity }
        })
        result.map(records => Spase(Number2u462u462, records.flatten, "en"))
      }
    } yield providers
  }
  
  def getSimulationModel(id: Option[URI], repository: Option[URI]): Seq[SimulationModel] = ???
  
  def getSimulationRun(id: Option[URI], model: Option[URI]): Seq[SimulationRun] = ???
  
  def getNumericalOutput(id: Option[URI], output: Option[URI]): Seq[NumericalOutput] = ???
  
  def getGranule(id: Option[URI], run: Option[URI]): Seq[Granule] = ???
  
  def getObservatory = ???
  
  def getInstrument = ???
  
  def getNumericalData = ???
  
}