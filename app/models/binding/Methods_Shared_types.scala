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

trait SpacecraftType

object SpacecraftType {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): SpacecraftType = value match {
    case "MarsExpress" => MarsExpressValue
    case "MarsGlobalSurveyor" => MarsGlobalSurveyorValue
    case "VenusExpress" => VenusExpressValue

  }
}

case object MarsExpressValue extends SpacecraftType { override def toString = "MarsExpress" }
case object MarsGlobalSurveyorValue extends SpacecraftType { override def toString = "MarsGlobalSurveyor" }
case object VenusExpressValue extends SpacecraftType { override def toString = "VenusExpress" }