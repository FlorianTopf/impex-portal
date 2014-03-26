package models.actor

import models.binding._
import models.provider._
import akka.actor._
import akka.pattern.ask
import scala.xml._
import scala.concurrent._
import scala.concurrent.duration._
import scalaxb.DataRecord

class SimDataProvider(val dataTree: Trees, val accessMethods: Methods) 
extends Actor with DataProvider[Spase] {
  import models.actor.ConfigService._
  import models.actor.DataProvider._
  
  // @TODO unified error messages
  def receive = {
    // @TODO return unified tree in XML
    case GetTrees(Some("xml")) => sender ! getTreeXML
    case GetTrees(None) => sender ! getTreeObjects
    case GetMethods => sender ! getMethodsXML
    case GetRepository => sender ! getRepository
    case GetSimulationModel => sender ! getSimulationModel
    case UpdateTrees => {
      dataTree.content = updateTrees
      // @TODO update also methods
    }
    //case _ => sender ! Json.obj("error" -> "message not found")
    case _ => sender ! <error>message not found in data provider</error>
  }

  protected def getMetaData: Database = {
    try {
      Await.result(ConfigService.request(GetDatabaseByName(self.path.name)).mapTo[Database], 5.seconds)
    } catch {
      // if config service fails just provide what is there!
      case e: TimeoutException => Database(self.path.name, None, Nil, Nil, Nil, Nil, "", 
          Simulation, UrlProvider.getURI("impex", self.path.name))
    }
  }

  protected def getTreeObjects: Seq[Spase] = {
    dataTree.content map { tree => scalaxb.fromXML[Spase](tree) }
  }

  protected def getRepository: Spase = {
    val records = getTreeObjects flatMap { 
      tree => tree.ResourceEntity.filter(c => c.key.get == "Repository") map {
        // @TODO still we have to do it like this, why?
        repo => DataRecord(None, Some("Repository"), (scalaxb.fromXML[Repository](repo.as[NodeSeq])))
      }
    }
    Spase(Number2u462u462, records, "en")
  }
  
  protected def getSimulationModel: Spase = {
    val records = getTreeObjects flatMap {
      tree => tree.ResourceEntity.filter(c => c.key.get == "SimulationModel") map {
        model => DataRecord(None, Some("SimulationModel"), model.as[SimulationModelType])
      }
    }
    Spase(Number2u462u462, records, "en")
  }
  
  protected def getSimulationRun: Spase = {
    val records = getTreeObjects flatMap {
      tree => tree.ResourceEntity.filter(c => c.key.get == "SimulationRun") map {
        run => DataRecord(None, Some("SimulationRun"), run.as[SimulationRun])
      }
    }
    Spase(Number2u462u462, records, "en")
  }
  
  protected def getNumericalOutput: Spase = ???
  
  protected def getGranule: Spase = ???

}

