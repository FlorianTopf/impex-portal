// Generated by <a href="http://scalaxb.org/">scalaxb</a>.
package models.binding

import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.net.URI


case class GetObsDataTreeResponseAMDA(success: Option[Boolean] = None,
  WorkSpace: models.binding.WorkSpace)

  
// shared with CLWEB
case class GetTimeTablesList(userID: Option[String] = None,
  password: Option[String] = None)

  
// shared with CLWEB
case class GetTimeTablesListResponse(success: Option[Boolean] = None,
  TimeTablesList: Option[java.net.URI] = None)
  
// json formatter
object GetTimeTablesListResponse {
  implicit val urlFormat: Format[URI] = new Format[URI] {
    def writes(u: URI): JsValue = JsString(u.toString)
	def reads(j: JsValue): JsResult[URI] = JsSuccess(new URI(j.as[String]))
  }  
  
  implicit val getTTListResponseFormat: Format[GetTimeTablesListResponse] = Json.format[GetTimeTablesListResponse]
}


case class GetParameterList(userID: String,
  password: Option[String] = None)


case class GetParameterListResponse(success: Option[Boolean] = None,
  ParameterList: models.binding.ParameterList)
  
// json formatter
object GetParameterListResponse {
  implicit val urlFormat: Format[URI] = new Format[URI] {
    def writes(u: URI): JsValue = JsString(u.toString)
	def reads(j: JsValue): JsResult[URI] = JsSuccess(new URI(j.as[String]))
  }
  
  implicit val getPLResponseFormat: Format[GetParameterListResponse] = Json.format[GetParameterListResponse]
  
  implicit val parameterListFormat: Format[ParameterList] = Json.format[ParameterList]
}


// @TODO can in fact be shared among all
trait EnumSpacecraft

object EnumSpacecraft {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): EnumSpacecraft = value match {
    case "CASSINI" => Cassini_PublicType
    case "GALILEO" => GalileoType
    case "Voyager_1" => Voyager_1Type
    case "Voyager_2" => Voyager_2Type
    case "Pioneer_10" => Pioneer_10Type
    case "Pioneer_11" => Pioneer_11Type
    case "PVO" => PVOType
    case "ACE" => ACEType
    case "VEX" => VEXType
    case "MEX" => MEXType
    case "MGS" => MGSType
    case "MAVEN" => MAVENType
    case "MESSENGER" => MESSENGERType
    case "ULYSSES" => ULYSSESType
    case "STEREO-A" => Stereou45AType
    case "STEREO-B" => Stereou45BType
    case "WIND" => WINDType
    case "THEMIS-A" => THEMISu45AType
    case "THEMIS-B" => THEMISu45BType
    case "THEMIS-C" => THEMISu45CType
    case "THEMIS-D" => THEMISu45DType
    case "THEMIS-E" => THEMISu45EType
    case "CLUSTER1" => CLUSTER1Type
    case "CLUSTER2" => CLUSTER2Type
    case "CLUSTER3" => CLUSTER3Type
    case "CLUSTER4" => CLUSTER4Type
    case "DOUBLESTAR1" => DoubleStar1Type
    case "IMP-8" => IMPu458Type
    case "GEOTAIL" => GEOTAILType
    case "POLAR" => POLARType
    case "INTERBALL-TAIL" => INTERBALLu45TailType
    case "ISEE-1" => ISEEu451Type
    case "ISEE-2" => ISEEu452Type

  }
}


