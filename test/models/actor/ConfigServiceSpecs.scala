package models.actor

import models.binding._
import models.actor.ConfigService._
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test._
import play.api.libs.concurrent._
import play.api.libs.json._
import akka.testkit._
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.xml.NodeSeq

  
object ConfigServiceSpecs extends Specification with Mockito {
  
    "ConfigService" should {
        
        "get config in XML" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(new ConfigService, name = "config")
                val actor = actorSystem.actorSelection("user/config")
                val future = ConfigService.request(GetConfig("xml"))
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
                val future = ConfigService.request(GetConfig("json"))
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
                val future = ConfigService.request(GetDatabases)
                val databases = Await.result(future.mapTo[Seq[Database]], DurationInt(10) second)
                
                databases must beAnInstanceOf[Seq[Database]]
            }
        }
        
        "get tool objects" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(new ConfigService, name = "config")
                val actor = actorSystem.actorSelection("user/config")
                val future = ConfigService.request(GetTools)
                val tools = Await.result(future.mapTo[Seq[Tool]], DurationInt(10) second)
                
                tools must beAnInstanceOf[Seq[Tool]]
            }
        }
        
        "get database object by name" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(new ConfigService, name = "config")
                val actor = actorSystem.actorSelection("user/config")
                val future = ConfigService.request(GetDatabaseByName("AMDA"))
                val database = Await.result(future.mapTo[Database], DurationInt(10) second)
                
                database must beAnInstanceOf[Database]
            }
        }
        
    }
}