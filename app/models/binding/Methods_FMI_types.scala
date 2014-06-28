package models.binding

import play.api.libs.json._


case class DataPointValueFMI(ResourceID: String,
  Variable: Option[Seq[String]] = None,
  url_XYZ: java.net.URI,
  extraParams: Option[models.binding.ExtraParams_getDataPointValueFMI] = None)


case class ExtraParams_getDataPointValueFMI(InterpolationMethod: Option[models.binding.EnumInterpolation] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


case class DataPointSpectraFMI(ResourceID: String,
  url_XYZ: java.net.URI,
  extraParams: Option[models.binding.ExtraParams_getDataPointSpectraFMI] = None)


case class ExtraParams_getDataPointSpectraFMI(InterpolationMethod: Option[models.binding.EnumInterpolation] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None,
  EnergyChannel: Option[Seq[String]] = None)


case class SurfaceFMI(ResourceID: String,
  Variable: Option[Seq[String]] = None,
  PlanePoint: Seq[Float],
  PlaneNormalVector: Seq[Float],
  extraParams: Option[models.binding.ExtraParams_getSurfaceFMI] = None)


case class ExtraParams_getSurfaceFMI(Resolution: Option[Double] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None,
  InterpolationMethod: Option[models.binding.EnumInterpolation] = None)


case class DataPointValueSpacecraftFMI(ResourceID: String,
  Variable: Option[Seq[String]] = None,
  Spacecraft_name: models.binding.SpacecraftType,
  StartTime: javax.xml.datatype.XMLGregorianCalendar,
  StopTime: javax.xml.datatype.XMLGregorianCalendar,
  Sampling: javax.xml.datatype.Duration,
  extraParams: Option[models.binding.ExtraParams_getDataPointValueFMI] = None)


case class DataPointSpectraSpacecraftFMI(ResourceID: String,
  Spacecraft_name: models.binding.SpacecraftType,
  StartTime: javax.xml.datatype.XMLGregorianCalendar,
  StopTime: javax.xml.datatype.XMLGregorianCalendar,
  Sampling: javax.xml.datatype.Duration,
  extraParams: Option[models.binding.ExtraParams_getDataPointSpectraFMI] = None)


case class FieldLineFMI(ResourceID: String,
  Variable: Option[Seq[String]] = None,
  url_XYZ: java.net.URI,
  extraParams: Option[models.binding.ExtraParams_getFieldLineFMI] = None)


case class ExtraParams_getFieldLineFMI(Direction: Option[models.binding.EnumDirection] = None,
  StepSize: Option[Double] = None,
  MaxSteps: Option[BigInt] = None,
  StopCondition_Radius: Option[Double] = None,
  StopCondition_Region: Option[Seq[Float]] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


case class ParticleTrajectory(ResourceID: String,
  url_XYZ: java.net.URI,
  extraParams: Option[models.binding.ExtraParams_getParticleTrajectory] = None)


case class ExtraParams_getParticleTrajectory(Direction: Option[models.binding.EnumDirection] = None,
  StepSize: Option[Double] = None,
  MaxSteps: Option[BigInt] = None,
  StopCondition_Radius: Option[Double] = None,
  StopCondition_Region: Option[Seq[Float]] = None,
  InterpolationMethod: Option[models.binding.EnumInterpolation] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


case class MostRelevantRun(Object: EnumRegion,
  RunCount: Option[BigInt] = None,
  SW_parameters: models.binding.SW_parameter_list)


case class SW_parameter_list(SW_Density: Option[models.binding.SW_parameter] = None,
  SW_Utot: Option[models.binding.SW_parameter] = None,
  SW_Temperature: Option[models.binding.SW_parameter] = None,
  SW_Btot: Option[models.binding.SW_parameter] = None,
  SW_Bx: Option[models.binding.SW_parameter] = None,
  SW_By: Option[models.binding.SW_parameter] = None,
  SW_Bz: Option[models.binding.SW_parameter] = None,
  Solar_F10u467: Option[models.binding.SW_parameter] = None,
  SW_Function: Option[models.binding.SW_parameter] = None)


case class SW_parameter(value: Double,
  weight: Option[Double] = None,
  scale: Option[Double] = None,
  function: Option[String] = None)


case class VOTableURL(Table_name: Option[String] = None,
  Description: Option[String] = None,
  Fields: Seq[models.binding.VOTable_field] = Nil)


case class VOTable_field(data: Seq[String],
  name: String,
  ID: Option[String] = None,
  unit: Option[String] = None,
  datatype: Option[models.binding.DataType] = None,
  xtype: Option[String] = None,
  ucd: Option[String] = None,
  utype: Option[String] = None,
  description: Option[String] = None,
  arraysize: Option[String] = None)
  
  
// json formatter for VOTableURL request
object VOTableURL {
  implicit val dataTypeFormat: Format[DataType] = new Format[DataType] {
    def writes(t: DataType): JsValue = JsString(t.toString)
	    
	def reads(json: JsValue): JsResult[DataType] = {
      json match {
        case json: JsString => {
          try {
            JsSuccess(DataType.fromString(json.as[String],
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")))
	        } catch {
	          case e: Exception => JsError("Invalid DataType")
	        }
	      }
	      case other => JsError("Malformed DataType")
	  }
	}
  }
  
  implicit val voTableFieldFormat: Format[VOTable_field] = Json.format[VOTable_field]
  
  implicit val voTableURLFormat: Format[VOTableURL] = Json.format[VOTableURL]
  
}

