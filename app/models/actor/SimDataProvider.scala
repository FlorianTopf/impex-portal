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
    case GetTrees(Some("xml")) => sender ! getTreeXML
    case GetTrees(None) => sender ! getTreeObjects
    case GetMethods => sender ! getMethodsXML
    case GetRepository => sender ! getRepository
    case GetElement(dType, id) => dType match {
      case SimulationModel => sender ! getSimulationModel(id)
      case SimulationRun => sender ! getSimulationRun(id)
      case NumericalOutput => sender ! getNumericalOutput(id)
      case Granule => sender ! getGranule(id)
    }
    case UpdateTrees => {
      dataTree.content = updateTrees
      // @TODO update also methods
    }
    //case _ => sender ! Json.obj("error" -> "message not found")
    case _ => sender ! <error>message not found in data provider</error>
  }
  
  // @TODO improve header later (namespaces)
  protected def getTreeXML: NodeSeq = {
    val trees = dataTree.content
    <Spase xmlns="http://impex-fp7.oeaw.ac.at" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      { trees(0).\("_").filter(_.label == "Version") }
      { trees.map(tree => tree.\("_").filter(_.label != "Version")) }
    </Spase>
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
    dataTree.content map { scalaxb.fromXML[Spase](_) }
  }

  protected def getRepository: Spase = {
    val records = getTreeObjects flatMap { 
      _.ResourceEntity.filter(_.key.get == "Repository") map {
        // @TODO still we have to do it like this, why?
        repo => DataRecord(None, Some("Repository"), scalaxb.fromXML[Repository](repo.as[NodeSeq]))
      }
    }
    Spase(Number2u462u462, records, "en")
  }
  
  // @TODO getSimulationElement with type classes
  protected def getSimulationModel(id: Option[String]): Spase = {
    val records = getTreeObjects flatMap {
      _.ResourceEntity.filter(_.key.get == "SimulationModel") map {
        _.as[SimulationModelType]
      }
    }
    val models = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
    Spase(Number2u462u462, models.map(m => DataRecord(None, Some("SimulationModel"), m)), "en")
  }
  
  protected def getSimulationRun(id: Option[String]): Spase = {
    val records = getTreeObjects flatMap {
      _.ResourceEntity.filter(_.key.get == "SimulationRun") map {
        // @FIXME still doesn't work the correct way
        run => scalaxb.fromXML[SimulationRun](run.as[NodeSeq])
      }
    }
    val runs = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
    Spase(Number2u462u462, runs.map(r => DataRecord(None, Some("SimulationRun"), r)), "en")
  }
  
  protected def getNumericalOutput(id: Option[String]): Spase = {
    val records = getTreeObjects flatMap {
      _.ResourceEntity.filter(_.key.get == "NumericalOutput") map {
        _.as[NumericalOutput]
      }
    }
    val outputs = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
    Spase(Number2u462u462, outputs.map(r => DataRecord(None, Some("NumericalOutput"), r)), "en")
  }
  
  protected def getGranule(id: Option[String]): Spase = {
    val records = getTreeObjects flatMap {
      _.ResourceEntity.filter(_.key.get == "Granule") map {
        granule => {
          // @FIXME still doesn't work the correct way
          scalaxb.fromXML[Granule](granule.as[NodeSeq])
        }
      }
    }
    val granules = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
    Spase(Number2u462u462, granules.map(r => DataRecord(None, Some("Granule"), r)), "en")
  }

}

