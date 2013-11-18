package models.actor

import models.binding._
import play.api._
import play.api.libs.json._
import play.api.libs.concurrent._
import play.libs.Akka._
import play.api.Play.current
import akka.util.Timeout
import akka.pattern.ask
import akka.actor._
import scala.concurrent.duration._
import scala.xml._
//import scala.collection.immutable.Map
import scala.collection.mutable.Map
import scala.language.postfixOps

// message formats
trait ConfigMessage
object GetConfig extends ConfigMessage
object GetTools extends ConfigMessage
object GetDatabases extends ConfigMessage
case class GetDatabase(val name: String) extends ConfigMessage
case class GetTool(val name: String) extends ConfigMessage

// @TODO provide a possiblity for updating config
//		 + saving to filesystem
class ConfigService extends Actor {  
    // @TODO provide messages for exposing in JSON
    // @TODO unified error message 
    def receive = {
        case GetConfig => sender ! getConfigXML
        case GetDatabases => sender ! getDatabases
        case GetTools => sender ! getTools
        case GetDatabase(n: String) => sender ! getDatabase(n)
        case GetTool(n: String) => sender ! getTool(n)
        //case _ => sender ! Json.obj("error" -> "message not found")
        case _ => sender ! <error>message not found in config</error>
    }
    
    private def getConfigXML: NodeSeq = scala.xml.XML.loadFile("conf/configuration.xml")
    
    private def getConfig: Impexconfiguration = scalaxb.fromXML[Impexconfiguration](getConfigXML)

    private def getDatabases = {
      val databases = getConfig.impexconfigurationoption.filter(c => c.key.get == "database")
      val result = databases map (d => (d.as[Database].name,d.as[Database]))
      result.toMap
    }
    
    private def getTools = {
      val databases = getConfig.impexconfigurationoption.filter(c => c.key.get == "tool")
      val result = databases map (d => (d.as[Tool].name,d.as[Tool]))
      result.toMap
    }
    
    private def getDatabase(name: String): Database = {
       getDatabases.get(name).get
    }
    
    private def getTool(name: String): Tool = {
       getTools.get(name).get
    }

}

object ConfigService {
    implicit val timeout = Timeout(10 seconds)
    
    // @TODO always return the same type
    // @TODO unified error message
    def request(msg: ConfigMessage) = {
        val actor: ActorRef = Akka.system.actorFor("user/config")
        actor.isTerminated match {
            case false => (actor ? msg)
            case _ => Akka.future {
                //Json.obj("error" -> "actor terminated")
            	<error>actor terminated</error>
            } // @TODO create new actor
        }
    }
    
}