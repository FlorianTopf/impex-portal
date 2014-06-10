// Generated by <a href="http://scalaxb.org/">scalaxb</a>.
package models.binding

trait SpacecraftTypeSINP

object SpacecraftTypeSINP {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): SpacecraftTypeSINP = value match {
    case "Cassini_Public" => Cassini_Public
    case "Galileo" => Galileo
    case "Voyager_1" => Voyager_1
    case "Voyager_2" => Voyager_2
    case "Pioneer_10" => Pioneer_10
    case "Pioneer_11" => Pioneer_11
    case "PVO" => PVO
    case "ACE" => ACE
    case "VEX" => VEX
    case "MEX" => MEX
    case "MGS" => MGS
    case "MAVEN" => MAVEN
    case "MESSENGER" => MESSENGER
    case "ULYSSES" => ULYSSES
    case "Stereo-A" => Stereou45A
    case "Stereo-B" => Stereou45B
    case "WIND" => WIND
    case "THEMIS-A" => THEMISu45A
    case "THEMIS-B" => THEMISu45B
    case "THEMIS-C" => THEMISu45C
    case "THEMIS-D" => THEMISu45D
    case "THEMIS-E" => THEMISu45E
    case "CLUSTER1" => CLUSTER1
    case "CLUSTER2" => CLUSTER2
    case "CLUSTER3" => CLUSTER3
    case "CLUSTER4" => CLUSTER4
    case "DoubleStar1" => DoubleStar1
    case "IMP-8" => IMPu458
    case "GEOTAIL" => GEOTAIL
    case "POLAR" => POLARValue
    case "INTERBALL-Tail" => INTERBALLu45Tail
    case "ISEE-1" => ISEEu451
    case "ISEE-2" => ISEEu452

  }
}

case object Cassini_Public extends SpacecraftTypeSINP { override def toString = "Cassini_Public" }
case object Galileo extends SpacecraftTypeSINP { override def toString = "Galileo" }
case object Voyager_1 extends SpacecraftTypeSINP { override def toString = "Voyager_1" }
case object Voyager_2 extends SpacecraftTypeSINP { override def toString = "Voyager_2" }
case object Pioneer_10 extends SpacecraftTypeSINP { override def toString = "Pioneer_10" }
case object Pioneer_11 extends SpacecraftTypeSINP { override def toString = "Pioneer_11" }
case object PVO extends SpacecraftTypeSINP { override def toString = "PVO" }
case object ACE extends SpacecraftTypeSINP { override def toString = "ACE" }
case object VEX extends SpacecraftTypeSINP { override def toString = "VEX" }
case object MEX extends SpacecraftTypeSINP { override def toString = "MEX" }
case object MGS extends SpacecraftTypeSINP { override def toString = "MGS" }
case object MAVEN extends SpacecraftTypeSINP { override def toString = "MAVEN" }
case object MESSENGER extends SpacecraftTypeSINP { override def toString = "MESSENGER" }
case object ULYSSES extends SpacecraftTypeSINP { override def toString = "ULYSSES" }
case object Stereou45A extends SpacecraftTypeSINP { override def toString = "Stereo-A" }
case object Stereou45B extends SpacecraftTypeSINP { override def toString = "Stereo-B" }
case object WIND extends SpacecraftTypeSINP { override def toString = "WIND" }
case object THEMISu45A extends SpacecraftTypeSINP { override def toString = "THEMIS-A" }
case object THEMISu45B extends SpacecraftTypeSINP { override def toString = "THEMIS-B" }
case object THEMISu45C extends SpacecraftTypeSINP { override def toString = "THEMIS-C" }
case object THEMISu45D extends SpacecraftTypeSINP { override def toString = "THEMIS-D" }
case object THEMISu45E extends SpacecraftTypeSINP { override def toString = "THEMIS-E" }
case object CLUSTER1 extends SpacecraftTypeSINP { override def toString = "CLUSTER1" }
case object CLUSTER2 extends SpacecraftTypeSINP { override def toString = "CLUSTER2" }
case object CLUSTER3 extends SpacecraftTypeSINP { override def toString = "CLUSTER3" }
case object CLUSTER4 extends SpacecraftTypeSINP { override def toString = "CLUSTER4" }
case object DoubleStar1 extends SpacecraftTypeSINP { override def toString = "DoubleStar1" }
case object IMPu458 extends SpacecraftTypeSINP { override def toString = "IMP-8" }
case object GEOTAIL extends SpacecraftTypeSINP { override def toString = "GEOTAIL" }
case object POLARValue extends SpacecraftTypeSINP { override def toString = "POLAR" }
case object INTERBALLu45Tail extends SpacecraftTypeSINP { override def toString = "INTERBALL-Tail" }
case object ISEEu451 extends SpacecraftTypeSINP { override def toString = "ISEE-1" }
case object ISEEu452 extends SpacecraftTypeSINP { override def toString = "ISEE-2" }


