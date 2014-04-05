package models.actor

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import models.binding._
import play.api.test.Helpers._
import play.api.test._
import play.api.libs.concurrent._
import play.api.libs.json._
import akka.testkit._
import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.xml.NodeSeq
import models.provider._
import scala.concurrent._
import models.enums._
import java.net.URI

object RegistryServiceSpecs extends Specification with Mockito {

    "RegistryService" should {
      
        // test info => all available databases
    	val config = scalaxb.fromXML[Impexconfiguration](scala.xml.XML.loadFile("conf/configuration.xml"))
    	val databases = config.impexconfigurationoption.filter(_.key.get == "database").map(_.as[Database])
    	val providers: Seq[(Databasetype, String, Trees, Methods)] = databases map { database => 
    	  val dns: String = database.databaseoption.head.value
    	  val protocol: String = database.protocol.head
    	  val treeURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, database.tree)
    	  val methodsURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, database.methods)
    	  
    	  val trees: Seq[NodeSeq] = treeURLs map { URL => 
    	    scala.xml.XML.load(PathProvider.getPath("trees", database.name , URL.toString))
    	  }
    	  val methods: Seq[NodeSeq] = methodsURLs map { URL =>
    	    scala.xml.XML.load(PathProvider.getPath("methods", database.name , URL.toString))
    	    
    	  }
          (database.typeValue, database.name, Trees(trees), Methods(methods))
    	} 
        
        "be initialised and hold valid actors" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService), name = "registry")
                
                providers map { provider =>
                  provider._1 match {
                    case Simulation => RegistryService.registerChild(
                    		Props(new SimDataProvider(provider._3, provider._4)), provider._2)
                    case Observation => RegistryService.registerChild(
                           Props(new ObsDataProvider(provider._3, provider._4)), provider._2)
                  }
                }	
               
                val future1 = RegistryService.getRepository(Some("impex://LATMOS"))
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getTree(Some("impex://TEST"))
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)       
                
                result1 must beLeft
                result1 match {
                  case Left(spase) => spase must beAnInstanceOf[Spase]
                  case Right(error) => error must beAnInstanceOf[RequestError]
                }
                result2 must beRight
                result2 match {
                  case Left(spase) => spase must beAnInstanceOf[Spase]
                  case Right(error) => error must beAnInstanceOf[RequestError]
                }
            }
        }
        
    }
}