case object Cassini_PublicType extends EnumSpacecraft { override def toString = "CASSINI" }
case object GalileoType extends EnumSpacecraft { override def toString = "GALILEO" }
case object Voyager_1Type extends EnumSpacecraft { override def toString = "Voyager_1" }
case object Voyager_2Type extends EnumSpacecraft { override def toString = "Voyager_2" }
case object Pioneer_10Type extends EnumSpacecraft { override def toString = "Pioneer_10" }
case object Pioneer_11Type extends EnumSpacecraft { override def toString = "Pioneer_11" }
case object PVOType extends EnumSpacecraft { override def toString = "PVO" }
case object ACEType extends EnumSpacecraft { override def toString = "ACE" }
case object VEXType extends EnumSpacecraft { override def toString = "VEX" }
case object MEXType extends EnumSpacecraft { override def toString = "MEX" }
case object MGSType extends EnumSpacecraft { override def toString = "MGS" }
case object MAVENType extends EnumSpacecraft { override def toString = "MAVEN" }
case object MESSENGERType extends EnumSpacecraft { override def toString = "MESSENGER" }
case object ULYSSESType extends EnumSpacecraft { override def toString = "ULYSSES" }
case object Stereou45AType extends EnumSpacecraft { override def toString = "STEREO-A" }
case object Stereou45BType extends EnumSpacecraft { override def toString = "STEREO-B" }
case object WINDType extends EnumSpacecraft { override def toString = "WIND" }
case object THEMISu45AType extends EnumSpacecraft { override def toString = "THEMIS-A" }
case object THEMISu45BType extends EnumSpacecraft { override def toString = "THEMIS-B" }
case object THEMISu45CType extends EnumSpacecraft { override def toString = "THEMIS-C" }
case object THEMISu45DType extends EnumSpacecraft { override def toString = "THEMIS-D" }
case object THEMISu45EType extends EnumSpacecraft { override def toString = "THEMIS-E" }
case object CLUSTER1Type extends EnumSpacecraft { override def toString = "CLUSTER1" }
case object CLUSTER2Type extends EnumSpacecraft { override def toString = "CLUSTER2" }
case object CLUSTER3Type extends EnumSpacecraft { override def toString = "CLUSTER3" }
case object CLUSTER4Type extends EnumSpacecraft { override def toString = "CLUSTER4" }
case object DoubleStar1Type extends EnumSpacecraft { override def toString = "DOUBLESTAR1" }
case object IMPu458Type extends EnumSpacecraft { override def toString = "IMP-8" }
case object GEOTAILType extends EnumSpacecraft { override def toString = "GEOTAIL" }
case object POLARType extends EnumSpacecraft { override def toString = "POLAR" }
case object INTERBALLu45TailType extends EnumSpacecraft { override def toString = "INTERBALL-TAIL" }
case object ISEEu451Type extends EnumSpacecraft { override def toString = "ISEE-1" }
case object ISEEu452Type extends EnumSpacecraft { override def toString = "ISEE-2" }

trait WorkSpaceValue

object WorkSpaceValue {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): WorkSpaceValue = value match {
    case "TimeTables" => TimeTables
    case "UserDefinedParameters" => UserDefinedParameters
    case "LocalDataBaseParameters" => LocalDataBaseParameters
    case "RemoteDataBaseParameters" => RemoteDataBaseParameters

  }
}

case object TimeTables extends WorkSpaceValue { override def toString = "TimeTables" }
case object UserDefinedParameters extends WorkSpaceValue { override def toString = "UserDefinedParameters" }
case object LocalDataBaseParameters extends WorkSpaceValue { override def toString = "LocalDataBaseParameters" }
case object RemoteDataBaseParameters extends WorkSpaceValue { override def toString = "RemoteDataBaseParameters" }

// is a little bit diffferent than in SPASE DM
trait EnumCoordinateSystemType

object EnumCoordinateSystemType {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): EnumCoordinateSystemType = value match {
    case "Carrington" => CarringtonType
    case "CGM" => CGMType
  	case "CPHIO" => CPHIOType
    case "CSE" => CSEType
    case "CSEQ" => CSEQType
    case "DM" => DMType
    case "EPHIO" => EPHIOType
    case "Equatorial" => EquatorialType
    case "GEI" => GEIType
    case "GEO" => GEOType
    case "GPHIO" => GPHIOType
    case "GSE" => GSEType
    case "GSEQ" => GSEQType
    case "GSM" => GSMType
    case "HAE" => HAEType
    case "HCC" => HCCType
    case "HCI" => HCIType
    case "HCR" => HCRType
    case "HEE" => HEEType
    case "HEEQ" => HEEQType
    case "HG" => HGType
    case "HGI" => HGIType
    case "HPC" => HPCType
    case "HPR" => HPRType
    case "HSM" => HSMType
    case "IPHIO" => IPHIOType
    case "IRC" => IRCType
    case "J2000" => J2000Type
    case "JSE" => JSEType
    case "JSM" => JSMType
    case "KRTP" => KRTPType
    case "KSM" => KSMType
    case "KSO" => KSOType
    case "LGM" => LGMType
    case "MAG" => MAGType
    case "MBF" => MBFType
    case "MFA" => MFAType
    case "MSM" => MSMType
    case "MSO" => MSOType
    case "RTN" => RTNType
    case "SC" => SCType
    case "SE" => SEType
    case "SM" => SMType
    case "SpacecraftOrbitPlane" => SpacecraftOrbitPlaneType
    case "SR" => SRType
    case "SR2" => SR2Type
    case "SSE" => SSEType
    case "SSE_L" => SSE_LType
    case "SYS3" => SYS3Type
    case "VSO" => VSOType
    case "WGS84" => WGS84Type
  }
}

