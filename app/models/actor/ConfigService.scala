package models.actor

import models.binding._
import scala.collection.mutable.ListBuffer
import play.api._
import play.api.libs.json._
import play.api.libs.concurrent._
import play.libs.Akka._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import akka.util.Timeout
import akka.pattern.ask
import akka.actor._
import scala.concurrent._
import scala.concurrent.duration._
import play.api.libs.ws._

class ConfigService extends Actor {
    val config = scala.xml.XML.loadFile("conf/configuration.xml")

    val configuration = scalaxb.fromXML[Impexconfiguration](config)
    
    val databases: ListBuffer[Database] = ListBuffer()
    val tools: ListBuffer[Tool] = ListBuffer()
    
    configuration.impexconfigurationoption.foreach(c => {
      //println(c.key.get +"="+ c.value)  
      c.value match {
        case d: Database => databases+=d//; println(d)
        case t: Tool => tools+=t
      }
    })
    
    // @TODO provide messages for exposing in XML
    def receive = {
        case Some("database") => sender ! databases.toList
        case Some("tool") => sender ! tools.toList
        case None => sender ! configuration
        case _ => sender ! Json.obj("error" -> "option not found")
    }
    
}

object ConfigService {
    implicit val timeout = Timeout(1 second)
    
    def getConfig(option: Option[String] = None) = {
        val actor: ActorRef = Akka.system.actorFor("user/config")
        actor.isTerminated match {
            case false => (actor ? option)
            case _ => Akka.future {
                Json.obj("error" -> "actor terminated")
            } // @TODO create new actor
        }
    }
    
}