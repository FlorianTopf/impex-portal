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
    case "ASCII" => ASCIIType

  }
}

case object NetCDFType extends OutputFormatType { override def toString = "netCDF" }
case object VOTableType extends OutputFormatType { override def toString = "VOTable" }
case object ASCIIType extends OutputFormatType { override def toString = "ASCII" }


trait EnumInterpolation

object EnumInterpolation {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): EnumInterpolation = value match {
    case "NearestGridPoint" => NearestGridPointValue
    case "Linear" => LinearValue

  }
}

case object NearestGridPointValue extends EnumInterpolation { override def toString = "NearestGridPoint" }
case object LinearValue extends EnumInterpolation { override def toString = "Linear" }

// the spacecraft values were changed because of LATMOS (they use abbreviations)
trait SpacecraftType

object SpacecraftType {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): SpacecraftType = value match {
    case "MEX" => MarsExpressValue
    case "MGS" => MarsGlobalSurveyorValue
    case "VEX" => VenusExpressValue
    // @FIXME this is a hack because FMI doesn't work according to WSDL rules
    case "c3_xyz" => Cluster3Value
  }
}

case object MarsExpressValue extends SpacecraftType { override def toString = "MEX" }
case object MarsGlobalSurveyorValue extends SpacecraftType { override def toString = "MGS" }
case object VenusExpressValue extends SpacecraftType { override def toString = "VEX" }
case object Cluster3Value extends SpacecraftType { override def toString = "c3_xyz" }