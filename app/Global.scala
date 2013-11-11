import akka.actor._
import play.libs.Akka
import scala.concurrent._
import play.api.libs.concurrent.Execution.Implicits._
import play.api._
import models.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import models.binding._
import play.api.libs.ws._

object Global extends GlobalSettings {
  override def onStart(app: play.api.Application) {
    implicit val timeout = Timeout(1 second)
    
    println("application has started")
    
    val actor: ActorRef = Akka.system.actorOf(Props(new ConfigService), name = "config")
    val config = Await.result((actor ? Some("database")), Duration.Inf)
    
    //println(config)
    
    config.asInstanceOf[List[Database]] map {
      database => database.typeValue.get.toString() match {
        case "simulation" => {
          println("fetching tree from "+database.name)
          
          val treeURL = "http://"+database.databaseoption.head.value+database.tree.head
          val methodsURL = "http://"+database.databaseoption.head.value+database.methods.head
          
          println("treeURL="+treeURL)
          println("methodsURL="+methodsURL)
          
          // @TODO move this to actor later
          val promise = WS.url(treeURL).get()
          // @TODO change duration of Await
          val tree = Await.result(promise, Duration.Inf).xml
          
          // @TODO exchange name with spaseID
          val actor: ActorRef = Akka.system.actorOf(
              Props(new DataProvider(SimTree(tree), treeURL, methodsURL)), 
              name = database.name)
              
          // @TODO initial delay will be switched later
          Akka.system.scheduler.schedule(30.minutes, 24.hours, actor, "update")
        }
        case "observation" => println("not yet implemented!")
      } 
    }
    
  } 
  
}