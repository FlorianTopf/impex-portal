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
  
  override def preStart = initChildActors

  def receive = {
    case reg: RegisterProvider => sender ! register(reg.props, reg.id)
  } 

  private def register(props: Props, id: String) = {
    val provider: ActorRef = context.actorOf(props, id)
    // @TODO initial delay will be switched later
    Akka.system.scheduler.schedule(30.minutes, 24.hours, provider, UpdateData)
  }
  
  private def initChildActors: Unit = {
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
  import models.binding.Simulation
  
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
  
  // Hack method for checking the id of the request against the database id (add to diagram)
  private def checkId(msg: GetElement, id: URI): Boolean = { 
    println("Computed ResourceID match="+id.toString.replaceAll(ERepository.toString, msg.dType.toString))
    msg.id.toString.contains(id.toString.replaceAll(ERepository.toString, msg.dType.toString))
  }
  
  private def getElement(msg: GetElement): Future[Either[Spase, RequestError]] = {
    implicit val timeout = Timeout(1.minute)
    println("ResourceID="+msg.id)
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- msg.id match {
        case Some(id) if(databases.exists(d => checkId(msg, d.id))) => {
          val db: Database = databases.find(d => checkId(msg, d.id)).get
          val provider: ActorSelection = getChild(db.id)
          (msg.dType, db.typeValue) match {
            case (e: SimElement, Simulation) => (provider ? msg).mapTo[Spase] map { Left(_) }
            case (e: ObsElement, Observation) => (provider ? msg).mapTo[Spase] map { Left(_) }
            case (e: GenElement, _) => (provider ? msg).mapTo[Spase] map { Left(_) }
            case _ => future { Right(RequestError(ERequestError.UNKNOWN_ENTITY)) }
          }   
        }
        case None => {
          val result = msg.dType match {
            case e: SimElement => Future.sequence(getChilds(databases.filter(_.typeValue == Simulation)) map { 
            	provider => (provider ? msg).mapTo[Spase] map { _.ResourceEntity }
            })
            case e: ObsElement => Future.sequence(getChilds(databases.filter(_.typeValue == Observation)) map { 
            	provider => (provider ? msg).mapTo[Spase] map { _.ResourceEntity }
            })
            case e: GenElement => Future.sequence(getChilds(databases) map { 
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
    implicit val timeout = Timeout(1.minute)
    for {
      databases <- ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- {
        id match {
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
  def getSimulationModel(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = {
    // Hack to check if a repository id is provided as query parameter =>
    // @TODO maybe add this to all methods (so that repository id can be used, update Definition/Swagger)
    id match {
      // @FIXME Temporary Hack for SINP (will be done after resourceId changes)
      case Some(id) if(id.contains(ERepository.toString+"/PMM")) => {
        getElement(GetElement(ESimulationModel, Some(id.replace(ERepository.toString+"/PMM", ESimulationModel.toString)), r))
      }
      case Some(id) if(id.contains(ERepository.toString)) => {
        getElement(GetElement(ESimulationModel, Some(id.replace(ERepository.toString, ESimulationModel.toString)), r))
      }
      case _ => getElement(GetElement(ESimulationModel, id, r))  
    }
  }
  
  def getSimulationRun(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(ESimulationRun, id, r))
  
  def getNumericalOutput(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(ENumericalOutput, id, r))
  
  def getGranule(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] =
    getElement(GetElement(EGranule, id, r))
  
  // observations methods
  def getObservatory(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(EObservatory, id))
  
  def getInstrument(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(EInstrument, id))
  
  def getNumericalData(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = 
    getElement(GetElement(ENumericalData, id))
  
}