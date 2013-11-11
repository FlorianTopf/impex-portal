package models.actor

import scala.concurrent._
import scala.concurrent.duration._
import models.binding._
import akka.actor._
import play.libs._
import akka.pattern.ask
import akka.util.Timeout

// @TODO add respective actor here!

object RegistryService {
  def getDataTree(providerName: Option[String]): Option[Spase] = {
    implicit val timeout = Timeout(1 second)
    
    val actor: ActorRef = Akka.system.actorFor("user/config") 
    val config = Await.result((actor ? Some("database")), Duration.Inf)
    
    providerName match {
      case Some(p: String) =>  Some(scalaxb.fromXML[Spase](DataProvider.getResource(p)))
      case _ => None
    }
  }
  
}