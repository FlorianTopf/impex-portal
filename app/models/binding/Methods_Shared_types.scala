package models.binding

trait EnumDirection

object EnumDirection {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): EnumDirection = value match {
    case "Both" => Both
    case "Forward" => Forward
    case "Backward" => Backward

  }
}

case object Both extends EnumDirection { override def toString = "Both" }
case object Forward extends EnumDirection { override def toString = "Forward" }
case object Backward extends EnumDirection { override def toString = "Backward" }


trait OutputFormatType

object OutputFormatType {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): OutputFormatType = value match {
    case "netCDF" => NetCDFType
    case "VOTable" => VOTableType
    //case "ASCII" => ASCIIType

  }
}

case object NetCDFType extends OutputFormatType { override def toString = "netCDF" }
case object VOTableType extends OutputFormatType { override def toString = "VOTable" }
//case object ASCIIType extends OutputFormatType { override def toString = "ASCII" }


trait EnumInterpolation

object EnumInterpolation {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): EnumInterpolation = value match {
    case "NearestGridPoint" => NearestGridPointValue
    case "Linear" => LinearValue

  }
}

case object NearestGridPointValue extends EnumInterpolation { override def toString = "NearestGridPoint" }
case object LinearValue extends EnumInterpolation { override def toString = "Linear" }


trait SpacecraftType

// only MEX / MGS / VEX used at LATMOS
object SpacecraftType {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): SpacecraftType = value match {
    case "MEX" => MEXValue
    case "MGS" => MGSValue
    case "VEX" => VEXValue
    // only used at FMI
    case "MAVEN" => MAVENValue
    case "MESSENGER" => MESSENGERValue
    case "CLUSTER1" => CLUSTER1Value
    case "CLUSTER2" => CLUSTER2Value
    case "CLUSTER3" => CLUSTER3Value
    case "CLUSTER4" => CLUSTER4Value
    case "IMP-8" => IMPu458Value
    case "GEOTAIL" => GEOTAILValue
    case "POLAR" => POLARValue2
  }
}

case object MEXValue extends SpacecraftType { override def toString = "MEX" }
case object MGSValue extends SpacecraftType { override def toString = "MGS" }
case object VEXValue extends SpacecraftType { override def toString = "VEX" }
case object MAVENValue extends SpacecraftType { override def toString = "MAVEN" }
case object MESSENGERValue extends SpacecraftType { override def toString = "MESSENGER" }
case object CLUSTER1Value extends SpacecraftType { override def toString = "CLUSTER1" }
case object CLUSTER2Value extends SpacecraftType { override def toString = "CLUSTER2" }
case object CLUSTER3Value extends SpacecraftType { override def toString = "CLUSTER3" }
case object CLUSTER4Value extends SpacecraftType { override def toString = "CLUSTER4" }
case object IMPu458Value extends SpacecraftType { override def toString = "IMP-8" }
case object GEOTAILValue extends SpacecraftType { override def toString = "GEOTAIL" }
case object POLARValue2 extends SpacecraftType { override def toString = "POLAR" }

