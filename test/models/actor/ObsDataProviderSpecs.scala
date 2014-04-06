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
  ESimulationModel, ESimulationRun, 
  ENumericalOutput, EGranule
}


object ObsDataProviderSpecs extends Specification with Mockito {

    // test info
  	val rand = new Random(System.currentTimeMillis())
    val config = scalaxb.fromXML[Impexconfiguration](scala.xml.XML.loadFile("conf/configuration.xml"))
	val databases = config.impexconfigurationoption.filter(_.key.get == "database").map(
	    _.as[Database]).filter(d => d.typeValue == Observation)

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
    } 
}