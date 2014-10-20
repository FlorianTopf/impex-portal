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
  Calendar,
  Date,
  GregorianCalendar}
import java.text.{
  DateFormat, SimpleDateFormat
}
import javax.xml.datatype._


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
      case EObservatory => sender ! getObservatory(id, r)
      case EInstrument => sender ! getInstrument(id, r)
      case ENumericalData => sender ! getNumericalData(id, r)
    }
    case UpdateData => sender ! updateData
    case GetStatus => sender ! getStatus
  }
    
  private def getObservatory(id: Option[String], r: Boolean): Spase = {
    val records = getTreeObjects("Observatory") map {
      obs => scalaxb.fromXML[Observatory](obs.as[NodeSeq])
    }
    val obs = id match {
      case Some(id) => records.filter(_.ResourceID.contains(id))
      case None => records
    }
    if(r == true) {
      val rRecords = getTreeObjects("Repository")++
    		  obs.map(m => DataRecord(None, Some("Observatory"), m))
      Spase(Number2u462u462, rRecords, "en")
    } else {
      Spase(Number2u462u462, obs.map(m => DataRecord(None, Some("Observatory"), m)), "en")
    }
  }
  
  private def getInstrument(id: Option[String], r: Boolean): Spase = {
    val records = getTreeObjects("Instrument") map {
      run => scalaxb.fromXML[Instrument](run.as[NodeSeq])
    }
    val instr = id match {
      // we make a combined search here => both self id and parent id
      case Some(id) => records.filter(r => (r.ResourceID.contains(id) || r.ObservatoryID.contains(id)))
      case None => records
    }
    if(r == true) {
      //println("ObservatoryID="+records.head.ObservatoryID)
      val rRecords = getObservatory(Some(instr.head.ObservatoryID), true).ResourceEntity++
      		instr.map(r => DataRecord(None, Some("Instrument"), r))
      Spase(Number2u462u462, rRecords, "en")	  
    } else {
      Spase(Number2u462u462, instr.map(r => DataRecord(None, Some("Instrument"), r)), "en")
    }
  }
  
  private def getNumericalData(id: Option[String], r: Boolean): Spase = {
    val records = getTreeObjects("NumericalData") map {
      data => scalaxb.fromXML[NumericalData](data.as[NodeSeq])
    }
    // we make a combined search here => both self id and parent id
    val data = id match {
      case Some(id) => records.filter(r => (r.ResourceID.contains(id) || r.InstrumentID.contains(id)))
      case None => records
    }
    if(r == true) {
      //println("InstrumentID="+records.head.InstrumentID)
      val rRecords = getInstrument(Some(records.head.InputResourceID.head), true).ResourceEntity++
            data.map(r => DataRecord(None, Some("NumericalData"), r))
      Spase(Number2u462u462, rRecords, "en")
    } else {
      Spase(Number2u462u462, data.map(r => DataRecord(None, Some("NumericalData"), r)), "en")
    }
  } 

}
