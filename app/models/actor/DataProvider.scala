package models.actor

import models.binding._
import play.api._
import play.api.libs.json._
import play.api.libs.concurrent._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import akka.util.Timeout
import akka.pattern.ask
import akka.actor._
import scala.concurrent._
import scala.concurrent.duration._
import play.api.libs.ws._
import scala.xml._

trait Tree {
  val content: Any
}
case class SimTree(val content: NodeSeq) extends Tree
case class ObsTree(val content: NodeSeq) extends Tree

class DataProvider(var dataTree: Tree, val treeURL: String, val methodsURL: String) extends Actor {
    def receive = {
        case "tree" => sender ! dataTree.content
        case "update" => {
          val promise = WS.url(treeURL).get()
          // @TODO change duration of Await
          dataTree = dataTree match {
            case SimTree(_) => SimTree(Await.result(promise, Duration.Inf).xml)
            case ObsTree(_) => ObsTree(Await.result(promise, Duration.Inf).xml)
          }
          println("finished")
        }
        //case _ => sender ! Json.obj("error" -> "option not found")
        case _ => sender ! "<error>option not found</error>"
    }
    
}

object DataProvider {
    implicit val timeout = Timeout(1 second)
    
    // @TODO getResource will get tree or methods
    def getResource(name: String): NodeSeq = {
        val actor: ActorRef = Akka.system.actorFor("user/"+name)
        Await.result((actor ? "tree"), Duration.Inf).asInstanceOf[NodeSeq]
    }
    
    def update(name: String) = {
    	val actor: ActorRef = Akka.system.actorFor("user/"+name)
    	Await.result((actor ? "update"), Duration.Inf)
    }
    
}