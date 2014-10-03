package models.actor

import models.binding._
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
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.xml.NodeSeq
import java.util.Random
// only single imports possible
import models.actor.DataProvider.{ 
  GetTree, GetMethods,
  GetElement, ERepository, 
  EObservatory, EInstrument, 
  ENumericalData
}

// actor tests need empty onStart routine  
object ObsDataProviderSpecs extends Specification with Mockito {

    // test info => only for AMDA!
  	val rand = new Random(java.lang.System.currentTimeMillis)
    val config = scalaxb.fromXML[Impexconfiguration](scala.xml.XML.loadFile("conf/configuration.xml"))
	val databases = config.impexconfigurationoption.filter(_.key.get == "database").map(
	    _.as[Database]).filter(d => d.typeValue == Observation && d.name == "AMDA")

    "ObsDataProvider" should {
        
        "be initialised and contain data" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val database = databases(rand.nextInt(databases.length))
                val actorId = UrlProvider.encodeURI(database.id)
                val actorRef = TestActorRef(
                    new ObsDataProvider(database), name = actorId
                    ) 
                val actor = actorSystem.actorSelection("user/"+actorId)
                val treeFuture = actor ? GetTree
                val treeResult = Await.result(treeFuture.mapTo[Spase], DurationInt(10) second)
                val methodsFuture = actor ? GetMethods
                val methodsResult = Await.result(methodsFuture.mapTo[Seq[NodeSeq]], DurationInt(10) second)
                
                actor must beAnInstanceOf[ActorSelection]  
                treeResult must beAnInstanceOf[Spase]
                methodsResult must beAnInstanceOf[Seq[NodeSeq]]
            }
        }
        
        "fetch repositories" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val database = databases(rand.nextInt(databases.length))
                val actorId = UrlProvider.encodeURI(database.id)
                val actorRef = TestActorRef(
                    new ObsDataProvider(database), name = actorId
                    ) 
                val actor = actorSystem.actorSelection("user/"+actorId)
                val future = actor ? GetElement(ERepository, None)
                val result = Await.result(future.mapTo[Spase], DurationInt(10) second)
                val repositories = 
                  result.ResourceEntity.filter(p => p.key == Some("Repository")).map(_.as[Repository])
                
                actor must beAnInstanceOf[ActorSelection]  
                result must beAnInstanceOf[Spase]
                repositories must beAnInstanceOf[Seq[Repository]]
            }
        }
        
        "fetch observatories" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val database = databases(rand.nextInt(databases.length))
                val actorId = UrlProvider.encodeURI(database.id)
                val actorRef = TestActorRef(
                    new ObsDataProvider(database), name = actorId
                    ) 
                val actor = actorSystem.actorSelection("user/"+actorId)
                val future = actor ? GetElement(EObservatory, None)
                val result = Await.result(future.mapTo[Spase], DurationInt(10) second)
                val observatories = 
                  result.ResourceEntity.filter(p => p.key == Some("Observatory")).map(_.as[Observatory])
                
                actor must beAnInstanceOf[ActorSelection]  
                result must beAnInstanceOf[Spase]
                observatories must beAnInstanceOf[Seq[Observatory]]
            }
        }
        
        "fetch instruments" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val database = databases(rand.nextInt(databases.length))
                val actorId = UrlProvider.encodeURI(database.id)
                val actorRef = TestActorRef(
                    new ObsDataProvider(database), name = actorId
                    ) 
                val actor = actorSystem.actorSelection("user/"+actorId)
                val future = actor ? GetElement(EInstrument, None)
                val result = Await.result(future.mapTo[Spase], DurationInt(10) second)
                val instruments = 
                  result.ResourceEntity.filter(p => p.key == Some("Instrument")).map(_.as[Instrument])
                
                actor must beAnInstanceOf[ActorSelection]  
                result must beAnInstanceOf[Spase]
                instruments must beAnInstanceOf[Seq[Instrument]]
            }
        }
        
        "fetch numerical data" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val database = databases(rand.nextInt(databases.length))
                val actorId = UrlProvider.encodeURI(database.id)
                val actorRef = TestActorRef(
                    new ObsDataProvider(database), name = actorId
                    ) 
                val actor = actorSystem.actorSelection("user/"+actorId)
                val future = actor ? GetElement(ENumericalData, None)
                val result = Await.result(future.mapTo[Spase], DurationInt(10) second)
                val instruments = 
                  result.ResourceEntity.filter(p => p.key == Some("NumericalData")).map(_.as[NumericalData])
                
                actor must beAnInstanceOf[ActorSelection]  
                result must beAnInstanceOf[Spase]
                instruments must beAnInstanceOf[Seq[NumericalData]]
            }
        }
        
    } 
}