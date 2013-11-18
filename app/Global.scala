import akka.actor._
import play.libs.Akka
import scala.concurrent._
import play.api._
import models.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import models.binding._
import play.api.libs.ws._
import models.provider._
import scala.xml._
import scala.language.postfixOps

object Global extends GlobalSettings {
  override def onStart(app: play.api.Application) {
    implicit val timeout = Timeout(10 seconds)
    
    println("application has started")
    
    val actor: ActorRef = Akka.system.actorOf(Props(new ConfigService), name = "config")
    val config = Await.result((actor ? GetDatabases).mapTo[Map[String, Database]], 1.minute)
    
    //println(config)
    
    Akka.system.actorOf(
        Props(new RegistryService), 
        name = "registry")
    
    config map {
      database =>        
        val dns: String = database._2.databaseoption.head.value
        val treeURLs: Seq[String] = UrlProvider.getUrls(dns, database._2.tree)
        val methodsURLs: Seq[String] = UrlProvider.getUrls(dns, database._2.methods)

        println("treeURL="+treeURLs)
        println("methodsURL="+methodsURLs)      
        println("fetching tree from "+database._1)
        
        val trees: Seq[NodeSeq] = treeURLs flatMap { 
          treeURL => 
        	val promise = WS.url(treeURL).get()
        	try {
        	  Some(Await.result(promise, 1.minute).xml)
        	} catch {
        	  case e: TimeoutException => println("timeout"); None
        	}
        }
        
        val methods: Seq[NodeSeq] = methodsURLs flatMap { 
          methodsURL => 
        	val promise = WS.url(methodsURL).get()
        	try {
        	  Some(Await.result(promise, 1.minute).xml)
        	} catch {
        	  case e: TimeoutException => println("timeout"); None
        	}
        }
        
        // @TODO maybe exchange name with spaseID
        RegistryService.register(
            Props(new DataProvider(Trees(trees), Methods(methods))),
            database._1)   
    }
    
  } 
  
}