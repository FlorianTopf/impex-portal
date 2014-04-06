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

// @TODO provide a possiblity for updating config + saving to filesystem
class ConfigService extends Actor {
  import models.actor.ConfigService._
  
  def receive = {
    case GetConfig(fmt: String) => fmt.toLowerCase match {
      case "xml" => sender ! getConfigXML
      case "json" => sender ! getConfigJSON
      case _ => sender ! getConfigJSON
    }
    // not used in the REST interface of the portal
    case GetDatabases => sender ! getDatabases
    case GetDatabaseType(dt: Databasetype) => sender ! getDatabaseType(dt)
    case GetDatabaseByName(n: String) => sender ! getDatabaseByName(n)
    case GetTools => sender ! getTools
    case GetTool(n: String) => sender ! getTool(n)
  }

  private def getConfigXML: NodeSeq = scala.xml.XML.loadFile("conf/configuration.xml")

  private def getConfig: Impexconfiguration = scalaxb.fromXML[Impexconfiguration](getConfigXML)
  
  private def getConfigJSON: JsValue = Json.toJson(getConfig)

  private def getDatabases: Seq[Database] = {
    val databases = getConfig.impexconfigurationoption.filter(_.key.get == "database")
    databases map (_.as[Database])
  }

  private def getTools: Seq[Tool] = {
    val databases = getConfig.impexconfigurationoption.filter(_.key.get == "tool")
    databases map (_.as[Tool])
  }

  private def getDatabaseType(dType: Databasetype): Seq[Database] = {
    getDatabases.filter(_.typeValue == dType)
  }

  // @FIXME return an option type
  private def getDatabaseByName(name: String): Database = {
    getDatabases.find(_.name == name).get
  }
  
  // @FIXME return an option type
  private def getDatabaseById(resID: URI): Database = {
    getDatabases.find(p => (resID.toString().indexOf(p.id) != -1)).get
  }

  private def getTool(name: String): Tool = {
    getTools.find(_.name == name).get
  }

}

object ConfigService {
  implicit val timeout = Timeout(10.seconds)
  
  // message formats
  trait ConfigMessage
  case class GetConfig(val fmt: String = "xml") extends ConfigMessage
  // not used in the REST interface of the portal
  case object GetTools extends ConfigMessage
  case object GetDatabases extends ConfigMessage
  case class GetDatabaseType(val dtype: Databasetype) extends ConfigMessage
  case class GetDatabaseByName(val name: String) extends ConfigMessage
  case class GetDatabaseByID(val id: URI) extends ConfigMessage
  case class GetTool(val name: String) extends ConfigMessage

  // @TODO check if actor exists and is alive
  def request(msg: ConfigMessage): Future[Any] = {
    val actor: ActorSelection = Akka.system.actorSelection("user/config")
    actor ? msg
  }

}