package models.provider

import java.util.GregorianCalendar
import javax.xml.datatype._

object TimeProvider {
  def getISONow: XMLGregorianCalendar = {
    val gregorianCalendar = new GregorianCalendar()
    val datatypeFactory = DatatypeFactory.newInstance()
    datatypeFactory.newXMLGregorianCalendar(gregorianCalendar)
  }

}