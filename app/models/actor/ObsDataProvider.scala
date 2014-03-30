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
    case GetElement(dType, id, r) => dType match {
      case ERepository => sender ! getRepository(None)
      case EObservatory => sender ! getObservatory(id)
      case EInstrument => sender ! getInstrument(id)
      case ENumericalData => sender ! getNumericalData(id)
    }
    // @TODO update also methods
    case UpdateTrees => {
      dataTree.content = updateTrees
    }
    //case _ => sender ! Json.obj("error" -> "message not found")
    case _ => sender ! <error>message not found in data provider</error>
  }
  
  protected def getTreeXML: NodeSeq = {
    val trees = dataTree.content 
    // @FIXME we only serve the first tree atm
    trees(0)
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

  // @FIXME must search for an specific ID (multiple Repositories per Authority)
  protected def getRepository(id: Option[String]): Spase = {
    val records = getTreeObjects flatMap { _.dataCenter map { dataCenter => 
        val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
        val resourceHeader = ResourceHeader(dataCenter.id.toString, Nil, TimeProvider.getISONow, 
           None, dataCenter.name, None, Seq(contact))
        val accessURL = AccessURL(None , getMetaData.info)
        // resource name is the "real" id for the proprietary data model of AMDA and CLWeb
        // this will be mapped all the time when calling web services.
        //val resourceID = getMetaData.id.toString+"/"+dataCenter.id.toString
        // @FIXME atm we only take one datacenter per file
        val resourceID = getMetaData.id.toString
        DataRecord(None, Some("Repository"), Repository(resourceID, resourceHeader, accessURL))
      }
    }
    Spase(Number2u462u462, records, "en")
  }
  
  private def getTreeObjects(element: String): Seq[DataRecord[Any]] = {
    val records = getTreeObjects flatMap { _.dataCenter map { 
      _.datacenteroption.filter(_.key.get == element) }}
    records.flatten
  }
  
  private def getObservatory(id: Option[String]): Spase = {
    import models.binding.Observatory
    val records = getTreeObjects("mission").map(_.as[Mission])   
    val missions = id match {
      case Some(id) if(id != getMetaData.id.toString) => { 
        // matching the proprietary ID
        val propID = id.replace(getMetaData.id.toString+"/", "")
        records.filter(_.id.replaceAll(" ", "_") == propID)
      }
      case _ => records
    }
   Spase(Number2u462u462, missions map { mission => 
      val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
      val informationURL = InformationURL(None, getMetaData.info)
      val resourceHeader = ResourceHeader(mission.name, Nil, TimeProvider.getISONow, 
           None, mission.desc, None, Seq(contact))
      // replace all whitespaces with underscore
      val resourceID = getMetaData.id.toString+"/"+mission.id.toString.replaceAll(" ", "_")
      // @FIXME this is very tricky => we can only manually crosscheck!
      val location = Location(Seq(Earth))
      DataRecord(None, Some("Observatory"), Observatory(resourceID, resourceHeader, Nil, location, None, Nil))    
    }, "en")
  }
  
  private def getInstrument(id: Option[String]): Spase = ???
  
  private def getNumericalData(id: Option[String]): Spase = ???

}

