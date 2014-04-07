package models.actor

import models.binding._
import models.enums._
import play.libs._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent._
import scala.concurrent.duration._
import scala.xml._
import akka.actor._
import akka.pattern._
import akka.util.Timeout
import akka.actor.SupervisorStrategy._
import scalaxb.DataRecord
import java.net.URI
import models.provider._
import play.api.libs.ws._

class RegistryService(val databases: Seq[Database]) extends Actor {  
  import models.actor.RegistryService._
  import models.actor.DataProvider._
  
  implicit val timeout = Timeout(10.seconds)
  
  override val supervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException => Stop
    case _: ActorKilledException => Stop
    case _: Exception => Restart
  }
  
  override def preStart = initChildActors(databases)

  def receive = {
    case reg: RegisterProvider => sender ! register(reg.props, reg.id)
  } 

  private def register(props: Props, id: String) = {
    val provider: ActorRef = context.actorOf(props, id)
    // @TODO initial delay will be switched later
    Akka.system.scheduler.schedule(5.minutes, 24.hours, provider, UpdateData)
  }
  
  private def initChildActors(databases: Seq[Database]) = {
    databases map { database =>
      val id: String = UrlProvider.encodeURI(database.id)
      
      database.typeValue match {
        case Simulation => register(
            Props(new SimDataProvider(database)), id)
        case Observation => register(
            Props(new ObsDataProvider(database)), id)
      }
    }
  }
  
}

object RegistryService {
  import models.actor.ConfigService._
  import models.actor.DataProvider._
  
  implicit val timeout = Timeout(1.minute)
  
  // message formats
  trait RegistryMessage
  case class RegisterProvider(val props: Props, val id: String) extends RegistryMessage

  private val registry: ActorSelection = Akka.system.actorSelection("user/registry")

  private def getChilds(databases: Seq[Database]): Seq[ActorSelection] = {
    databases map { d => getChild(d.id) }
  }

  // @TODO check if child exists and is alive
  private def getChild(id: URI): ActorSelection = {
    Akka.system.actorSelection("user/registry/" + UrlProvider.encodeURI(id))
  }
  
  private def getElement(msg: GetElement): Future[Either[Spase, RequestError]] = {
    implicit val timeout = Timeout(30.seconds)
    println("ResourceID="+msg.id)
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- msg.id match {
        case Some(id) if(databases.exists(d => id.contains(d.id.toString))) => {
          val db: Database = databases.find(d => id.contains(d.id.toString)).get
          val provider: ActorSelection = getChild(db.id)
          (provider ? msg).mapTo[Spase] map { Left(_) }
        }
        case None => {
          val result = msg.dType match {
            case m: SimElement => Future.sequence(getChilds(databases.filter(_.typeValue == Simulation)) map { 
            	provider => (provider ? msg).mapTo[Spase] map { _.ResourceEntity }
            })
            case m: ObsElement => Future.sequence(getChilds(databases.filter(_.typeValue == Observation)) map { 
            	provider => (provider ? msg).mapTo[Spase] map { _.ResourceEntity }
            })
            case r: Element => Future.sequence(getChilds(databases) map { 
            	provider => (provider ? msg).mapTo[Spase] map { _.ResourceEntity }
            })
          }
          result.map(records => Left(Spase(Number2u462u462, records.flatten, "en"))) 
        }
        case _ => future { Right(RequestError(ERequestError.UNKNOWN_ENTITY)) }
      }
    } yield provider 
  }
   
  def registerChild(props: Props, id: String) = {
    (registry ? RegisterProvider(props, id))
  } 
 
  // general methods
  def getTree(id: Option[String] = None): Future[Either[Spase, RequestError]] = {
    implicit val timeout = Timeout(10.seconds)
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- {
        id match {
          // @TODO improve this check everywhere
          // @FIXME this is only available for simulations for now!
          case Some(id) if databases.exists(d => id.contains(d.id.toString) && d.typeValue == Simulation) => {
            val provider: ActorSelection = getChild(databases.find(d => id.contains(d.id.toString)).get.id)
            (provider ? GetTree).mapTo[Spase] map { Left(_) }
          }
          // give correct error in the interface
          case Some(id) if databases.exists(d => id.contains(d.id.toString) && d.typeValue == Observation) => {
            future { Right(RequestError(ERequestError.NOT_IMPLEMENTED)) }
          }
          // @FIXME this is only available for simulations for now!
          case None => { 
            val result = Future.sequence(getChilds(databases.filter(d => d.typeValue == Simulation)) map { provider =>
        	 	(provider ? GetTree).mapTo[Spase] map { _.ResourceEntity }
        	})
        	result.map(records => Left(Spase(Number2u462u462, records.flatten, "en")))
          }
          case _ => future { Right(RequestError(ERequestError.UNKNOWN_PROVIDER)) }
        }
      }
    } yield provider
  }

  def getMethods(id: Option[String] = None): Future[Either[Seq[NodeSeq], RequestError]] = {
    implicit val timeout = Timeout(10.seconds)
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- {
        id match {
          case Some(id) if databases.exists(d => id.contains(d.id.toString)) => {
            val provider: ActorSelection = getChild(databases.find(d => id.contains(d.id.toString)).get.id)
            (provider ? GetMethods).mapTo[Seq[NodeSeq]] map { Left(_) } 
          }
          // @TODO what do we do if None is given?
          case _ => future { Right(RequestError(ERequestError.UNKNOWN_PROVIDER)) }
        }
      }
    } yield provider
  }
  
  def getRepository(id: Option[String] = None): Future[Either[Spase, RequestError]] =
    getElement(GetElement(ERepository, id))

  def getRepositoryType(dbType: Databasetype): Future[Spase] = {
    implicit val timeout = Timeout(10.seconds)
	for {
	  databases <- ConfigService.request(GetDatabaseType(dbType)).mapTo[Seq[Database]]
	  providers <- { 
	    val result = Future.sequence(getChilds(databases) map { provider =>
	     	(provider ? GetElement(ERepository, None)).mapTo[Spase] map { _.ResourceEntity }
	    })
	    result.map(records => Spase(Number2u462u462, records.flatten, "en"))
	  }
	} yield providers
  }

  // simulations methods
  def getSimulationModel(id: Option[String], recursive: String): Future[Either[Spase, RequestError]] =
    getElement(GetElement(ESimulationModel, id, recursive.toBoolean))
  
  def getSimulationRun(id: Option[String], recursive: String): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(ESimulationRun, id, recursive.toBoolean))
  
  def getNumericalOutput(id: Option[String], recursive: String): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(ENumericalOutput, id, recursive.toBoolean))
  
  def getGranule(id: Option[String], recursive: String): Future[Either[Spase, RequestError]] =
    getElement(GetElement(EGranule, id, recursive.toBoolean))
  
  // observations methods
  // @TODO add recursive functionality
  def getObservatory(id: Option[String]): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(EObservatory, id))
  
  def getInstrument(id: Option[String]): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(EInstrument, id))
  
  def getNumericalData(id: Option[String]): Future[Either[Spase, RequestError]] = ???
    //getElement(GetElement(ENumericalData, id))
  
}