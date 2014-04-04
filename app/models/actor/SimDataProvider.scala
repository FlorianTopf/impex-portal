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
extends Actor with DataProvider {
  import models.actor.ConfigService._
  import models.actor.DataProvider._
  
  // @TODO unified error messages
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
  
  protected def getTreeObjects: Spase = {
    val spase = dataTree.content flatMap { tree =>
    	scalaxb.fromXML[Spase](tree).ResourceEntity
  	}
    Spase(Number2u462u462, spase, "en")
  }
  
  protected def getTreeObjects(element: String): Seq[DataRecord[Any]] = {
    getTreeObjects.ResourceEntity.filter(_.key.get == element)
  }
  
  protected def getRepository(id: Option[String]): Spase = {
    println("RepositoryID="+id)
    val records = getTreeObjects("Repository") map {
	  repo => scalaxb.fromXML[Repository](repo.as[NodeSeq])
    }
    val repos = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
	Spase(Number2u462u462, repos.map(r => DataRecord(None, Some("Repository"), r)), "en")
  }
  
  private def getSimulationModel(id: Option[String], recursive: Boolean): Spase = {
    val records = getTreeObjects("SimulationModel") map {
      _.as[SimulationModelType]
    }
    val models = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
    if(recursive == true) {
      val rRecords = getTreeObjects("Repository")++
    		  models.map(m => DataRecord(None, Some("SimulationModel"), m))
      Spase(Number2u462u462, rRecords, "en")
    } else {
      Spase(Number2u462u462, models.map(m => DataRecord(None, Some("SimulationModel"), m)), "en")
    }
  }
  
  //@FIXME needs to return empty result if there is no run stored in the file 
  // (check if records are empty)
  private def getSimulationRun(id: Option[String], recursive: Boolean): Spase = {
    val records = getTreeObjects("SimulationRun") map {
      run => scalaxb.fromXML[SimulationRun](run.as[NodeSeq])
    }
    val runs = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
    if(recursive == true) {
      println("ModelID="+records.head.Model.ModelID)
      val rRecords = getSimulationModel(Some(records.head.Model.ModelID), true).ResourceEntity++
      		runs.map(r => DataRecord(None, Some("SimulationRun"), r))
      Spase(Number2u462u462, rRecords, "en")	  
    } else {
      Spase(Number2u462u462, runs.map(r => DataRecord(None, Some("SimulationRun"), r)), "en")
    }
  }
  
  private def getNumericalOutput(id: Option[String], recursive: Boolean): Spase = {
    val records = getTreeObjects("NumericalOutput") map {
      _.as[NumericalOutput]
    }
    val outputs = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
    if(recursive == true) {
      println("InputResourceID="+records.head.InputResourceID.head)
      val rRecords = getSimulationRun(Some(records.head.InputResourceID.head), true).ResourceEntity++
            outputs.map(r => DataRecord(None, Some("NumericalOutput"), r))
      Spase(Number2u462u462, rRecords, "en")
    } else {
      Spase(Number2u462u462, outputs.map(r => DataRecord(None, Some("NumericalOutput"), r)), "en")
    }
  }
  
  private def getGranule(id: Option[String], recursive: Boolean): Spase = {
    val records = getTreeObjects("Granule") map {
      granule => scalaxb.fromXML[Granule](granule.as[NodeSeq])
    }
    val granules = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
    Spase(Number2u462u462, granules.map(r => DataRecord(None, Some("Granule"), r)), "en")
  }

}