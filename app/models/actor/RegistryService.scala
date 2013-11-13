package models.actor

import scala.concurrent._
import scala.concurrent.duration._
import models.binding._
import akka.actor._
import play.libs._
import akka.pattern.ask
import akka.util.Timeout

// @TODO add respective actor here!
// => this will be started at server startup => registering the data providers
// dann einfach alle methoden getDataProvider etc. etc.

object RegistryService {
  def getDataTree(providerName: Option[String], treeType: String) = {
    implicit val timeout = Timeout(1 second)
    
    val actor: ActorRef = Akka.system.actorFor("user/config") 
    val config = Await.result((actor ? Some("database")), Duration.Inf)
    
    (providerName, treeType) match {
      case (Some(p: String), "sim") => Some(scalaxb.fromXML[Spase](DataProvider.getResource(p)))
      case (Some(p: String), "obs") => Some(scalaxb.fromXML[DataRoot](DataProvider.getResource(p)))
      case _ => None
    }
  }
  
}