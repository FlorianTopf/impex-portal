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
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val isoDate: Date = dateFormat.parse(date)
    val gregorianCalendar = new GregorianCalendar()
    gregorianCalendar.setTime(isoDate)
    val datatypeFactory = DatatypeFactory.newInstance()
    datatypeFactory.newXMLGregorianCalendar(gregorianCalendar)
  }
  
  def getDuration(duration: String): Duration = {
    val datatypeFactory = DatatypeFactory.newInstance()
    datatypeFactory.newDuration(duration)
  }
}