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
// only single imports possible
import models.actor.DataProvider.{ 
  GetTree, GetMethods, 
  GetElement, ERepository, 
  ESimulationModel, ESimulationRun, 
  ENumericalOutput, EGranule
}

// @FIXME we cannot test because of dependencies on Config Service
object ObsDataProviderSpecs extends Specification with Mockito {

    // test info
    val providerName = "CLWeb"
    val treeName = PathProvider.getPath("trees", providerName, "/clweb_tree.xml")
    println(treeName)
    val tree = scala.xml.XML.load(treeName)    
    val methodsName = PathProvider.getPath("methods", providerName, "/Methods_CLWEB.wsdl")
    println(methodsName)
    val methods = scala.xml.XML.load(methodsName) 

    "ObsDataProvider" should {
        
        "be initialised and contain data" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                // here we need the config service available?
                //val configActorRef = TestActorRef(new ConfigService, name = "config")
                val actorRef = TestActorRef(
                    new ObsDataProvider(Trees(Seq(tree)), Methods(Seq(methods))), name = providerName
                    ) 
                val actor = actorSystem.actorSelection("user/"+providerName)
                //val treeFuture = actor ? GetTree
                //val treeResult = Await.result(treeFuture, DurationInt(10) second)
                /*val methodsFuture = actor ? GetMethods
                val methodsResult = Await.result(methodsFuture.mapTo[Seq[NodeSeq]], DurationInt(10) second) */
                
                actor must beAnInstanceOf[ActorSelection]  
                //treeResult must beAnInstanceOf[Spase]
                //methodsResult must beAnInstanceOf[Seq[NodeSeq]]
            }
        }
    } 
}