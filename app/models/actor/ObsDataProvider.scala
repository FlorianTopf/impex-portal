package models.actor

import models.binding._
import models.provider._
import akka.actor._
import akka.pattern.ask
import scala.xml._
import scala.concurrent._
import scala.concurrent.duration._
import scalaxb.DataRecord
import java.util.{
  Date,
  GregorianCalendar}
import java.text.{
  DateFormat, SimpleDateFormat
}
import javax.xml.datatype._
import java.util.Calendar


class ObsDataProvider(var metadata: Database) 
extends Actor with DataProvider {
  import models.actor.ConfigService._
  import models.actor.DataProvider._
  
  override def preStart = initData
  
  def receive = {
    case GetTree => sender ! getTreeObjects
    case GetMethods => sender ! getMethods
    case GetElement(dType, id, r) => dType match {
      case ERepository => sender ! getRepository(id)
      case EObservatory => sender ! getObservatory(id)
      case EInstrument => sender ! getInstrument(id)
      case ENumericalData => sender ! getNumericalData(id)
    }
    case UpdateData => sender ! updateData
  }
  
  protected def getTreeObjects: Spase = {
    Spase(Number2u462u462, 
      getRepository(None).ResourceEntity++
      getObservatory(None).ResourceEntity++
      getInstrument(None).ResourceEntity++
      getNumericalData(None).ResourceEntity, "en")
  }

  protected def getNativeTreeObjects: Seq[DataRoot] = {
    getTrees map { tree => { 
      // we select here only portions of the tree (e.g. AMDA)
      val selection = 
        <dataRoot>
          { tree \\ "dataCenter" filterNot { element => 
            // CLWEB@IRAP is already natively included
            // Simulations are already natively included
            (element \\ "@name" exists (_.text.contains("CLWEB@IRAP"))) ||
   			(element \\ "@isSimulation" exists (_.text == "true"))
   		  }}
        </dataRoot> 
      
      scalaxb.fromXML[DataRoot](selection) 
    }}
  }

  protected def getTreeObjects(element: String): Seq[DataRecord[Any]] = {
    val records = getNativeTreeObjects flatMap { _.dataCenter map { 
      _.datacenteroption.filter(_.key.get == element) }}
    records.flatten
  }


  // @TODO we must ensure that there is only one repository per tree
  protected def getRepository(id: Option[String]): Spase = {
    val records = getNativeTreeObjects flatMap { _.dataCenter map { dataCenter => 
        val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
        val resourceHeader = ResourceHeader(dataCenter.id.toString, Nil, TimeProvider.getISONow, 
           None, dataCenter.name, None, Seq(contact))
        val accessURL = AccessURL(None , getMetaData.info)
        // we have only one repository, so we take the id from the config
        val resourceId = getMetaData.id.toString
        //val resourceId = getMetaData.id.toString+"/Repository/"+dataCenter.id.toString
        DataRecord(None, Some("Repository"), Repository(resourceId, resourceHeader, accessURL))
      }
    }
    Spase(Number2u462u462, records, "en")
  }
  
  private def createId(element: ObsElement): String = {
    getMetaData.id.toString.replace(ERepository.toString, element.toString)   
  }
  
  // @TODO add recursion for the remaining elements
  private def getObservatory(id: Option[String]): Spase = {
    import models.binding.Observatory
    val records = getTreeObjects("mission").map(_.as[Mission])   
    val missions = id match {
      case Some(id) if(id != getMetaData.id.toString) => { 
        // creating and matching the proprietary Id
        val propId = id.replace(createId(EObservatory)+"/", "")
        records.filter(_.id.replaceAll(" ", "_") == propId)
      }
      case _ => records
    }
   Spase(Number2u462u462, missions flatMap { mission => 
      val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
      val informationURL = InformationURL(None, getMetaData.info)
      val resourceHeader = ResourceHeader(mission.name, Nil, TimeProvider.getISONow, 
           None, mission.desc, None, Seq(contact))
      // replace all whitespaces with underscore
      val resourceId = createId(EObservatory)+"/"+mission.id.toString.replaceAll(" ", "_")
      // Location/ObservatoryRegion is taken from target/targets attribute
      var regions = mission.target match {
        case Some(t) => Seq(t)
        case None => Seq()
      }
      mission.targets match {
        case Some(t) =>  regions++Seq(t.split(" "))
        case None =>  regions++Seq()
        
      }
      val location = Location(regions.map(r => EnumRegion.fromString(r, scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at"))))
      
      if(mission.missionoption.exists(_.key.get == "observatory")) {
        val observatories = mission.missionoption.filter(_.key.get == "observatory") map(_.as[ObservatoryType])
        observatories map { o => 
          // taking the parent mission description here
          val resourceHeader = ResourceHeader(o.name, Nil, TimeProvider.getISONow, 
           None, mission.desc, None, Seq(contact))
          // replace all whitespaces with underscore
          val resourceId = createId(EObservatory)+"/"+o.id.toString.replaceAll(" ", "_")
          // taking the parent mission targets here
          DataRecord(None, Some("Observatory"), Observatory(resourceId, resourceHeader, Nil, location, None, Nil))
        }
      } else {
        Seq(DataRecord(None, Some("Observatory"), Observatory(resourceId, resourceHeader, Nil, location, None, Nil)))
      }
    }, "en")
  }
  
  private def getInstrument(id: Option[String]): Spase = {
    val records = getTreeObjects("mission").map(_.as[Mission])
    val missions: Seq[(String, Seq[InstrumentType])] = records map { record => 
      val missionId = createId(EObservatory)+"/"+record.id.toString.replaceAll(" ", "_")
      // @FIXME 
      // observatory/instrument is not included
      // simulationmodel/instrument is not included
      if(record.missionoption.exists(_.key.get == "group")) {
        val groups = record.missionoption.filter(_.key.get == "group").map(_.as[GroupType])
        (missionId, record.missionoption.filter(_.key.get == "instrument").map(_.as[InstrumentType])++ 
            groups.filter(_.restricted!="1").flatMap(g => g.instrument))
      }
      else {
        (missionId, record.missionoption.filter(_.key.get == "instrument").map(_.as[InstrumentType]))
      }
    }
    // @TODO here we need to filter the instruments for requested id
    Spase(Number2u462u462, missions flatMap { instruments =>
      val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
      instruments._2 map { instrument =>
      	val resourceHeader = ResourceHeader(instrument.name, Nil, TimeProvider.getISONow,
          None, instrument.desc, None, Seq(contact))
        // replace all whitespaces with underscore, replace @ with __at__
        val resourceId = createId(EInstrument)+"/"
        	instrument.id.toString.replaceAll(" ", "_").replaceAll("@", "__at__")
        // @FIXME InstrumentType is missing for Instrument
        val instrumentType = Seq(Magnetometer)
        // @FIXME Investigation name is mandadory => take name
        DataRecord(None, Some("Instrument"), 
            Instrument(resourceId, resourceHeader, instrumentType, Seq(instrument.name), None, instruments._1))
      }
    }, "en")
  }
  
  private def getNumericalData(id: Option[String]): Spase = {
    // format needed for startDate and stopDate
    val df: DateFormat = new SimpleDateFormat("yyyy/MM/dd")
    val cal: GregorianCalendar = new GregorianCalendar
    
    val records = getTreeObjects("mission").map(_.as[Mission])
    val missions: Seq[(String, Seq[String], Seq[InstrumentType])] = records map { record => 
      // replace all whitespaces with underscore
      val missionId = createId(EObservatory)+"/"+record.id.toString.replaceAll(" ", "_")
      var regions = record.target match {
        case Some(t) => Seq(t)
        case None => Seq()
      }
      record.targets match {
        case Some(t) => regions++Seq(t.split(" "))
        case None => regions++Seq()  
      }
      // @FIXME  
      // group/instrument missing (can be kicked out if restricted=1)
      // observatory/instrument is not included
      // simulationmodel/instrument is not included
      (missionId, regions, record.missionoption.filter(_.key.get == "instrument").map(_.as[InstrumentType]))
    }
    // @TODO here we need to filter the numerical data for requested id
    Spase(Number2u462u462, missions flatMap { instruments =>
      val contact = Contact(getMetaData.id.toString, Seq(ArchiveSpecialist))
      
      instruments._3 flatMap { instrument =>
        // replace all whitespaces with underscore, replace @ with __at__
        val instrumentId = createId(EInstrument)+"/"+
        	instrument.id.toString.replaceAll(" ", "_").replaceAll("@", "__at__")
          
        instrument.dataset map { dataset => 
      		val resourceHeader = ResourceHeader(dataset.name, Nil, TimeProvider.getISONow,
      		    None, dataset.desc.getOrElse(""), None, Seq(contact))
            // replace all whitespaces with underscore, replace @ with __at__
      		val resourceId = createId(ENumericalData)+"/"+
      			instrument.id.toString.replaceAll(" ", "_").replaceAll("@", "__at__")
            // @TODO url is only data center location => dataSource cannot be used
            val accessURL = AccessURL(None, getMetaData.info) 
            // we only have one repository (datacenter) per file
            val accessInfo = AccessInformation(getMetaData.id.toString, None, None, 
                Seq(accessURL), VOTableValue, Some(ASCIIValue))
            
            var startTime: XMLGregorianCalendar = TimeProvider.getISONow
            var stopTime: XMLGregorianCalendar = TimeProvider.getISONow   
            
            // french coders are very nasty
            if(dataset.dataStart != Some("depending on mission") && 
                dataset.dataStop != Some("depending on mission") && 
                dataset.dataStart != None && dataset.dataStop != None) {
              cal.setTime(df.parse(dataset.dataStart.get))
              startTime = 
              DatatypeFactory.newInstance().newXMLGregorianCalendar(
                  cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 
                  cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0, 0)
              cal.setTime(df.parse(dataset.dataStop.get))
              stopTime = 
              DatatypeFactory.newInstance().newXMLGregorianCalendar(
                  cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 
                  cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0, 0, 0)
            }
            
      		var cadence = dataset.sampling match {
      		  case None => ""
      		  case Some(s) => s.toUpperCase
      		}
      		if(cadence.toUpperCase.contains("HZ") || cadence == "DEPENDING ON MISSION")
      		  cadence = ""
                
            val tempDescription = TemporalDescription(
                TimeSpan(startTime, DataRecord(None, Some("StopDate"), stopTime)), cadence match {
                  case "" => None
                  case _ => Some(DatatypeFactory.newInstance().newDuration("PT"+cadence))
                }
            )
            
            // @FIXME MeasurementType is missing for NumericalData
            val measurementType = MagneticField
            // @TODO atm we apply all regions of the mission 
            // to a numerical data element of that mission
            // if there is no target set for the original dataset element
            val observedRegion = dataset.target match {
      		  case Some(o) if(o != "") => Seq(EnumRegion.fromString(o, scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
      		  case _ => instruments._2.map(m => EnumRegion.fromString(m, scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
      		}
            
            // @TODO these procedure is very tricky 
            //(multiple optional/mandatory elements)
            // what about vectors? (e.g coordinates)
            val parameter = dataset.parameter map {
      		  param => 
      		    val elements = param.component.map{ c => Element(c.name, Nil, 
      		         Seq(param.component.indexOf(c)+1), Some(c.id)) }
      		    var structure: Option[Structure] = None
      		    // additional Element:
      		    // support => Positional or Temporal
      		    // field => Quantity must be known
      		    var addElem: DataRecord[Any] = 
      		      DataRecord(None, Some("Support"), Support(Nil, Positional))
      		    // match position identifiers (experimental)
      		    if(param.id.contains("xyz") || dataset.id.contains("ephem")) {
      		       addElem = DataRecord(None, Some("Support"), Support(Nil, Positional))
      		    }
      		    // we need to match the time
      		    else {
      		      // else we put here a field element
      		      // @FIXME Field/Quantity is missing for Parameter
      		      addElem = DataRecord(None, Some("Field"), FieldType(Nil, Magnetic))
      		    }	    
      		    if(elements.size > 1) {
      		      structure = 
      		      Some(Structure(Seq(param.component.size), None, 
      		            elements))
      		    }
      		    
      		    Parameter(param.name, Nil, Some(param.id), param.desc, None,
      		        None, param.units, None, None, Nil, structure, None, None, None, 
      		        addElem, Nil)
      		}
     
            // input resource Id = instrument Id
            DataRecord(None, Some("NumericalData"), 
             NumericalData(resourceId, resourceHeader, Seq(accessInfo), None, None, None, None, 
                 Seq(instrumentId), Seq(measurementType), Some(tempDescription), Nil, observedRegion, 
                 None, Seq(dataset.att.getOrElse("")), Seq(instrumentId), parameter, Nil))
        }
      }
    }, "en") 
  } 

}

