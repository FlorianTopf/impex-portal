package models.actor

import models.binding._
import models.enums._
import play.api.Play._
import play.api.libs.json._
import play.api.libs.concurrent._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._
import scala.xml._
import java.net.URI


class ConfigService extends Actor {
  import models.actor.ConfigService._
  
  def receive = {
    case GetConfig(fmt: String) => fmt.toLowerCase match {
      case "xml" => sender ! getConfigXML
      case "json" => sender ! getConfigJSON
      case _ => sender ! Json.toJson(RequestError(ERequestError.UNKNOWN_MSG))
    }
    // used to init actors and return WSDLs
    case GetDatabases => sender ! getDatabases
    // used for registry service
    case GetRegistryDatabases => sender ! getPortalDatabases
    case GetDatabaseType(dt: Databasetype) => sender ! getDatabaseType(dt)
    case GetDatabaseById(id: URI) => sender ! getDatabaseById(id)
  }

  private def getConfigXML: NodeSeq = scala.xml.XML.loadFile("conf/configuration.xml")

  // only for the API
  private def getConfig: Impexconfiguration = scalaxb.fromXML[Impexconfiguration](getConfigXML)
  
  private def getConfigJSON: JsValue = Json.toJson(getConfig)

  private def getDatabases: Seq[Database] = {
    val databases = getConfig.impexconfigurationoption.filter((e) => e.key.get == "database")
    databases.map(_.as[Database])
  }
  
  // used for registry service (only portal dbs allowed)
  private def getPortalDatabases: Seq[Database] = {
    getDatabases.filter(_.portal == true)
  }
  
  // used for registry service (only portal dbs allowed)
  private def getDatabaseType(dType: Databasetype): Seq[Database] = {
    getPortalDatabases.filter(_.typeValue == dType)
  }
  
  // used in data provider actor
  private def getDatabaseById(resID: URI): Database = {
    getDatabases.find(_.id == resID).get
  }

}

object ConfigService {
  implicit val timeout = Timeout(10.seconds)
  
  // message formats
  trait ConfigMessage
  case class GetConfig(val fmt: String = "xml") extends ConfigMessage
  case object GetDatabases extends ConfigMessage
  // used for registry service
  case object GetRegistryDatabases extends ConfigMessage
  case class GetDatabaseType(val dtype: Databasetype) extends ConfigMessage
  case class GetDatabaseById(val id: URI) extends ConfigMessage

  // @TODO check if actor exists and is alive
  def request(msg: ConfigMessage): Future[Any] = {
    val actor: ActorSelection = Akka.system.actorSelection("user/config")
    (actor ? msg)
  }

}