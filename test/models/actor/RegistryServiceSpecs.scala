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

// actor tests need empty onStart routine  
// @TODO test recursion and check random resources in the tree
object RegistryServiceSpecs extends Specification with Mockito {
  
    // test info => only for portal == true
  	val rand = new Random(java.lang.System.currentTimeMillis)
	val config = scalaxb.fromXML[Impexconfiguration](scala.xml.XML.loadFile("conf/configuration.xml"))
	val databases = config.impexconfigurationoption.filter(d => d.key.get == "database")
		.map(_.as[Database]).filter(d => d.portal)
	
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
                val future2 = RegistryService.getTree(Some("spase://TEST"))
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                
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
            }
        }
        
        "get all statuses" in {
        	val app = new FakeApplication
        	running(app) {
        		implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
                
                val future = RegistryService.getStatus()
                val result = Await.result(future.mapTo[Seq[DataProvider.Status]], DurationInt(10) second)
        	  
                result must beAnInstanceOf[Seq[DataProvider.Status]]
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
                val future2 = RegistryService.getRepository(Some("spase://TEST"))
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
               
                val future1 = RegistryService.getSimulationModel(Some("spase://IMPEX/Repository/FMI/GUMICS"), false)
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getSimulationModel(Some("spase://TEST"), false)
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)       
                val future3 = RegistryService.getSimulationModel(Some("spase://IMPEX/Repository/AMDA"), false)
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
               
                val future1 = RegistryService.getSimulationRun(Some("spase://IMPEX/SimulationModel/SINP/Earth/Static"), false)
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getSimulationRun(Some("spase://TEST"), false)
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)  
                val future3 = RegistryService.getSimulationRun(Some("spase://IMPEX/Repository/AMDA"), false)
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

        "fetch numerical outputs" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
               
                val future1 = RegistryService.getNumericalOutput(Some("spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_14_01_13"), false)
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getNumericalOutput(Some("spase://TEST"), false)
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)  
                val future3 = RegistryService.getNumericalOutput(Some("spase://IMPEX/Repository/AMDA"), false)
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
        
        "fetch granules" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
               
                val future1 = RegistryService.getGranule(Some("spase://IMPEX/Granule/LATMOS/Hybrid/Mars_14_01_13/Mag/2D/XY"), false)
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getGranule(Some("spase://TEST"), false)
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)   
                val future3 = RegistryService.getGranule(Some("spase://IMPEX/Repository/AMDA"), false)
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
        
        "fetch observatories" in {
        	val app = new FakeApplication
        	running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
                
                val future1 = RegistryService.getObservatory(Some("spase://IMPEX/Observatory/AMDA/ACE"), false)
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getObservatory(Some("spase://TEST"), false)
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)   
                val future3 = RegistryService.getObservatory(Some("spase://IMPEX/Repository/LATMOS"), false)
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
        
        "fetch instruments" in {
        	val app = new FakeApplication
        	running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
                
                val future1 = RegistryService.getInstrument(Some("spase://IMPEX/Instrument/AMDA/ACE/Ephemeris"), false)
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getInstrument(Some("spase://TEST"), false)
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)   
                val future3 = RegistryService.getInstrument(Some("spase://IMPEX/Repository/LATMOS"), false)
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
        
        "fetch numericaldata" in {
        	val app = new FakeApplication
        	running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService(databases)), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
            
                val future1 = RegistryService.getNumericalData(Some("spase://IMPEX/NumericalData/AMDA/Giotto/RPA/gio-rpa1-corr"), false)
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getNumericalData(Some("spase://TEST"), false)
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)   
                val future3 = RegistryService.getNumericalData(Some("spase://IMPEX/Repository/LATMOS"), false)
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
        
    }
}