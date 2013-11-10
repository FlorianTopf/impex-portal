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


class DataProvider(var dataTree: Spase, val treeURL: String, val methodsURL: String) extends Actor {
    def receive = {
        case "tree" => sender ! dataTree
        case "update" => {
          val promise = WS.url(treeURL).get()
          dataTree = scalaxb.fromXML[Spase](Await.result(promise, Duration.Inf).xml)
          println("finished")
        }
        case _ => sender ! Json.obj("error" -> "option not found")
    }
    
}

object DataProvider {
    implicit val timeout = Timeout(1 second)
    
    def getResource(name: String) = {
        val actor: ActorRef = Akka.system.actorFor("user/"+name)
        Await.result((actor ? "tree"), Duration.Inf).asInstanceOf[Spase]
    }
    
    def update(name: String) = {
    	val actor: ActorRef = Akka.system.actorFor("user/"+name)
    	Await.result((actor ? "update"), Duration.Inf)
    }
    
}