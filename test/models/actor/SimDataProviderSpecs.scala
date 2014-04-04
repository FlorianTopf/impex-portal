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

object SimDataProviderSpecs extends Specification with Mockito {

    "SimDataProvider" should {
      
        // test info
        val providerName = "FMI"
        val treeName = PathProvider.getPath("trees", providerName, "/Tree_FMI_HYB.xml")
        val tree = scala.xml.XML.load(treeName)    
        val methodsName = PathProvider.getPath("methods", providerName, "/Methods_FMI.wsdl")
        val methods = scala.xml.XML.load(methodsName) 
        
        "be initialised and contain data" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(
                    new SimDataProvider(Trees(Seq(tree)), Methods(Seq(methods))), name = providerName
                    ) 
                val actor = actorSystem.actorSelection("user/"+providerName)
                // @FIXME why cant we import the DataProvider Messages?
                val treeFuture = actor ? models.actor.DataProvider.GetTree
                val treeResult = Await.result(treeFuture.mapTo[Spase], DurationInt(10) second)
                val methodsFuture = actor ? models.actor.DataProvider.GetMethods
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
                val actorRef = TestActorRef(
                    new SimDataProvider(Trees(Seq(tree)), Methods(Seq(methods))), name = providerName
                    ) 
                val actor = actorSystem.actorSelection("user/"+providerName)
                // @FIXME why cant we import the DataProvider Messages?
                val future = actor ? models.actor.DataProvider.GetElement(
                    models.actor.DataProvider.ERepository, None)
                val result = Await.result(future.mapTo[Spase], DurationInt(10) second)
                val repositories = 
                  result.ResourceEntity.filter(p => p.key == Some("Repository")).map(_.as[Repository])
                
                actor must beAnInstanceOf[ActorSelection]  
                result must beAnInstanceOf[Spase]
                repositories must beAnInstanceOf[Seq[Repository]]
            }
        }
        
        "fetch simulation models" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(
                    new SimDataProvider(Trees(Seq(tree)), Methods(Seq(methods))), name = providerName
                    ) 
                val actor = actorSystem.actorSelection("user/"+providerName)
                // @FIXME why cant we import the DataProvider Messages?
                val future = actor ? models.actor.DataProvider.GetElement(
                    models.actor.DataProvider.ESimulationModel, None)
                val result = Await.result(future.mapTo[Spase], DurationInt(10) second)
                val repositories = 
                  result.ResourceEntity.filter(p => p.key == Some("SimulationModel")).map(_.as[SimulationModelType])
                
                actor must beAnInstanceOf[ActorSelection]  
                result must beAnInstanceOf[Spase]
                repositories must beAnInstanceOf[Seq[SimulationModelType]]
            }
        }
        
        "fetch simulation runs" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(
                    new SimDataProvider(Trees(Seq(tree)), Methods(Seq(methods))), name = providerName
                    ) 
                val actor = actorSystem.actorSelection("user/"+providerName)
                // @FIXME why cant we import the DataProvider Messages?
                val future = actor ? models.actor.DataProvider.GetElement(
                    models.actor.DataProvider.ESimulationRun, None)
                val result = Await.result(future.mapTo[Spase], DurationInt(10) second)
                val repositories = 
                  result.ResourceEntity.filter(p => p.key == Some("SimulationRun")).map(_.as[SimulationRun])
                
                actor must beAnInstanceOf[ActorSelection]  
                result must beAnInstanceOf[Spase]
                repositories must beAnInstanceOf[Seq[SimulationRun]]
            }
        }
         
        "fetch numerical outputs" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(
                    new SimDataProvider(Trees(Seq(tree)), Methods(Seq(methods))), name = providerName
                    ) 
                val actor = actorSystem.actorSelection("user/"+providerName)
                // @FIXME why cant we import the DataProvider Messages?
                val future = actor ? models.actor.DataProvider.GetElement(
                    models.actor.DataProvider.ENumericalOutput, None)
                val result = Await.result(future.mapTo[Spase], DurationInt(10) second)
                val repositories = 
                  result.ResourceEntity.filter(p => p.key == Some("NumericalOutput")).map(_.as[NumericalOutput])
                
                actor must beAnInstanceOf[ActorSelection]  
                result must beAnInstanceOf[Spase]
                repositories must beAnInstanceOf[Seq[NumericalOutput]]
            }
        } 
       
        "fetch granules" in {
            val app = new FakeApplication
            running(app) {
                implicit val actorSystem = Akka.system(app)
                val actorRef = TestActorRef(
                    new SimDataProvider(Trees(Seq(tree)), Methods(Seq(methods))), name = providerName
                    ) 
                val actor = actorSystem.actorSelection("user/"+providerName)
                // @FIXME why cant we import the DataProvider Messages?
                val future = actor ? models.actor.DataProvider.GetElement(
                    models.actor.DataProvider.EGranule, None)
                val result = Await.result(future.mapTo[Spase], DurationInt(10) second)
                val repositories = 
                  result.ResourceEntity.filter(p => p.key == Some("Granule")).map(_.as[Granule])
                  
                actor must beAnInstanceOf[ActorSelection]  
                result must beAnInstanceOf[Spase]
                repositories must beAnInstanceOf[Seq[Granule]]
            }
        }
        
        //@TODO test interface for updating
    }
}