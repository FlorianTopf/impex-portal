package models.actor

import models.binding._
import models.enums._
import models.provider._
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
  
  // Hack method for checking the id of the request against the database id 
  private def validateId(msg: GetElement, dbId: URI): Boolean = { 
    val msgId = msg.id.get
    .replace(ESimulationModel.toString, ERepository.toString)
    .replace(ESimulationRun.toString, ERepository.toString)
    .replace(ENumericalOutput.toString, ERepository.toString)
    .replace(EGranule.toString, ERepository.toString)
    .replace(EObservatory.toString, ERepository.toString)
    .replace(EInstrument.toString, ERepository.toString)
    .replace(ENumericalData.toString, ERepository.toString)
    //println("Computed search Id="+msgId)
    msgId.contains(dbId.toString)
  }
  
  private def getElement(msg: GetElement): Future[Either[Spase, RequestError]] = {
    implicit val timeout = Timeout(1.minute)
    //println("ResourceID="+msg.id)
    for {
      databases <- ConfigService.request(GetRegistryDatabases).mapTo[Seq[Database]]
      provider <- msg.id match {
        case Some(id) if(databases.exists(d => validateId(msg, d.id))) => {
          val db: Database = databases.find(d => validateId(msg, d.id)).get
          val provider: ActorSelection = getChild(db.id)
          (msg.dType, db.typeValue) match {
            case (e: SimElement, Simulation) => (provider ? msg).mapTo[Spase] map { 
              s => { s.ResourceEntity match {
                case Seq() => Right(RequestError(ERequestError.UNKNOWN_ENTITY))
                case _ => Left(s)
              }}  
            }
            case (e: ObsElement, Observation) => (provider ? msg).mapTo[Spase] map { 
              s => { s.ResourceEntity match {
                case Seq() => Right(RequestError(ERequestError.UNKNOWN_ENTITY))
                case _ => Left(s)
              }}  
            }
            // generic element must always exist
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
        case _ => future { Right(RequestError(ERequestError.UNKNOWN_PROVIDER)) }
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
      databases <- ConfigService.request(GetRegistryDatabases).mapTo[Seq[Database]]
      provider <- {
        id match {
          case Some(id) if databases.exists(d => id.contains(d.id.toString)) => {
            val provider: ActorSelection = getChild(databases.find(d => id.contains(d.id.toString)).get.id)
            (provider ? GetTree).mapTo[Spase] map { Left(_) }
          }
          // give correct error in the interface
          case Some(id) if databases.exists(d => id.contains(d.id.toString) && d.typeValue == Observation) => {
            future { Right(RequestError(ERequestError.NOT_IMPLEMENTED)) }
          }
          case None => { 
            val result = Future.sequence(getChilds(databases) map { provider =>
        	 	(provider ? GetTree).mapTo[Spase] map { _.ResourceEntity }
        	})
        	result.map(records => Left(Spase(Number2u462u462, records.flatten, "en")))
          }
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
    // check if a repository id is given as parameter
    id match {
      case Some(id) if(id.contains(ERepository.toString)) => {
        getElement(GetElement(ESimulationModel, Some(id.replace(ERepository.toString, ESimulationModel.toString)), r))
      }
      case _ => getElement(GetElement(ESimulationModel, id, r))  
    }
  }
  
  def getSimulationRun(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] =  {
    // check if a repository id is given as parameter
    id match {
      case Some(id) if(id.contains(ERepository.toString)) => {
        getElement(GetElement(ESimulationRun, Some(id.replace(ERepository.toString, ESimulationRun.toString)), r))
      }
      case _ => getElement(GetElement(ESimulationRun, id, r))
    }
  }
    
  
  def getNumericalOutput(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = {
    // check if a repository id is given as parameter
    id match {
      case Some(id) if(id.contains(ERepository.toString)) => {
        getElement(GetElement(ENumericalOutput, Some(id.replace(ERepository.toString, ENumericalOutput.toString)), r))
      }
      case _ => getElement(GetElement(ENumericalOutput, id, r))
    }
  }
  
  def getGranule(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = {
    // check if a repository id is given as parameter
    id match {
      case Some(id) if(id.contains(ERepository.toString)) => {
        getElement(GetElement(EGranule, Some(id.replace(ERepository.toString, EGranule.toString)), r))
      }
      case _ => getElement(GetElement(EGranule, id, r))
    }
  }
  
  // observations methods
  def getObservatory(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = {
    // check if a repository id is given as parameter
    id match {
      case Some(id) if(id.contains(ERepository.toString)) => {
        getElement(GetElement(EObservatory, Some(id.replace(ERepository.toString, EObservatory.toString)), r))
      }
      case _ => getElement(GetElement(EObservatory, id))
    }
  }
  
  def getInstrument(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = {
    // check if a repository id is given as parameter
    id match {
      case Some(id) if(id.contains(ERepository.toString)) => {
        getElement(GetElement(EInstrument, Some(id.replace(ERepository.toString, EInstrument.toString)), r))
      }
      case _ => getElement(GetElement(EInstrument, id))
    }
  }
  
  def getNumericalData(id: Option[String], r: Boolean): Future[Either[Spase, RequestError]] = {
    // check if a repository id is given as parameter
    id match {
      case Some(id) if(id.contains(ERepository.toString)) => {
        getElement(GetElement(ENumericalData, Some(id.replace(ERepository.toString, ENumericalData.toString)), r))
      }
      case _ => getElement(GetElement(ENumericalData, id))
    }
    
  }
  
}