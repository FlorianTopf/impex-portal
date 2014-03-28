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

  // @TODO check if child exists and is alive
  private def getChild(name: String): ActorSelection = {
    Akka.system.actorSelection("user/registry/" + name)
  }
  
  // @TODO improve this!
  private def getElement(msg: GetElement, parent: Option[String]): Future[Either[Spase, RequestError]] = {
    implicit val timeout = Timeout(30.seconds)
    
    println("Requested ID: "+msg.id)
    
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- (msg.id, parent) match {
         case (Some(id), Some(p)) if (id.contains(p) && databases.exists(_.id.toString == p)) => {
          val db: Database = databases.find(_.id.toString == p).get
          val provider: ActorSelection = getChild(db.name)
          (provider ? msg).mapTo[Spase] map { entry => Left(entry) }
        }
        case (Some(id), None) if(databases.exists(d => id.contains(d.id.toString))) => {
          val db: Database = databases.find(d => id.contains(d.id.toString)).get
          val provider: ActorSelection = getChild(db.name)
          (provider ? msg).mapTo[Spase] map { entry => Left(entry) }
        }
        case (None, Some(p)) if(databases.exists(d => p.contains(d.id.toString))) => {
          val db: Database = databases.find(d => p.contains(d.id.toString)).get
          val provider: ActorSelection = getChild(db.name)
          (provider ? msg).mapTo[Spase] map { entry => Left(entry) }
        } 
        case (None, None) => {
          val result = msg.dType match {
            case m: SimElement => Future.sequence(getChilds(databases.filter(_.typeValue == Simulation)) map { 
            provider => (provider ? msg).mapTo[Spase] map { entry => entry.ResourceEntity }
            })
            case m: ObsElement => Future.sequence(getChilds(databases.filter(_.typeValue == Observation)) map { 
            provider => (provider ? msg).mapTo[Spase] map { entry => entry.ResourceEntity }
            })     
          }
          result.map(records => Left(Spase(Number2u462u462, records.flatten, "en"))) 
        }
        case _ => future { Right(RequestError(ERequestError.UNKNOWN_ENTITY)) }
      }
    } yield provider 
  }
   
  def registerChild(props: Props, name: String) = {
    (registry ? RegisterProvider(props, name))
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
  
  def getRepository(id: Option[String] = None): Future[Either[Spase, RequestError]] = {
    implicit val timeout = Timeout(10.seconds)
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <-id match {
        case Some(id) if databases.exists(d => id.contains(d.id.toString)) => {
          val provider: ActorSelection = getChild(databases.find(d => id.contains(d.id.toString)).get.name)
          (provider ? GetRepository).mapTo[Spase] map { entry => Left(entry) }
        }
        case None => {
          val result = Future.sequence(getChilds(databases) map { provider =>
            (provider ? GetRepository).mapTo[Spase] map { entry => entry.ResourceEntity }
          })
          result.map(records => Left(Spase(Number2u462u462, records.flatten, "en")))
        } 
        case _ => future { Right(RequestError(ERequestError.UNKNOWN_PROVIDER)) }
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

  def getSimulationModel(id: Option[String], repository: Option[String]): Future[Either[Spase, RequestError]] =
    getElement(GetElement(SimulationModel, id), repository)
  
  def getSimulationRun(id: Option[String], model: Option[String]): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(SimulationRun, id), model)
  
  def getNumericalOutput(id: Option[String], run: Option[String]): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(NumericalOutput, id), run)
  
  def getGranule(id: Option[String], output: Option[String]): Future[Either[Spase, RequestError]] =
    getElement(GetElement(Granule, id), output)
  
  // @TODO this doesn't work, because we do not encode the provider reference in the id
    // maybe we create an artificial id? e.g. impex://AMDA/AMDA/Cassini_Public?
  def getObservatory(id: Option[String], repository: Option[String]): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(Observatory, id), repository)
  
  def getInstrument(id: Option[String], observatory: Option[String]): Future[Either[Spase, RequestError]] = ???
  
  def getNumericalData(id: Option[String], instrument: Option[String]): Future[Either[Spase, RequestError]] = ???
  
}