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
    // maybe we only need the minutes!
    val dateFormat = new SimpleDateFormat("yy-mm-dd H:m:s")
    val isoDate: Date = dateFormat.parse(date)
    val gregorianCalendar = new GregorianCalendar()
    gregorianCalendar.setTime(isoDate)
    val datatypeFactory = DatatypeFactory.newInstance()
    datatypeFactory.newXMLGregorianCalendar(gregorianCalendar)
  }
  
  def getDuration(duration: Long): Duration = {
    val datatypeFactory = DatatypeFactory.newInstance()
    datatypeFactory.newDuration(duration)
  }
}