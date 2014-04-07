package models.actor

import models.binding._
import models.provider._
import akka.actor._
import akka.pattern.ask
import scala.xml._
import scala.concurrent._
import scala.concurrent.duration._
import scalaxb.DataRecord

class ObsDataProvider(var metadata: Database) 
extends Actor with DataProvider {
  import models.actor.ConfigService._
  import models.actor.DataProvider._
  
  override def preStart = initData(metadata)
  
  def receive = {
    // @TODO return unified tree in XML
    // @FIXME we only serve what we have atm
    case GetTree => sender ! getTreeObjects
    case GetMethods => sender ! getMethods
    case GetElement(dType, id, r) => dType match {
      case ERepository => sender ! getRepository(id)
      case EObservatory => sender ! getObservatory(id)
      case EInstrument => sender ! getInstrument(id)
      case ENumericalData => sender ! getNumericalData(id)
    }
    case UpdateData => updateData(getMetaData)
  }
  
  protected def getTreeObjects: Spase = {
    Spase(Number2u462u462, 
      getRepository(None).ResourceEntity++
      getObservatory(None).ResourceEntity++
      getInstrument(None).ResourceEntity, "en")
  }

  protected def getNativeTreeObjects: Seq[DataRoot] = {
    getTrees map { tree => scalaxb.fromXML[DataRoot](tree) }
  }

  protected def getTreeObjects(element: String): Seq[DataRecord[Any]] = {
    val records = getNativeTreeObjects flatMap { _.dataCenter map { 
      _.datacenteroption.filter(_.key.get == element) }}
    records.flatten
  }

  // @TODO finalise access methods
  // @FIXME must search for an specific ID (multiple Repositories per Authority)
  protected def getRepository(id: Option[String]): Spase = {
    println("RepositoryID="+id)
    val records = getNativeTreeObjects flatMap { _.dataCenter map { dataCenter => 
        val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
        val resourceHeader = ResourceHeader(dataCenter.id.toString, Nil, TimeProvider.getISONow, 
           None, dataCenter.name, None, Seq(contact))
        val accessURL = AccessURL(None , getMetaData.info)
        // resource name is the "real" id for the proprietary data model of AMDA and CLWeb
        // this will be mapped all the time when calling web services.
        //val resourceID = getMetaData.id.toString+"/"+dataCenter.id.toString
        // @FIXME ATM we only take one datacenter per file
        val resourceId  = getMetaData.id.toString
        DataRecord(None, Some("Repository"), Repository(resourceId, resourceHeader, accessURL))
      }
    }
    Spase(Number2u462u462, records, "en")
  }
  
  private def getObservatory(id: Option[String]): Spase = {
    import models.binding.Observatory
    val records = getTreeObjects("mission").map(_.as[Mission])   
    val missions = id match {
      // @FIXME improve this check
      case Some(id) if(id != getMetaData.id.toString) => { 
        // matching the proprietary ID
        val propId = id.replace(getMetaData.id.toString+"/", "")
        records.filter(_.id.replaceAll(" ", "_") == propId)
      }
      case _ => records
    }
   Spase(Number2u462u462, missions map { mission => 
      val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
      val informationURL = InformationURL(None, getMetaData.info)
      val resourceHeader = ResourceHeader(mission.name, Nil, TimeProvider.getISONow, 
           None, mission.desc, None, Seq(contact))
      // replace all whitespaces with underscore
      val resourceId = getMetaData.id.toString+"/Observatory/"+mission.id.toString.replaceAll(" ", "_")
      // @FIXME this is very tricky => we can only manually crosscheck!
      val location = Location(Seq(Earth))
      DataRecord(None, Some("Observatory"), Observatory(resourceId, resourceHeader, Nil, location, None, Nil))    
    }, "en")
  }
  
  private def getInstrument(id: Option[String]) = {
    val records = getTreeObjects("mission").map(_.as[Mission])
    // @FIXME created problems with more than one mission
    val missions: Seq[(String, Seq[Instrument])] = records map { record => 
      val parentId = getMetaData.id.toString+"/Observatory/"+record.id.toString.replaceAll(" ", "_")
      (parentId, record.missionoption.filter(_.key.get == "instrument").map(_.as[Instrument]))
    }
    // @FIXME here we need to filter the instruments for requested id
    Spase(Number2u462u462, missions flatMap { instruments =>
      val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
      instruments._2 map { instrument =>
        // @FIXME we do not add instrument.att at the moment
      	val resourceHeader = ResourceHeader(instrument.name, Nil, TimeProvider.getISONow,
          None, instrument.desc, None, Seq(contact))
        // replace all whitespaces with underscore ... replace @ with __at__
        val resourceId = getMetaData.id.toString+"/Instrument/"+instrument.id.toString.replaceAll(" ", "_").replaceAll("@", "__at__")
        // @FIXME this is very tricky => we can only manually crosscheck!
        val instrumentType = Seq(Magnetometer)
        // @FIXME investigation name is mandadory => take name
        DataRecord(None, Some("Instrument"), InstrumentType(resourceId, resourceHeader, instrumentType, Seq(instrument.name), None, instruments._1))
      }
    }, "en")
  }
  
  private def getNumericalData(id: Option[String]): Spase = ???

}

