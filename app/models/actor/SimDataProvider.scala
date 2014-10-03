package models.actor

import models.binding._
import models.provider._
import akka.actor._
import akka.pattern.ask
import scala.xml._
import scala.concurrent._
import scala.concurrent.duration._
import scalaxb.DataRecord


class SimDataProvider(var metadata: Database) 
extends Actor with DataProvider {
  import models.actor.ConfigService._
  import models.actor.DataProvider._
  
  override def preStart = initData
  
  def receive = {
    case GetTree => sender ! getTreeObjects
    case GetMethods => sender ! getMethods
    case GetElement(dType, id, r) => dType match {
      case ERepository => sender ! getRepository(id)
      case ESimulationModel => sender ! getSimulationModel(id, r)
      case ESimulationRun => sender ! getSimulationRun(id, r)
      case ENumericalOutput => sender ! getNumericalOutput(id, r)
      case EGranule => sender ! getGranule(id, r)
    }
    case UpdateData => sender ! updateData
  }
  
  private def getSimulationModel(id: Option[String], r: Boolean): Spase = {
    val records = getTreeObjects("SimulationModel") map {
      _.as[SimulationModel]
    }
    val models = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
    if(r == true) {
      val rRecords = getTreeObjects("Repository")++
    		  models.map(m => DataRecord(None, Some("SimulationModel"), m))
      Spase(Number2u462u462, rRecords, "en")
    } else {
      Spase(Number2u462u462, models.map(m => DataRecord(None, Some("SimulationModel"), m)), "en")
    }
  }
  
  private def getSimulationRun(id: Option[String], r: Boolean): Spase = {
    val records = getTreeObjects("SimulationRun") map {
      run => scalaxb.fromXML[SimulationRun](run.as[NodeSeq])
    }
    val runs = id match {
      // we make a combined search here => both self id and parent id
      case Some(id) => records.filter(r => (r.ResourceID.contains(id) || r.Model.ModelID.contains(id)))
      case None => records
    }
    if(r == true) {
      //println("ModelID="+records.head.Model.ModelID)
      val rRecords = getSimulationModel(Some(runs.head.Model.ModelID), true).ResourceEntity++
      		runs.map(r => DataRecord(None, Some("SimulationRun"), r))
      Spase(Number2u462u462, rRecords, "en")	  
    } else {
      Spase(Number2u462u462, runs.map(r => DataRecord(None, Some("SimulationRun"), r)), "en")
    }
  }
  
  private def getNumericalOutput(id: Option[String], r: Boolean): Spase = {
    val records = getTreeObjects("NumericalOutput") map {
      _.as[NumericalOutput]
    }
    // we make a combined search here => both self id and parent id
    val outputs = id match {
      case Some(id) => records.filter(r => (r.ResourceID.contains(id) || r.InputResourceID.contains(id)))
      case None => records
    }
    if(r == true) {
      //println("InputResourceID="+records.head.InputResourceID.head)
      val rRecords = getSimulationRun(Some(records.head.InputResourceID.head), true).ResourceEntity++
            outputs.map(r => DataRecord(None, Some("NumericalOutput"), r))
      Spase(Number2u462u462, rRecords, "en")
    } else {
      Spase(Number2u462u462, outputs.map(r => DataRecord(None, Some("NumericalOutput"), r)), "en")
    }
  }
  
  private def getGranule(id: Option[String], r: Boolean): Spase = {
    val records = getTreeObjects("Granule") map {
      granule => scalaxb.fromXML[Granule](granule.as[NodeSeq])
    }
    // we make a combined search here => both self id and parent id
    val granules = id match {
      case Some(id) => records.filter(r => (r.ResourceID.contains(id) || r.ParentID.contains(id)))
      case None => records
    }
    if(r == true) {
      //println("ParentID="+records.head.ParentID)
      val rRecords = getNumericalOutput(Some(records.head.ParentID), true).ResourceEntity++
            granules.map(r => DataRecord(None, Some("Granule"), r))
      Spase(Number2u462u462, rRecords, "en")
    } else {
      Spase(Number2u462u462, granules.map(r => DataRecord(None, Some("Granule"), r)), "en")
    }
  }

}
