package models.binding


case class DataPointValueLATMOS(ResourceID: String,
  Variable: Option[Seq[String]] = None,
  url_XYZ: java.net.URI,
  extraParams: Option[models.binding.ExtraParams_getDataPointValueLATMOS] = None)


case class ExtraParams_getDataPointValueLATMOS(IMFClockAngle: Option[Double] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


case class DataPointSpectraLATMOS(ResourceID: String,
  Population: Option[Seq[String]] = None,
  url_XYZ: java.net.URI,
  extraParams: Option[models.binding.ExtraParams_getDataPointSpectraLATMOS] = None)


case class ExtraParams_getDataPointSpectraLATMOS(IMFClockAngle: Option[Double] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None,
  EnergyChannel: Option[Seq[String]] = None)


case class SurfaceLATMOS(ResourceID: String,
  Variable: Option[Seq[String]] = None,
  PlanePoint: Seq[Float],
  PlaneNormalVector: Seq[Float],
  extraParams: Option[models.binding.ExtraParams_getSurfaceLATMOS] = None)


case class ExtraParams_getSurfaceLATMOS(Resolution: Option[Double] = None,
  IMFClockAngle: Option[Double] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


case class DataPointValue_SpacecraftLATMOS(ResourceID: String,
  Variable: Option[Seq[String]] = None,
  Spacecraft_name: models.binding.SpacecraftType,
  StartTime: javax.xml.datatype.XMLGregorianCalendar,
  StopTime: javax.xml.datatype.XMLGregorianCalendar,
  Sampling: javax.xml.datatype.Duration,
  extraParams: Option[models.binding.ExtraParams_getDataPointValueLATMOS] = None)


case class DataPointSpectra_SpacecraftLATMOS(ResourceID: String,
  Population: Option[Seq[String]] = None,
  Spacecraft_name: models.binding.SpacecraftType,
  StartTime: javax.xml.datatype.XMLGregorianCalendar,
  StopTime: javax.xml.datatype.XMLGregorianCalendar,
  Sampling: javax.xml.datatype.Duration,
  extraParams: Option[models.binding.ExtraParams_getDataPointSpectraLATMOS] = None)


case class FileURL(ResourceID: String,
  StartTime: javax.xml.datatype.XMLGregorianCalendar,
  StopTime: javax.xml.datatype.XMLGregorianCalendar)


case class FieldLineLATMOS(ResourceID: String,
  Variable: Option[Seq[String]] = None,
  url_XYZ: java.net.URI,
  extraParams: Option[models.binding.ExtraParams_getFieldLineLATMOS] = None)


case class ExtraParams_getFieldLineLATMOS(Direction: Option[models.binding.EnumDirection] = None,
  StepSize: Option[Double] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)

