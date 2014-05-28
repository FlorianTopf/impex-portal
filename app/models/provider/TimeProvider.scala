package models.provider

import java.util.{GregorianCalendar, Date}
import javax.xml.datatype._
import java.text.SimpleDateFormat

object TimeProvider {
  def getISONow: XMLGregorianCalendar = {
    val gregorianCalendar = new GregorianCalendar()
    val datatypeFactory = DatatypeFactory.newInstance()
    datatypeFactory.newXMLGregorianCalendar(gregorianCalendar)
  }
  
  def getISODate(date: String): XMLGregorianCalendar = {
    // @TODO recollect this in the client design, the string must be
    // already in iso format
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val isoDate: Date = dateFormat.parse(date)
    val gregorianCalendar = new GregorianCalendar()
    gregorianCalendar.setTime(isoDate)
    val datatypeFactory = DatatypeFactory.newInstance()
    datatypeFactory.newXMLGregorianCalendar(gregorianCalendar)
  }
  
  // this is provided in seconds and multiplied x1000
  def getDuration(durationSec: Long): Duration = {
    val durationMs = durationSec * 1000
    val datatypeFactory = DatatypeFactory.newInstance()
    datatypeFactory.newDuration(durationMs)
  }
}