case object CarringtonType extends EnumCoordinateSystemType { override def toString = "Carrington" }
case object CGMType extends EnumCoordinateSystemType { override def toString = "CGM" }
case object CPHIOType extends EnumCoordinateSystemType { override def toString = "CPHIO" }
case object CSEType extends EnumCoordinateSystemType { override def toString = "CSE" }
case object CSEQType extends EnumCoordinateSystemType { override def toString = "CSEQ" }
case object DMType extends EnumCoordinateSystemType { override def toString = "DM" }
case object EPHIOType extends EnumCoordinateSystemType { override def toString = "EPHIO" }
case object EquatorialType extends EnumCoordinateSystemType { override def toString = "Equatorial" }
case object GEIType extends EnumCoordinateSystemType { override def toString = "GEI" }
case object GEOType extends EnumCoordinateSystemType { override def toString = "GEO" }
case object GPHIOType extends EnumCoordinateSystemType { override def toString = "GPHIO" }
case object GSEQType extends EnumCoordinateSystemType { override def toString = "GSEQ" }
case object GSEType extends EnumCoordinateSystemType { override def toString = "GSE" }
case object GSMType extends EnumCoordinateSystemType { override def toString = "GSM" }
case object HAEType extends EnumCoordinateSystemType { override def toString = "HAE" }
case object HCCType extends EnumCoordinateSystemType { override def toString = "HCC" }
case object HCIType extends EnumCoordinateSystemType { override def toString = "HCI" }
case object HCRType extends EnumCoordinateSystemType { override def toString = "HCR" }
case object HEEQType extends EnumCoordinateSystemType { override def toString = "HEEQ" }
case object HEEType extends EnumCoordinateSystemType { override def toString = "HEE" }
case object HGIType extends EnumCoordinateSystemType { override def toString = "HGI" }
case object HGType extends EnumCoordinateSystemType { override def toString = "HG" }
case object HPCType extends EnumCoordinateSystemType { override def toString = "HPC" }
case object HPRType extends EnumCoordinateSystemType { override def toString = "HPR" }
case object HSMType extends EnumCoordinateSystemType { override def toString = "HSM" }
case object IPHIOType extends EnumCoordinateSystemType { override def toString = "IPHIO" }
case object IRCType extends EnumCoordinateSystemType { override def toString = "IRC" }
case object J2000Type extends EnumCoordinateSystemType { override def toString = "J2000" }
case object JSEType extends EnumCoordinateSystemType { override def toString = "JSE" }
case object JSMType extends EnumCoordinateSystemType { override def toString = "JSM" }
case object KRTPType extends EnumCoordinateSystemType { override def toString = "KRTPType" }
case object KSMType extends EnumCoordinateSystemType { override def toString = "KSM" }
case object KSOType extends EnumCoordinateSystemType { override def toString = "KSO" }
case object LGMType extends EnumCoordinateSystemType { override def toString = "LGM" }
case object MAGType extends EnumCoordinateSystemType { override def toString = "MAG" }
case object MBFType extends EnumCoordinateSystemType { override def toString = "MBF" }
case object MFAType extends EnumCoordinateSystemType { override def toString = "MFA" }
case object MSMType extends EnumCoordinateSystemType { override def toString = "MSM" }
case object MSOType extends EnumCoordinateSystemType { override def toString = "MSO" }
case object RTNType extends EnumCoordinateSystemType { override def toString = "RTN" }
case object SCType extends EnumCoordinateSystemType { override def toString = "SC" }
case object SEType extends EnumCoordinateSystemType { override def toString = "SE" }
case object SMType extends EnumCoordinateSystemType { override def toString = "SM" }
case object SpacecraftOrbitPlaneType extends EnumCoordinateSystemType { override def toString = "SpacecraftOrbitPlane" }
case object SR2Type extends EnumCoordinateSystemType { override def toString = "SR2" }
case object SRType extends EnumCoordinateSystemType { override def toString = "SR" }
case object SSEType extends EnumCoordinateSystemType { override def toString = "SSE" }
case object SSE_LType extends EnumCoordinateSystemType { override def toString = "SSE_L" }
case object SYS3Type extends EnumCoordinateSystemType { override def toString = "SYS3" }
case object VSOType extends EnumCoordinateSystemType { override def toString = "VSO" }
case object WGS84Type extends EnumCoordinateSystemType { override def toString = "WGS84" }


case class ParameterList(UserDefinedParameters: Option[java.net.URI] = None,
  LocalDataBaseParameters: Option[java.net.URI] = None,
  RemoteDataBaseParameters: Option[java.net.URI] = None)


case class WorkSpace(LocalDataBaseParameters: Option[java.net.URI] = None,
  RemoteDataBaseParameters: Option[java.net.URI] = None)

  
// is shared with CLWEB
trait OutputFormat

object OutputFormat {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): OutputFormat = value match {
    case "netCDF" => NetCDFFormat
    case "VOTable" => VOTableFormat
    case "ASCII" => ASCIIFormat

  }
}

