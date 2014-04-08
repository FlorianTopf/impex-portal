package models.actor

import models.binding._
import models.enums._
import models.provider._
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test._
import play.api.libs.concurrent._
import play.api.libs.json._
import akka.testkit._
import akka.actor._
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.xml.NodeSeq
import java.net.URI
import java.util.Random
import models.actor.RegistryService.RegisterProvider

// @TODO test recursion and check random resources in the tree
object RegistryServiceSpecs extends Specification with Mockito {
  
	// test info
  	val rand = new Random(System.currentTimeMillis())
	val config = scalaxb.fromXML[Impexconfiguration](scala.xml.XML.loadFile("conf/configuration.xml"))
	val databases = config.impexconfigurationoption.filter(_.key.get == "database").map(_.as[Database])
	
    "RegistryService" should {
    
        "be initialised and hold valid actors" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
                
                val randomProvider1 = databases(rand.nextInt(databases.length))
                val future1 = RegistryService.getTree(Some(randomProvider1.id.toString))
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getTree(Some("impex://TEST"))
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val randomProvider3 = databases(rand.nextInt(databases.length))
                val future3 = RegistryService.getMethods(Some(randomProvider3.id.toString))
                val result3 = Await.result(future3.mapTo[Either[Seq[NodeSeq], RequestError]], DurationInt(10) second)
                val future4 = RegistryService.getMethods(Some("impex://TEST"))
                val result4 = Await.result(future4.mapTo[Either[Seq[NodeSeq], RequestError]], DurationInt(10) second)
                
                if(randomProvider1.typeValue == Simulation)
                  result1 must beLeft
                else
                  result1 must beRight
                result1 match {
                  case Left(spase) => spase must beAnInstanceOf[Spase]
                  case Right(error) => error must beAnInstanceOf[RequestError]
                }
                result2 must beRight
                result2 match {
                  case Left(spase) => spase must beAnInstanceOf[Spase]
                  case Right(error) => error must beAnInstanceOf[RequestError]
                }
                result3 must beLeft
                result3 match {
                  case Left(xml) => xml must beAnInstanceOf[Seq[NodeSeq]]
                  case Right(error) => error must beAnInstanceOf[RequestError]
                }
                result4 must beRight
                result4 match {
                  case Left(xml) => xml must beAnInstanceOf[Seq[NodeSeq]]
                  case Right(error) => error must beAnInstanceOf[RequestError]
                } 
            }
        }
        
        "fetch repositories" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
               
                val future1 = RegistryService.getRepository(Some(databases(rand.nextInt(databases.length)).id.toString))
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getRepository(Some("impex://TEST"))
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
        
        "fetch simulation models" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
               
                val future1 = RegistryService.getSimulationModel(Some("impex://FMI/HWA/GUMICS"), "false")
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getSimulationModel(Some("impex://TEST"), "false")
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)       
                val future3 = RegistryService.getSimulationModel(Some("impex://AMDA"), "false")
                val result3 = Await.result(future3.mapTo[Either[Spase, RequestError]], DurationInt(10) second)  
                
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
                result3 must beRight
                result3 match {
                  case Left(spase) => spase must beAnInstanceOf[Spase]
                  case Right(error) => error must beAnInstanceOf[RequestError]
                }  
            }
        }
 
        "fetch simulation runs" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")	
               
                val future1 = RegistryService.getSimulationRun(Some("impex://SINP/PMM/Earth/Static"), "false")
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getSimulationRun(Some("impex://TEST"), "false")
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)  
                val future3 = RegistryService.getSimulationRun(Some("impex://AMDA"), "false")
                val result3 = Await.result(future3.mapTo[Either[Spase, RequestError]], DurationInt(10) second)  
                
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
                result3 match {
                  case Left(spase) => spase must beAnInstanceOf[Spase]
                  case Right(error) => error must beAnInstanceOf[RequestError]
                }
            }
        }

        "fetch numerical outputs" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
               
                val future1 = RegistryService.getNumericalOutput(Some("impex://LATMOS/Hybrid/Mars_14_01_13"), "false")
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getNumericalOutput(Some("impex://TEST"), "false")
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)  
                val future3 = RegistryService.getNumericalOutput(Some("impex://AMDA"), "false")
                val result3 = Await.result(future3.mapTo[Either[Spase, RequestError]], DurationInt(10) second)  
                
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
                result3 match {
                  case Left(spase) => spase must beAnInstanceOf[Spase]
                  case Right(error) => error must beAnInstanceOf[RequestError]
                }
            }
        }
        
        "fetch granules" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
               
                val future1 = RegistryService.getGranule(Some("impex://LATMOS/Hybrid/Mars_14_01_13/Mag/2D/XY"), "false")
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getGranule(Some("impex://TEST"), "false")
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)   
                val future3 = RegistryService.getGranule(Some("impex://AMDA"), "false")
                val result3 = Await.result(future3.mapTo[Either[Spase, RequestError]], DurationInt(10) second)  
                
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
                result3 match {
                  case Left(spase) => spase must beAnInstanceOf[Spase]
                  case Right(error) => error must beAnInstanceOf[RequestError]
                }
            }
        }
        
        // @TODO extend with observation test cases
        
    }
}