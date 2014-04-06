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
  
	// test info => get all available databases from config
	val config = scalaxb.fromXML[Impexconfiguration](scala.xml.XML.loadFile("conf/configuration.xml"))
	val databases = config.impexconfigurationoption.filter(_.key.get == "database").map(_.as[Database])
	val providers: Seq[(Database, Trees, Methods)] = databases map { database => 
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
	  (database, Trees(trees), Methods(methods))
	}
	
	val rand = new Random(System.currentTimeMillis())
  
	def registerActors(registryActor: ActorSelection) = {
	  providers map { provider =>
	    provider._1.typeValue match {
	      case Simulation => 
	        registryActor ? RegisterProvider(Props(new SimDataProvider(provider._2, provider._3)), provider._1.name)
          case Observation => 
            registryActor ? RegisterProvider(Props(new ObsDataProvider(provider._2, provider._3)), provider._1.name)
        }
      }	
	}
	
    "RegistryService" should {
    
        "be initialised and hold valid actors" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
                registerActors(registryActor)
                
                val randomProvider1 = providers(rand.nextInt(providers.length))._1
                val future1 = RegistryService.getTree(Some(randomProvider1.id.toString))
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getTree(Some("impex://TEST"))
                val result2 = Await.result(future2.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val randomProvider3 = providers(rand.nextInt(providers.length))._1
                val future3 = RegistryService.getMethods(Some(randomProvider3.id.toString))
                val result3 = Await.result(future3.mapTo[Either[Seq[NodeSeq], RequestError]], DurationInt(10) second)
                val future4 = RegistryService.getMethods(Some("impex://TEST"))
                val result4 = Await.result(future4.mapTo[Either[Seq[NodeSeq], RequestError]], DurationInt(10) second)
                
                // @FIXME intermediate solution for observations
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
                val registryActorRef = TestActorRef((new RegistryService), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
                registerActors(registryActor)
               
                val future1 = RegistryService.getRepository(Some(providers(rand.nextInt(providers.length))._1.id.toString))
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
                val registryActorRef = TestActorRef((new RegistryService), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
                registerActors(registryActor)	
               
                val future1 = RegistryService.getSimulationModel(Some("impex://FMI/HWA/GUMICS"), "false")
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getSimulationModel(Some("impex://TEST"), "false")
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
 
        "fetch simulation runs" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
                registerActors(registryActor)	
               
                val future1 = RegistryService.getSimulationRun(Some("impex://SINP/PMM/Earth/Static"), "false")
                val result1 = Await.result(future1.mapTo[Either[Spase, RequestError]], DurationInt(10) second)
                val future2 = RegistryService.getSimulationRun(Some("impex://TEST"), "false")
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

        "fetch numerical outputs" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
                registerActors(registryActor)
               
                val future1 = RegistryService.getRepository(Some("impex://LATMOS/Hybrid/Mars_14_01_13"))
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
        
        "fetch granules" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val configActorRef = TestActorRef(new ConfigService, name = "config")
                val registryActorRef = TestActorRef((new RegistryService), name = "registry")
                val registryActor = actorSystem.actorSelection("user/registry")
                registerActors(registryActor)	
               
                val future1 = RegistryService.getRepository(Some("impex://LATMOS/Hybrid/Mars_14_01_13/Mag/2D/XY"))
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
        
    }
}