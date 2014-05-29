// Generated by <a href="http://scalaxb.org/">scalaxb</a>.
package models.binding


trait Methods_LATMOS {
  def getDataPointValue(resourceID: String, variable: Option[Seq[String]], url_XYZ: java.net.URI, extraParams: Option[models.binding.ExtraParams_getDataPointValueLATMOS]): Either[scalaxb.Soap11Fault[Any], java.net.URI]
  def getDataPointSpectra(resourceID: String, population: Option[Seq[String]], url_XYZ: java.net.URI, extraParams: Option[models.binding.ExtraParams_getDataPointSpectraLATMOS]): Either[scalaxb.Soap11Fault[Any], java.net.URI]
  def getSurface(resourceID: String, variable: Option[Seq[String]], planePoint: Seq[Float], planeNormalVector: Seq[Float], extraParams: Option[models.binding.ExtraParams_getSurfaceLATMOS]): Either[scalaxb.Soap11Fault[Any], java.net.URI]
  def getDataPointValue_Spacecraft(resourceID: String, variable: Option[Seq[String]], spacecraft_name: models.binding.SpacecraftType, startTime: javax.xml.datatype.XMLGregorianCalendar, stopTime: javax.xml.datatype.XMLGregorianCalendar, sampling: javax.xml.datatype.Duration, extraParams: Option[models.binding.ExtraParams_getDataPointValueLATMOS]): Either[scalaxb.Soap11Fault[Any], java.net.URI]
  def getDataPointSpectra_Spacecraft(resourceID: String, population: Option[Seq[String]], spacecraft_name: models.binding.SpacecraftType, startTime: javax.xml.datatype.XMLGregorianCalendar, stopTime: javax.xml.datatype.XMLGregorianCalendar, sampling: javax.xml.datatype.Duration, extraParams: Option[models.binding.ExtraParams_getDataPointSpectraLATMOS]): Either[scalaxb.Soap11Fault[Any], java.net.URI]
  def getFileURL(resourceID: String, startTime: javax.xml.datatype.XMLGregorianCalendar, stopTime: javax.xml.datatype.XMLGregorianCalendar): Either[scalaxb.Soap11Fault[Any], models.binding.VOTABLE]
  def getFieldLine(resourceID: String, variable: Option[Seq[String]], url_XYZ: java.net.URI, extraParams: Option[models.binding.ExtraParams_getFieldLineLATMOS]): Either[scalaxb.Soap11Fault[Any], java.net.URI]
}



