package models.actor

import models.binding._
import models.provider._
import akka.actor._
import akka.pattern.ask
import scala.xml._
import scala.concurrent._
import scala.concurrent.duration._
import scalaxb.DataRecord

class ObsDataProvider(val dataTree: Trees, val accessMethods: Methods) 
extends Actor with DataProvider[DataRoot] {
  import models.actor.ConfigService._
  import models.actor.DataProvider._
  
  // @TODO unified error messages
  def receive = {
    // @TODO return unified tree in XML
    case GetTrees(Some("xml")) => sender ! getTreeXML
    case GetTrees(None) => sender ! getTreeObjects
    case GetMethods => sender ! getMethodsXML
    case GetRepository => sender ! getRepository
    case GetElement(dType, id) => dType match {
      case Observatory => sender ! getObservatory(id)
      case Instrument => sender ! getInstrument(id)
      case NumericalData => sender ! getNumericalData(id)
    }
    // @TODO update also methods
    case UpdateTrees => {
      dataTree.content = updateTrees
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
          Observation, UrlProvider.getURI("impex", self.path.name))
    }
  }

  protected def getTreeObjects: Seq[DataRoot] = {
    dataTree.content map { tree => scalaxb.fromXML[DataRoot](tree) }
  }

  protected def getRepository: Spase = {
    val records = getTreeObjects flatMap { _.dataCenter map { dataCenter => 
        val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
        val resourceHeader = ResourceHeader(dataCenter.id.toString, Nil, TimeProvider.getISONow, 
           None, dataCenter.name, None, Seq(contact))
        val accessURL = AccessURL(None , getMetaData.info)
        // resource name is the "real" id for the proprietary data model of AMDA and CLWeb
        // this will be mapped all the time when calling web services.
        //val resourceID = getMetaData.id.toString+"/"+dataCenter.id.toString
        // @TODO atm we do not need this => has only one datacenter
        val resourceID = getMetaData.id.toString
        DataRecord(None, Some("Repository"), Repository(resourceID, resourceHeader, accessURL))
      }
    }
    Spase(Number2u462u462, records, "en")
  }
  
  //@TODO finalise this and continue ONLY with sim-stuff
  protected def getObservatory(id: Option[String]) = {
    val records = getTreeObjects flatMap { _.dataCenter map { 
        _.datacenteroption.filter(_.key.get == "mission").map(_.as[Mission])   
      }
    }
    val missions = id match {
      case Some(id) => records.flatten.filter(_.id == id)
      case None => records.flatten
    }
    missions map { mission => 
      val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
      val informationURL = InformationURL(None, getMetaData.info)
      val resourceHeader = ResourceHeader(mission.name, Nil, TimeProvider.getISONow, 
           None, mission.desc, None, Seq(contact))
      val resourceID = getMetaData.id.toString+"/"+mission.id.toString
      
      
    }

  }
  
  protected def getInstrument(id: Option[String]): Spase = ???
  
  protected def getNumericalData(id: Option[String]): Spase = ???

}

