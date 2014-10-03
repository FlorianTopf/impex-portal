package models.actor

import models.binding._
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test._
import play.api.libs.concurrent._
import play.api.libs.json._
import akka.pattern.ask
import akka.testkit._
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.xml.NodeSeq
import java.net.URI
import models.actor.ConfigService.{
  GetConfig, GetDatabases, 
  GetRegistryDatabases,
  GetDatabaseById,
  GetDatabaseType
}
import models.actor.ConfigService.GetDatabaseType

// actor tests need empty onStart routine  
object ConfigServiceSpecs extends Specification with Mockito {
  
    "ConfigService" should {
        
        "get config in XML" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(new ConfigService, name = "config")
                val actor = actorSystem.actorSelection("user/config")
                val future = actor ? GetConfig("xml")
                val config = Await.result(future.mapTo[NodeSeq], DurationInt(10) second)
                
                config must beAnInstanceOf[NodeSeq]
            }
        }
        
        "get config in JSON" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(new ConfigService, name = "config")
                val actor = actorSystem.actorSelection("user/config")
                val future = actor ? GetConfig("json")
                val config = Await.result(future.mapTo[JsValue], DurationInt(10) second)
                
                config must beAnInstanceOf[JsValue]
            }
        }
        
        "get database objects" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(new ConfigService, name = "config")
                val actor = actorSystem.actorSelection("user/config")
                val future = actor ? GetDatabases
                val databases = Await.result(future.mapTo[Seq[Database]], DurationInt(10) second)
                
                databases must beAnInstanceOf[Seq[Database]]
            }
        }
        
        "get database objects for registry" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(new ConfigService, name = "config")
                val actor = actorSystem.actorSelection("user/config")
                val future = actor ? GetRegistryDatabases
                val databases = Await.result(future.mapTo[Seq[Database]], DurationInt(10) second)
                
                databases must beAnInstanceOf[Seq[Database]]
            }
        }
        
        "get database objects by type" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(new ConfigService, name = "config")
                val actor = actorSystem.actorSelection("user/config")
                val future = actor ? GetDatabaseType(Simulation)
                val databases = Await.result(future.mapTo[Seq[Database]], DurationInt(10) second)
                
                databases.map(_.typeValue must beEqualTo(Simulation))
                databases must beAnInstanceOf[Seq[Database]]
            }
        }
        
        "get database object by id" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(new ConfigService, name = "config")
                val actor = actorSystem.actorSelection("user/config")
                val future = actor ? GetDatabaseById(new URI("impex://LATMOS"))
                val database = Await.result(future.mapTo[Database], DurationInt(10) second)
                
                database must beAnInstanceOf[Database]
            }
        }
        
    }
}