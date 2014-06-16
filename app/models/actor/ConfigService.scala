package models.actor

import models.binding._
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
import models.enums._


class ConfigService extends Actor {
  import models.actor.ConfigService._
  
  def receive = {
    case GetConfig(fmt: String) => fmt.toLowerCase match {
      case "xml" => sender ! getConfigXML
      case "json" => sender ! getConfigJSON
      case _ => sender ! Json.toJson(RequestError(ERequestError.UNKNOWN_MSG))
    }
    // not used in the REST interface of the portal
    case GetDatabases => sender ! getDatabases
    case GetDatabaseType(dt: Databasetype) => sender ! getDatabaseType(dt)
    case GetDatabaseById(id: URI) => sender ! getDatabaseById(id)
  }

  private def getConfigXML: NodeSeq = scala.xml.XML.loadFile("conf/configuration.xml")

  private def getConfig: Impexconfiguration = scalaxb.fromXML[Impexconfiguration](getConfigXML)
  
  private def getConfigJSON: JsValue = Json.toJson(getConfig)

  private def getDatabases: Seq[Database] = {
    val databases = getConfig.impexconfigurationoption.filter(_.key.get == "database")
    databases map (_.as[Database])
  }

  private def getDatabaseType(dType: Databasetype): Seq[Database] = {
    getDatabases.filter(_.typeValue == dType)
  }
  
  private def getDatabaseById(resID: URI): Database = {
    getDatabases.find(_.id == resID).get
  }

}

object ConfigService {
  implicit val timeout = Timeout(10.seconds)
  
  // message formats
  trait ConfigMessage
  case class GetConfig(val fmt: String = "xml") extends ConfigMessage
  // not used in the REST interface of the portal
  case object GetDatabases extends ConfigMessage
  case class GetDatabaseType(val dtype: Databasetype) extends ConfigMessage
  case class GetDatabaseById(val id: URI) extends ConfigMessage

  // @TODO check if actor exists and is alive
  def request(msg: ConfigMessage): Future[Any] = {
    val actor: ActorSelection = Akka.system.actorSelection("user/config")
    (actor ? msg)
  }

}