case object NetCDFFormat extends OutputFormat { override def toString = "netCDF" }
case object VOTableFormat extends OutputFormat { override def toString = "VOTable" }
case object ASCIIFormat extends OutputFormat { override def toString = "ASCII" }


// is shared with CLWEB
trait TimeFormat

object TimeFormat {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): TimeFormat = value match {
    case "ISO8601" => ISO8601
    case "unixtime" => Unixtime

  }
}

case object ISO8601 extends TimeFormat { override def toString = "ISO8601" }
case object Unixtime extends TimeFormat { override def toString = "unixtime" }


// is shared with CLWEB
case class GetParameter(startTime: String,
  stopTime: String,
  parameterID: String,
  sampling: Option[Float] = None,
  userID: Option[String] = None,
  password: Option[String] = None,
  outputFormat: Option[models.binding.OutputFormat] = None,
  timeFormat: Option[models.binding.TimeFormat] = None,
  gzip: Option[BigInt] = None)


case class GetParameterResponse(success: Option[Boolean] = None,
  dataFileURLs: Seq[java.net.URI] = Nil)
  
// json formatter
object GetParameterResponse {
  implicit val urlFormat: Format[URI] = new Format[URI] {
    def writes(u: URI): JsValue = JsString(u.toString)
	def reads(j: JsValue): JsResult[URI] = JsSuccess(new URI(j.as[String]))
  }
  
  implicit val getParamResponseformat: Format[GetParameterResponse] = Json.format[GetParameterResponse]
}


case class GetDataset(startTime: String,
  stopTime: String,
  datasetID: String,
  sampling: Option[Float] = None,
  userID: Option[String] = None,
  password: Option[String] = None,
  outputFormat: Option[models.binding.OutputFormat] = None,
  timeFormat: Option[models.binding.TimeFormat] = None,
  gzip: Option[BigInt] = None)


case class GetDatasetResponse(success: Option[Boolean] = None,
  dataFileURLs: Seq[java.net.URI] = Nil)

  
trait Units

object Units {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): Units = value match {
    case "km" => Km
    case "Rs" => Rs
    case "Rj" => Rj
    case "Rca" => Rca
    case "Rga" => Rga
    case "Rio" => Rio
    case "Reu" => Reu
    case "Rv" => Rv
    case "Rm" => Rm
    case "Re" => Re
    case "AU" => AU

  }
}

case object Km extends Units { override def toString = "km" }
case object Rs extends Units { override def toString = "Rs" }
case object Rj extends Units { override def toString = "Rj" }
case object Rca extends Units { override def toString = "Rca" }
case object Rga extends Units { override def toString = "Rga" }
case object Rio extends Units { override def toString = "Rio" }
case object Reu extends Units { override def toString = "Reu" }
case object Rv extends Units { override def toString = "Rv" }
case object Rm extends Units { override def toString = "Rm" }
case object Re extends Units { override def toString = "Re" }
case object AU extends Units { override def toString = "AU" }


case class GetOrbites(startTime: String,
  stopTime: String,
  spacecraft: models.binding.EnumSpacecraft,
  coordinateSystem: models.binding.EnumCoordinateSystemType,
  units: Option[models.binding.Units] = None,
  sampling: Option[Float] = None,
  userID: Option[String] = None,
  password: Option[String] = None,
  outputFormat: Option[models.binding.OutputFormat] = None,
  timeFormat: Option[models.binding.TimeFormat] = None,
  gzip: Option[BigInt] = None)


case class GetOrbitesResponse(success: Option[Boolean] = None,
  dataFileURLs: Seq[java.net.URI] = Nil)
  
// json formatter  
object GetOrbitesResponse {
    implicit val urlFormat: Format[URI] = new Format[URI] {
    def writes(u: URI): JsValue = JsString(u.toString)
	def reads(j: JsValue): JsResult[URI] = JsSuccess(new URI(j.as[String]))
  }
  
  implicit val getOrbitsResponseFormat: Format[GetOrbitesResponse] = Json.format[GetOrbitesResponse]
}


// is shared with CLWEB
case class GetTimeTable(userID: Option[String] = None,
  password: Option[String] = None,
  ttID: String)


case class GetTimeTableResponse(success: Option[Boolean] = None,
  ttFileURL: java.net.URI)
  
// json formatter
object GetTimeTableResponse {
  implicit val urlFormat: Format[URI] = new Format[URI] {
    def writes(u: URI): JsValue = JsString(u.toString)
	def reads(j: JsValue): JsResult[URI] = JsSuccess(new URI(j.as[String]))
  }
  
  implicit val getTTResponseFormat: Format[GetTimeTableResponse] = Json.format[GetTimeTableResponse]
}