/** x,y,z
*/
case class ListOfDouble(x: Option[Double] = None,
  y: Option[Double] = None,
  z: Option[Double] = None)


/** List with sizes of a cube
*/
case class Cube_size_array(x_low: Option[BigInt] = None,
  x_high: Option[BigInt] = None,
  y_low: Option[BigInt] = None,
  y_high: Option[BigInt] = None,
  z_low: Option[BigInt] = None,
  z_high: Option[BigInt] = None)

  
case class DataPointValueSINP(ResourceID: String,
  Variable: Option[Seq[String]] = None,
  url_XYZ: Option[java.net.URI] = None,
  extraParams: Option[models.binding.ExtraParams_getDataPointValueSINP] = None)
  
case class ExtraParams_getDataPointValueSINP(OutputFileType: Option[models.binding.OutputFormatType] = None,
  InterpolationMethod: Option[models.binding.EnumInterpolation] = None)


/** List of individual parameters: SW Density, SW Velocity, IMF_B (Bx,By,Bz), Dst, AL, BD, BT, RD2, RD1, R2, R1.
*/
case class ExtraParams_calculateDataPointValueFixedTime(SWDensity: Option[Double] = None,
  SWVelocity: Option[Double] = None,
  IMF_B: Option[models.binding.ListOfDouble] = None,
  Dst: Option[Double] = None,
  AL: Option[Double] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


/** Only OutputFileType for now.
*/
case class ExtraParams_calculateDataPointValue(OutputFileType: Option[models.binding.OutputFormatType] = None)


/**  By default:'BD'=-196.0,'Flux'=131.0,'Rss'=1.35,'R2'=1.32,'DZ'=0.0,'IMF_B'=(0.0,0.0,0.0)
*/
case class ExtraParams_calculateDataPointValueMercury(OutputFileType: Option[models.binding.OutputFormatType] = None,
  BD: Option[Double] = None,
  Flux: Option[Double] = None,
  Rss: Option[Double] = None,
  R2: Option[Double] = None,
  DZ: Option[Double] = None,
  IMF_B: Option[models.binding.ListOfDouble] = None)


/** By default:'BDÐ¡'=3.0,'BT'=7.0,'RD2'=15.0,'RD1'=6.5,'Rss'=22.0,'R2'=18.0,'IMF_B'=(0.0,0.0,0.0)
*/
case class ExtraParams_calculateDataPointValueSaturn(OutputFileType: Option[models.binding.OutputFormatType] = None,
  BDC: Option[Double] = None,
  BT: Option[Double] = None,
  RD2: Option[Double] = None,
  RD1: Option[Double] = None,
  R2: Option[Double] = None,
  Rss: Option[Double] = None,
  IMF_B: Option[models.binding.ListOfDouble] = None)


/** 
*/
case class ExtraParams_calculateDataPointValueJupiter(OutputFileType: Option[models.binding.OutputFormatType] = None,
  BD: Option[Double] = None,
  BDC: Option[Double] = None,
  BT: Option[Double] = None,
  RD2: Option[Double] = None,
  RD1: Option[Double] = None,
  R2: Option[Double] = None,
  Rss: Option[Double] = None)


/** Only OutputFileType for now.
*/
case class ExtraParams_calculateDataPointValueSpacecraft(OutputFileType: Option[models.binding.OutputFormatType] = None)


/** Only OutputFileType for now.
*/
case class ExtraParams_calculateFieldLine(LineLength: Option[Double] = None,
  StepSize: Option[Double] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


/** List of individual parameters:SW Density,SW Velocity,IMF_B(Bx,By,Bz),Dst,AL.
*/
case class ExtraParams_calculateCube(SWDensity: Option[Double] = None,
  SWVelocity: Option[Double] = None,
  IMF_B: Option[models.binding.ListOfDouble] = None,
  Dst: Option[Double] = None,
  AL: Option[Double] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


/** List of parameters: BD,Flux,Rss,R2,DZ,IMF_B. By default:'BD'=-196.0,'Flux'=131.0,'Rss'=1.35,'R2'=1.32,'DZ'=0.0,'IMF_B'=(0.0,0.0,0.0)
*/
case class ExtraParams_calculateCubeMercury(BD: Option[Double] = None,
  Flux: Option[Double] = None,
  Rss: Option[Double] = None,
  R2: Option[Double] = None,
  DZ: Option[Double] = None,
  IMF_B: Option[models.binding.ListOfDouble] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


/** List of parameters: BDC, BT, RD2, RD1, R2, Rss, IMF_B. By default:'BDÐ¡'=3.0,'BT'=7.0,'RD2'=15.0,'RD1'=6.5,'Rss'=22.0,'R2'=18.0,'IMF_B'=(0.0,0.0,0.0)
*/
case class ExtraParams_calculateCubeSaturn(BDC: Option[Double] = None,
  BT: Option[Double] = None,
  RD2: Option[Double] = None,
  RD1: Option[Double] = None,
  R2: Option[Double] = None,
  Rss: Option[Double] = None,
  IMF_B: Option[models.binding.ListOfDouble] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


/** List of parameters: BDC, BT, RD2, RD1, R2, Rss, IMF_B.
*/
case class ExtraParams_calculateCubeJupiter(BDC: Option[Double] = None,
  BT: Option[Double] = None,
  RD2: Option[Double] = None,
  RD1: Option[Double] = None,
  R2: Option[Double] = None,
  Rss: Option[Double] = None,
  IMF_B: Option[models.binding.ListOfDouble] = None,
  OutputFileType: Option[models.binding.OutputFormatType] = None)


case class CalculateDataPointValueFixedTime(ResourceID: String,
  StartTime: javax.xml.datatype.XMLGregorianCalendar,
  extraParams: Option[models.binding.ExtraParams_calculateDataPointValueFixedTime] = None,
  url_XYZ: java.net.URI)


case class CalculateDataPointValue(ResourceID: String,
  extraParams: Option[models.binding.ExtraParams_calculateDataPointValue] = None,
  url_XYZ: java.net.URI)


case class CalculateDataPointValueMercury(ResourceID: String,
  extraParams: Option[models.binding.ExtraParams_calculateDataPointValueMercury] = None,
  url_XYZ: java.net.URI)


case class CalculateDataPointValueSaturn(ResourceID: String,
  extraParams: Option[models.binding.ExtraParams_calculateDataPointValueSaturn] = None,
  url_XYZ: java.net.URI)


case class CalculateDataPointValueJupiter(ResourceID: String,
  extraParams: Option[models.binding.ExtraParams_calculateDataPointValueJupiter] = None,
  url_XYZ: java.net.URI)


case class CalculateDataPointValueSpacecraft(ResourceID: String,
  Spacecraft_name: models.binding.SpacecraftTypeSINP,
  StartTime: javax.xml.datatype.XMLGregorianCalendar,
  StopTime: javax.xml.datatype.XMLGregorianCalendar,
  Sampling: javax.xml.datatype.Duration,
  extraParams: Option[models.binding.ExtraParams_calculateDataPointValueSpacecraft] = None)


case class CalculateFieldLine(ResourceID: String,
  StartTime: javax.xml.datatype.XMLGregorianCalendar,
  extraParams: Option[models.binding.ExtraParams_calculateFieldLine] = None,
  url_XYZ: java.net.URI)


case class CalculateCube(ResourceID: String,
  StartTime: javax.xml.datatype.XMLGregorianCalendar,
  extraParams: Option[models.binding.ExtraParams_calculateCube] = None,
  Sampling: Option[Double] = None,
  cube_size_array: Option[models.binding.Cube_size_array] = None)


case class CalculateCubeMercury(ResourceID: String,
  extraParams: Option[models.binding.ExtraParams_calculateCubeMercury] = None)


case class CalculateCubeSaturn(ResourceID: String,
  StartTime: javax.xml.datatype.XMLGregorianCalendar,
  extraParams: Option[models.binding.ExtraParams_calculateCubeSaturn] = None, 
  Sampling: Option[Double] = None,
  cube_size_array: Option[Cube_size_array] = None)


case class CalculateCubeJupiter(ResourceID: String,
  extraParams: Option[models.binding.ExtraParams_calculateCubeJupiter] = None)

