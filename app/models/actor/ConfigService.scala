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

// @TODO provide a possiblity for updating config + saving to filesystem
class ConfigService extends Actor {
  import models.actor.ConfigService._
  
  // @TODO unified error message 
  def receive = {
    case GetConfig(fmt: String) => fmt.toLowerCase match {
      case "xml" => sender ! getConfigXML
      case "json" => sender ! getConfigJSON
      // @TODO error case
    }
    // not used in the REST interface of the portal
    case GetDatabases => sender ! getDatabases
    case GetDatabaseType(dt: Databasetype) => sender ! getDatabaseType(dt)
    case GetTools => sender ! getTools
    case GetDatabase(n: String) => sender ! getDatabase(n)
    case GetTool(n: String) => sender ! getTool(n)
    //case _ => sender ! Json.obj("error" -> "message not found")
    case _ => sender ! <error>message not found in config</error>
  }

  private def getConfigXML: NodeSeq = scala.xml.XML.loadFile("conf/configuration.xml")

  private def getConfig: Impexconfiguration = scalaxb.fromXML[Impexconfiguration](getConfigXML)
  
  private def getConfigJSON: JsValue = Json.toJson(getConfig)

  private def getDatabases: Seq[Database] = {
    val databases = getConfig.impexconfigurationoption.filter(c => c.key.get == "database")
    databases map (d => d.as[Database])
  }

  private def getTools: Seq[Tool] = {
    val databases = getConfig.impexconfigurationoption.filter(c => c.key.get == "tool")
    databases map (d => (d.as[Tool]))
  }

  private def getDatabaseType(dType: Databasetype): Seq[Database] = {
    getDatabases.filter(d => d.typeValue == dType)
  }

  private def getDatabase(name: String): Database = {
    getDatabases.find(p => p.name == name).get
  }

  private def getTool(name: String): Tool = {
    getTools.find(p => p.name == name).get
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
  case class GetDatabase(val name: String) extends ConfigMessage
  case class GetTool(val name: String) extends ConfigMessage

  // @TODO check if actor exists and is alive
  // @TODO unified error message
  def request(msg: ConfigMessage): Future[Any] = {
    val actor: ActorSelection = Akka.system.actorSelection("user/config")
    actor ? msg
  }

}