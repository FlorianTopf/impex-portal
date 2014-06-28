package models.binding

import play.api.libs.json._
import models.binding._
import scala.xml._
import javax.xml.datatype._
import java.net.URI

/** 
               Space Physics Archive Search and Extract (SPASE).
               The outermost container or envelope for SPASE
               metadata. This indicates the start of the
               SPASE metadata.
            
*/
case class Spase(Version: models.binding.EnumVersion,
  ResourceEntity: Seq[scalaxb.DataRecord[Any]] = Nil,
  lang: String)


// @TODO we may need to reorganise optional attributes for client-side deserialisation (remove them if not available?)
// @TODO refactoring (according to new coding styles => Option can be null, use Format and ORIGINAL name)
object Spase {
  
 implicit val spaseWrites: Writes[Spase] = new Writes[Spase] {
    def writes(s: Spase): JsValue = {
      Json.obj("spase" -> Json.obj(
          "version" -> s.Version.toString, 
          "resources" -> s.ResourceEntity, 
          "lang" -> s.lang))
    }
  }
  
  // some elements make problems when directly converting (e.g. in full tree)
  implicit val datarecordWrites: Writes[scalaxb.DataRecord[Any]] = new Writes[scalaxb.DataRecord[Any]] {
    def writes(d: scalaxb.DataRecord[Any]): JsValue = {
      d.key match {
        case Some("Repository") => {
          d.value match {
            case n: NodeSeq => Json.obj("repository" -> scalaxb.fromXML[Repository](d.as[NodeSeq]))
            case r: Repository => Json.obj("repository" -> d.as[Repository])
          }
        }
        case Some("SimulationModel") => Json.obj("simulationModel" -> d.as[SimulationModel])
        case Some("SimulationRun") => { 
          d.value match {
            case n: NodeSeq => Json.obj("simulationRun" -> scalaxb.fromXML[SimulationRun](d.as[NodeSeq]))
            case r: SimulationRun => Json.obj("simulationRun" -> d.as[SimulationRun])
          }
        }
        case Some("NumericalOutput") => Json.obj("numericalOutput" -> d.as[NumericalOutput])
        case Some("Granule") => { 
          d.value match {
            case n: NodeSeq => Json.obj("granule" -> scalaxb.fromXML[Granule](d.as[NodeSeq]))
            case r: Granule => Json.obj("granule" -> d.as[Granule])
          }
        }
        // should never happen
        case _ => JsNull
      }
    }
  }
  
  
  // writer for repository
  implicit val repositoryWrites: Writes[Repository] = new Writes[Repository] {
    def writes(r: Repository): JsValue = {
      Json.obj(
          "resourceId" -> r.ResourceID, 
          "resourceHeader" -> r.ResourceHeader, 
          "accessUrl" -> r.AccessURL)
    }
  }
  
  implicit val accessURLWrites: Writes[AccessURL] = new Writes[AccessURL] {
    def writes(a: AccessURL): JsValue = Json.obj("url" -> a.URL, "productKey" -> a.ProductKey)
  }
  
  implicit val resourceHeaderWrites: Writes[ResourceHeader] = new Writes[ResourceHeader] {
    def writes(h: ResourceHeader): JsValue = {
      Json.obj(
          "resourceName" -> h.ResourceName, 
          "releaseDate" -> h.ReleaseDate, 
          "description" -> h.Description, 
          "contact" -> h.Contact, 
          "informationurl" -> h.InformationURL)
    }
  }
  
  implicit val calendarWrites: Writes[XMLGregorianCalendar] = new Writes[XMLGregorianCalendar] {
    def writes(t: XMLGregorianCalendar): JsValue = JsString(t.toString)
  }
  
  implicit val durationWrites: Writes[Duration] = new Writes[Duration] {
    def writes(d: Duration): JsValue = JsString(d.toString)
  }
  
  implicit val infoURLWrites: Writes[InformationURL] = new Writes[InformationURL] {
    def writes(u: InformationURL): JsValue = JsString(u.URL)
  }
  
  implicit val contactWrites: Writes[Contact] = new Writes[Contact] {
    def writes(c: Contact): JsValue = Json.obj("personId" -> c.PersonID, "role" -> c.Role)
  }
  
  implicit val roleWrites: Writes[EnumRole] = new Writes[EnumRole] {
    def writes(r: EnumRole): JsValue = JsString(r.toString)
  }
  
  
  // writer for simulation model
  implicit val simulationModelWrites: Writes[SimulationModel] = new Writes[SimulationModel] {
    def writes(s: SimulationModel): JsValue = {
      Json.obj(
          "resourceId" -> s.ResourceID, 
          "resourceHeader" -> s.ResourceHeader, 
          "versions" -> s.Versions, 
          "simulationType" -> s.SimulationType)
    }
  }
  
  implicit val simulationTypeWrites: Writes[EnumSimulationType] = new Writes[EnumSimulationType] {
    def writes(s: EnumSimulationType): JsValue = JsString(s.toString)
  }
  
  implicit val versionsWrites: Writes[Versions] = new Writes[Versions] {
    def writes(v: Versions): JsValue = Json.obj("modelVersion" -> v.ModelVersion)
  }
  
  implicit val modelVersionWrites: Writes[ModelVersion] = new Writes[ModelVersion] {
    def writes(v: ModelVersion): JsValue = { 
      Json.obj(
          "versionId" -> v.VersionID, 
          "releaseDate" -> v.ReleaseDate, 
          "description" -> v.Description)
    }
  }
  
  /* implicit val stringOptionWrites: Writes[Option[String]] = new Writes[Option[String]] {
    def writes(s: Option[String]): JsValue = s match {
      case Some(s) => JsString(s)
      case None => JsNull
    }
  }*/
  
  
  // writer for simulation run
  // input entities make problems when directly converting (e.g. in full tree)
  implicit val simulationRunWrites: Writes[SimulationRun] = new Writes[SimulationRun] {
    def writes(r: SimulationRun): JsValue = {
      val inputEntity: Seq[JsValue] = r.InputEntity map { entity => 
        entity.key match {
          case Some("InputField") => {
            entity.value match {
              case e: NodeSeq => Json.obj("inputField" -> scalaxb.fromXML[InputField](entity.as[NodeSeq]))
              case e: InputField => Json.obj("inputField" -> entity.as[InputField])
            }
          }
          case Some("InputParameter") => Json.obj("inputParameter" -> entity.as[InputParameter])
          case Some("InputPopulation") => Json.obj("inputPopulation" -> entity.as[InputPopulation])
          case Some("InputProcess") => { 
            entity.value match {
              case e: NodeSeq => Json.obj("inputProcess" -> scalaxb.fromXML[InputProcess](entity.as[NodeSeq]))
              case e: InputProcess => Json.obj("inputProcess" -> entity.as[InputProcess])
            }
          }
          // should never happen
          case _ => JsNull
        }
      }
      Json.obj(
          "resourceId" -> r.ResourceID, 
          "resourceHeader" -> r.ResourceHeader, 
          "modelId" -> r.Model.ModelID, 
          "temporalDependence" -> r.TemporalDependence.getOrElse("").toString, 
          "simulatedRegion" -> r.SimulatedRegion, 
          "likelihoodRating" -> r.LikelihoodRating, 
          "simulationTime" -> r.SimulationTime, 
          "simulationDomain" -> r.SimulationDomain, 
          "inputs" -> inputEntity)
    }
  }
  
  implicit val simulationTimeWrites: Writes[SimulationTime] = new Writes[SimulationTime] {
    def writes(s: SimulationTime): JsValue = {
      Json.obj(
          "duration" -> s.Duration, 
          "timeStart" -> s.TimeStart, 
          "timeStop" -> s.TimeStop, 
          "timeStep" -> s.TimeStep)
    }
  }
  
  implicit val durationOptionWrites: Writes[Option[Duration]] = new Writes[Option[Duration]] {
    def writes(d: Option[Duration]): JsValue = d match {
      case Some(d) => JsString(d.toString)
      case None => JsNull
    }
  }
  
  implicit val calenderOptionWrites: Writes[Option[XMLGregorianCalendar]] = new Writes[Option[XMLGregorianCalendar]] {
    def writes(c: Option[XMLGregorianCalendar]): JsValue = c match {
      case Some(c) => JsString(c.toString)
      case None => JsNull
    }
  }
  
  implicit val confidenceRatingWrites: Writes[EnumConfidenceRating] = new Writes[EnumConfidenceRating] {
    def writes(c: EnumConfidenceRating): JsValue = JsString(c.toString)
  }
 
  // boundaryConditions attribute is optional
  implicit val simulationDomainWrites: Writes[SimulationDomain] = new Writes[SimulationDomain] {
    def writes(s: SimulationDomain): JsValue = {
      val boundaryConditions = s.BoundaryConditions match {
        case Some(b) => Json.obj("boundaryConditions" -> b)
        case None => Json.obj()
      }
      Json.obj(
          "description" -> s.Description, 
          "caveats" -> s.Caveats,
          "spatialDimension" -> s.SpatialDimension, 
          "velocityDimension" -> s.VelocityDimension , 
          "fieldDimension" -> s.FieldDimension, 
          "units" -> s.Units, 
          "unitsConversion" -> s.UnitsConversion, 
          "coordinatesLabel" -> s.CoordinatesLabel, 
          "validMin" -> s.ValidMin, 
          "validMax" -> s.ValidMax, 
          "gridStructure" -> s.GridStructure, 
          "gridCellSize" -> s.GridCellSize, 
          "symmetry" -> s.Symmetry)++
          boundaryConditions
    }
  }
  
  implicit val bigIntWrites: Writes[BigInt] = new Writes[BigInt] {
    def writes(i: BigInt): JsValue = JsString(i.toString)
  }
  
  implicit val symmetryWrites: Writes[EnumSymmetry] = new Writes[EnumSymmetry] {
    def writes(s: EnumSymmetry): JsValue = JsString(s.toString)
  }
  
  implicit val boundaryConditionsWrites: Writes[BoundaryConditions] = new Writes[BoundaryConditions] {
    def writes(b: BoundaryConditions): JsValue = { 
      Json.obj("fieldBoundary" -> b.FieldBoundary, "particleBoundary" -> b.ParticleBoundary)
    }
  }
  
  implicit val elementBoundaryWrites: Writes[Option[ElementBoundary]] = new Writes[Option[ElementBoundary]] {
    def writes(e: Option[ElementBoundary]): JsValue = e match {
      case Some(e) => Json.obj(
          "frontWall" -> e.FrontWall, 
          "backWall" -> e.BackWall, 
          "sideWall" -> e.SideWall, 
          "obstacle" -> e.Obstacle, 
          "caveats" -> e.Caveats)
      case None => JsNull
    }
  } 

  implicit val inputFieldWrites: Writes[InputField] = new Writes[InputField] {
    def writes(i: InputField): JsValue = { 
      Json.obj(
          "name" -> i.Name, 
          "set" -> i.Set, 
          "parameterKey" -> i.ParameterKey,
          "description" -> i.Description, 
          "caveats" -> i.Caveats, 
          "simulatedRegion" -> i.SimulatedRegion,
          "coordinateSystem" -> i.CoordinateSystem, 
          "qualifier" -> i.Qualifier, 
          "fieldQuantity" -> i.FieldQuantity,
          "units" -> i.Units, 
          "unitsConversion" -> i.UnitsConversion, 
          "inputLabel" -> i.InputLabel, 
          "fieldValue" -> i.FieldValue,
          "inputTableUrl" -> i.InputTableURL, 
          "validMin" -> i.ValidMin, 
          "validMax" -> i.ValidMax, 
          "fieldModel" -> i.FieldModel,
          "modelUrl" -> i.ModelURL)
    }
  }
  
  implicit val qualifierWrites: Writes[EnumQualifier] = new Writes[EnumQualifier] {
    def writes(q: EnumQualifier): JsValue = JsString(q.toString)
  }

  implicit val fieldQuantityWrites: Writes[EnumFieldQuantity] = new Writes[EnumFieldQuantity] {
    def writes(q: EnumFieldQuantity): JsValue = JsString(q.toString)
  }
  
  implicit val urlOptionWrites: Writes[Option[URI]] = new Writes[Option[URI]] {
    def writes(u: Option[URI]): JsValue = u match {
      case Some(u) => JsString(u.toString)
      case None => JsNull
    }
  }

  implicit val inputParameterWrites: Writes[InputParameter] = new Writes[InputParameter] {
    def writes(i: InputParameter): JsValue = { 
      Json.obj(
          "name" -> i.Name, 
          "description" -> i.Description, 
          "caveats" -> i.Caveats, 
          "simulatedRegion" -> i.SimulatedRegion,
          "qualifier" -> i.Qualifier, 
          "parameterQuantity" -> i.ParameterQuantity, 
          "property" -> i.Property)
    }
  }
  
  implicit val propertyWrites: Writes[Property] = new Writes[Property] {
    def writes(p: Property): JsValue = {
      Json.obj(
          "name" -> p.Name, 
          "description" -> p.Description, 
          "caveats" -> p.Caveats, 
          "propertyQuantity" -> p.PropertyQuantity,
          "qualifier" -> p.Qualifier, 
          "units" -> p.Units, 
          "unitsConversion" -> p.UnitsConversion, 
          "propertyLabel" -> p.PropertyLabel, 
          "propertyValue" -> p.PropertyValue, 
          "propertyTableUrl" -> p.PropertyTableURL, 
          "validMin" -> p.ValidMin, 
          "validMax" -> p.ValidMax, 
          "propertyModel" -> p.PropertyModel, 
          "modelUrl" -> p.ModelURL)
    } 
  }
  
  implicit val inputPopulationWrites: Writes[InputPopulation] = new Writes[InputPopulation] {
    def writes(i: InputPopulation): JsValue = { 
      Json.obj(
          "name" -> i.Name, 
          "set" -> i.Set, 
          "parameterKey" -> i.ParameterKey, 
          "description" -> i.Description,
          "caveats" -> i.Caveats,
          "simulatedRegion" -> i.SimulatedRegion,
          "qualifier" -> i.Qualifier,
          "particleType" -> i.ParticleType,
          "chemicalFormula" -> i.ChemicalFormula,
          "atomicNumber" -> i.AtomicNumber,
          "populationMassNumber" -> i.PopulationMassNumber,
          "populationChargeState" -> i.PopulationChargeState,
          "populationDensity" -> i.PopulationDensity,
          "populationTemperature" -> i.PopulationTemperature,
          "populationFlowSpeed" -> i.PopulationFlowSpeed,
          "distribution" -> i.Description,
          "productionRate" -> i.ProductionRate,
          "totalProductionRate" -> i.TotalProductionRate,
          "inputTableURL" -> i.InputTableURL,
          "profile" -> i.Profile,
          "modelURL" -> i.ModelURL)
    }
  }
  
  implicit val inputValueWrites: Writes[Option[InputValue]] = new Writes[Option[InputValue]] {
    def writes(p: Option[InputValue]): JsValue = p match { 
      case Some(p) => Json.obj(
          "mixed" -> JsString(p.mixed.map(_.as[String]).mkString(" ")),
          "units" -> p.Units,
          "unitsConversion" -> p.UnitsConversion)
      case None => JsNull
    }
    
  }

  implicit val inputProcessWrites: Writes[InputProcess] = new Writes[InputProcess] {
    def writes(i: InputProcess): JsValue = { 
      Json.obj(
          "name" -> i.Name, 
          "set" -> i.Set, 
          "parameterKey" -> i.ParameterKey,
          "description" -> i.Description, 
          "caveats" -> i.Caveats,
          "simulatedRegion" -> i.SimulatedRegion,
          "processType" -> i.ProcessType, 
          "units" -> i.Units, 
          "unitsConversion" -> i.UnitsConversion, 
          "processCoefficient" -> i.ProcessCoefficient, 
          "processCoeffType" -> i.ProcessCoeffType, 
          "processModel" -> i.ProcessModel, 
          "modelUrl" -> i.ModelURL)
    }
  }
  
  implicit val processTypeWrites: Writes[EnumProcessType] = new Writes[EnumProcessType] {
    def writes(q: EnumProcessType): JsValue = JsString(q.toString)
  }
  
  implicit val processCoeffTypeOptionWrites: Writes[Option[EnumProcCoefType]] = new Writes[Option[EnumProcCoefType]] {
    def writes(p: Option[EnumProcCoefType]): JsValue = p match { 
      case Some(p) => JsString(p.toString)
      case None => JsNull
    }
  }

  
  // writer for numerical output
  // temporalDescription, spatialDescription attribute is optional
  implicit val numericalOutputWrites: Writes[NumericalOutput] = new Writes[NumericalOutput] {
    def writes(n: NumericalOutput): JsValue = {
      val nOption = n.numericaloutputoption match {
            case Some(o) if (o.key.get == "SpatialDescription") => Json.obj("spatialDescription" -> o.as[SpatialDescription])
            case Some(o) if (o.key.get == "TemporalDescription") => Json.obj("temporalDescription" -> o.as[TemporalDescription])
            // should never happen
            case _ => Json.obj()
      }
      Json.obj(
          "resourceId" -> n.ResourceID, 
          "resourceHeader" -> n.ResourceHeader, 
          "accessInformation" -> n.AccessInformation,
          "measurementType" -> n.MeasurementType)++nOption++
      Json.obj(
          "simulatedRegion" -> n.SimulatedRegion, 
          "inputResourceId" -> n.InputResourceID, 
          "parameter" -> n.Parameter)
    }
  }

  implicit val accessInformationWrites: Writes[AccessInformation] = new Writes[AccessInformation] {
    def writes(a: AccessInformation): JsValue = {
      Json.obj(
          "repositoryId" -> a.RepositoryID, 
          "availability" -> a.Availability,
          "accessUrl" -> a.AccessURL, 
          "format" -> a.Format, 
          "encoding" -> a.Encoding, 
          "dataExtent" -> a.DataExtent)
    }
  }
  
  implicit val availabilityWrites: Writes[Option[EnumAvailability]] = new Writes[Option[EnumAvailability]] {
    def writes(a: Option[EnumAvailability]): JsValue = a match {
      case Some(a) => JsString(a.toString)
      case None => JsNull
    }
  } 
  
  implicit val formatWrites: Writes[EnumFormat] = new Writes[EnumFormat] {
    def writes(f: EnumFormat): JsValue = JsString(f.toString)
  } 
  
  implicit val encodingWrites: Writes[Option[EnumEncoding]] = new Writes[Option[EnumEncoding]] {
    def writes(e: Option[EnumEncoding]): JsValue = e match {
      case Some(e) => JsString(e.toString)
      case None => JsNull
    }
  } 
  
  // units and per attribute is optional
  implicit val dataExtentWrites: Writes[DataExtent] = new Writes[DataExtent] {
    def writes(d: DataExtent): JsValue = { 
      val units = d.Units match {
        case Some(u) => Json.obj("units" -> u)
        case _ => Json.obj()
      }
      val per = d.Per match {
        case Some(p) => Json.obj("per" -> p)
        case _ => Json.obj()
      }
      Json.obj("quantity" -> d.Quantity)++units++per
    }
  }
  
  implicit val measurementTypeWrites: Writes[EnumMeasurementType] = new Writes[EnumMeasurementType] {
    def writes(m: EnumMeasurementType): JsValue = JsString(m.toString)
  }
  
  // parameter attribute is optional
  // some elements make problems when directly converting (e.g. in full tree)
  implicit val parameterWrites: Writes[Parameter] = new Writes[Parameter] {
    def writes(p: Parameter): JsValue = {
      val param = p.ParameterEntity.key match {
        case Some("Field") => Json.obj("field" -> p.ParameterEntity.as[FieldType])
        case Some("Wave") =>  Json.obj("wave" -> p.ParameterEntity.as[Wave])
        case Some("Mixed") => Json.obj("mixed" -> p.ParameterEntity.as[Mixed])
        //case Some("Support") => Json.obj("support" -> p.ParameterEntity.as[Support])
        case Some("Support") => Json.obj("support" -> scalaxb.fromXML[Support](p.ParameterEntity.as[NodeSeq]))
        case Some("Particle") => Json.obj("particle" -> p.ParameterEntity.as[Particle])
        // should never happen
        case _ => Json.obj()
      }
      Json.obj(
          "name" -> p.Name, 
          "set" -> p.Set, 
          "parameterKey" -> p.ParameterKey, 
          "description" -> p.Description, 
          "caveats" -> p.Caveats, 
          "cadence" -> p.Cadence, 
          "units" -> p.Units,
          "unitsConversion" -> p.UnitsConversion, 
          "coordinateSystem" -> p.CoordinateSystem, 
          "renderingHints" -> p.RenderingHints,
          "structure" -> p.Structure, 
          "validMin" -> p.ValidMin, 
          "validMax" -> p.ValidMax, 
          "fillValue" -> p.FillValue, 
          "property" -> p.Property)++param
    }
  }
  
  implicit val coordinateSystemWrites: Writes[CoordinateSystemType] = new Writes[CoordinateSystemType] {
    def writes(c: CoordinateSystemType): JsValue = {
      Json.obj(
          "coordinateRepresentation" -> c.CoordinateRepresentation, 
          "coordinateSystem" -> c.CoordinateSystemName)
    }
  }
  
  implicit val coordRepresentationWrites: Writes[EnumCoordinateRepresentation] = new Writes[EnumCoordinateRepresentation] {
    def writes(c: EnumCoordinateRepresentation): JsValue = JsString(c.toString)
  }  
  
  implicit val coordSystemWrites: Writes[EnumCoordinateSystemName] = new Writes[EnumCoordinateSystemName] {
    def writes(c: EnumCoordinateSystemName): JsValue = JsString(c.toString)
  } 
  
  implicit val renderingWrites: Writes[RenderingHints] = new Writes[RenderingHints] {
    def writes(r: RenderingHints): JsValue = {
      Json.obj(
          "axisLabel" -> r.AxisLabel, 
          "displayType" -> r.DisplayType, 
          "index" -> r.Index, 
          "renderingAxis" -> r.RenderingAxis, 
          "scaleMax" -> r.ScaleMax, 
          "scaleMin" -> r.ScaleMin, 
          "scaleType" -> r.ScaleType, 
          "valueFormat" -> r.ValueFormat)
    }
  }
  
  implicit val displayTypeWrites: Writes[EnumDisplayType] = new Writes[EnumDisplayType] {
    def writes(d: EnumDisplayType): JsValue = JsString(d.toString)
  }
  
  implicit val renderingAxisWrites: Writes[EnumRenderingAxis] = new Writes[EnumRenderingAxis] {
    def writes(r: EnumRenderingAxis): JsValue = JsString(r.toString)
  }
  
  implicit val scaleTypeWrites: Writes[EnumScaleType] = new Writes[EnumScaleType] {
    def writes(s: EnumScaleType): JsValue = JsString(s.toString)
  }
  
  implicit val structureWrites: Writes[Structure] = new Writes[Structure] {
    def writes(s: Structure): JsValue = Json.obj(
        "description" -> s.Description, 
        "element" -> s.Element, 
        "size" -> s.Size)
    
  }
  
  implicit val elementWrites: Writes[Element] = new Writes[Element] {
    def writes(e: Element): JsValue = {
      Json.obj(
          "fillValue" -> e.FillValue, 
          "index" -> e.Index, 
          "name" -> e.Name, 
          "parameterKey" -> e.ParameterKey, 
          "qualifier" -> e.Qualifier, 
          "renderingHints" -> e.RenderingHints, 
          "units" -> e.Units, 
          "unitsConversion" -> e.UnitsConversion, 
          "validMax" -> e.ValidMax, 
          "validMin" -> e.ValidMin)
    }
  }
  
  implicit val fieldWrites: Writes[FieldType] = new Writes[FieldType] {
    def writes(f: FieldType): JsValue = {
      Json.obj(
          "fieldQuantity" -> f.FieldQuantity, 
          "frequencyRange" -> f.FrequencyRange, 
          "qualifier" -> f.Qualifier)
    }
  }
  
  implicit val frequencyRangeWrites: Writes[FrequencyRange] = new Writes[FrequencyRange] {
    def writes(f: FrequencyRange): JsValue = {
      Json.obj(
          "bin" -> f.Bin, 
          "high" -> f.High, 
          "low" -> f.Low, 
          "spectralRange" -> f.SpectralRange, 
          "units" -> f.Units)
    }
  }
  
  implicit val binWrites: Writes[Bin] = new Writes[Bin] {
    def writes(b: Bin): JsValue = Json.obj("bandName" -> b.BandName, "high" -> b.High, "low" -> b.Low)
  }
  
  implicit val spectralRangeWrites: Writes[EnumSpectralRange] = new Writes[EnumSpectralRange] {
    def writes(s: EnumSpectralRange): JsValue = JsString(s.toString)
  } 
  
  implicit val waveWrites: Writes[Wave] = new Writes[Wave] {
    def writes(w: Wave): JsValue = {
      Json.obj(
          "energyRange" -> w.EnergyRange, 
          "frequencyRange" -> w.FrequencyRange, 
          "qualifier" -> w.Qualifier, 
          "wavelengthRange" -> w.WavelengthRange, 
          "waveQuantity" -> w.WaveQuantity, 
          "waveType" -> w.WaveType)
    }
  }
  
  implicit val energyRangeWrites: Writes[EnergyRange] = new Writes[EnergyRange] {
    def writes(e: EnergyRange): JsValue = {
      Json.obj("bin" -> e.Bin, "high" -> e.High, "low" -> e.Low, "units" -> e.Units)
    }
  }
  
  implicit val wavelengthRangeWrites: Writes[WavelengthRange] = new Writes[WavelengthRange] {
    def writes(w: WavelengthRange): JsValue = {
      Json.obj(
          "bin" -> w.Bin, 
          "high" -> w.High, 
          "low " -> w.Low, 
          "spectralRange" -> w.SpectralRange, 
          "units" -> w.Units)
    }
  }
  
  implicit val waveQuantityWrites: Writes[EnumWaveQuantity] = new Writes[EnumWaveQuantity] {
    def writes(w: EnumWaveQuantity): JsValue = JsString(w.toString)
  }
  
  implicit val waveTypeWrites: Writes[EnumWaveType] = new Writes[EnumWaveType] {
    def writes(w: EnumWaveType): JsValue = JsString(w.toString)
  }
 
  implicit val mixedWrites: Writes[Mixed] = new Writes[Mixed] {
    def writes(m: Mixed): JsValue = {
      Json.obj("mixedQuantity" -> m.MixedQuantity, "particleType" -> m.ParticleType, "qualifier" -> m.Qualifier)
    }
  }
  
  implicit val mixedQuantityWrites: Writes[EnumMixedQuantity] = new Writes[EnumMixedQuantity] {
    def writes(m: EnumMixedQuantity): JsValue = JsString(m.toString)
  }
  
  implicit val supportWrites: Writes[Support] = new Writes[Support] {
    def writes(s: Support): JsValue = Json.obj("supportQuantity" -> s.SupportQuantity, "qualifier" -> s.Qualifier) 
  }
  
  implicit val supportQuantityWrites: Writes[EnumSupportQuantity] = new Writes[EnumSupportQuantity] {
    def writes(s: EnumSupportQuantity): JsValue = JsString(s.toString)
  }
  
  implicit val particleWrites: Writes[Particle] = new Writes[Particle] {
    def writes(p: Particle): JsValue = {
      Json.obj(
          "populationId" -> p.PopulationID, 
          "particleType" -> p.ParticleType, 
          "particleQuantitiy" -> p.ParticleQuantity,
          "chemicalFormula" -> p.ChemicalFormula, 
          "atomicNumber" -> p.AtomicNumber, 
          "populationMassNumber" -> p.PopulationMassNumber, 
          "populationChargeState" -> p.PopulationChargeState)
    }
  }
  
  implicit val particleTypeWrites: Writes[EnumParticleType] = new Writes[EnumParticleType] {
    def writes(p: EnumParticleType): JsValue = JsString(p.toString)
  }
  
  implicit val particleQuantityWrites: Writes[EnumParticleQuantity] = new Writes[EnumParticleQuantity] {
    def writes(p: EnumParticleQuantity): JsValue = JsString(p.toString)
  }
   
  // spatialDescription attribute is optional
  implicit val spatialDescriptionWrites: Writes[SpatialDescription] = new Writes[SpatialDescription] {
    def writes(s: SpatialDescription): JsValue = {
      val spatialDescription = s.spatialdescriptionoption.key match {
        case Some("CubeDescriptionSequence") => 
          Json.obj("cubeDescription" -> s.spatialdescriptionoption.as[CubesDescriptionSequence])
        case Some("CutsDescriptionSequence") => 
          Json.obj("cutsDescription" -> s.spatialdescriptionoption.as[CutsDescriptionSequence])
        // should never happen
        case _ => Json.obj()
      }
      Json.obj(
          "dimension" -> s.Dimension.toString, 
          "coordinateSystem" -> s.CoordinateSystem, 
          "coordinatesLabel" -> s.CoordinatesLabel, 
          "units" -> s.Units)++spatialDescription
    }
  }
  
  implicit val cubesDescriptionWrites: Writes[CubesDescriptionSequence] = new Writes[CubesDescriptionSequence] {
    def writes(c: CubesDescriptionSequence): JsValue = {
      Json.obj("regionBegin" -> c.RegionBegin, "regionEnd" -> c.RegionEnd)
    }
  }
  
  implicit val cutsDescriptionWrites: Writes[CutsDescriptionSequence] = new Writes[CutsDescriptionSequence] {
    def writes(c: CutsDescriptionSequence): JsValue = {
      Json.obj("planeNormalVector" -> c.PlaneNormalVector, "planePoint" -> c.PlanePoint)
    }
  }
  
  implicit val temporalDescriptionWrites: Writes[TemporalDescription] = new Writes[TemporalDescription] {
    def writes(t: TemporalDescription): JsValue = {
      Json.obj("cadence" -> t.Cadence, "exposure" -> t.Exposure, "timespan" -> t.TimeSpan)
    }
  }
  
  // stopDate makes problems => see hack below
  implicit val timeSpanWrites: Writes[TimeSpan] = new Writes[TimeSpan] {
    def writes(t: TimeSpan): JsValue = {
      Json.obj("startDate" -> t.StartDate, 
          "stopDate" -> scalaxb.fromXML[XMLGregorianCalendar](t.StopDateEntity.as[NodeSeq]), 
          "note" -> t.Note)
    }
  }
  
  
  // writer for granule
  // @TODO only regions and timespans are avaialable, maybe more in the future?
  implicit val granuleWrites: Writes[Granule] = new Writes[Granule] {
    def writes(g: Granule): JsValue = {
      val params = g.granuleoption.key match {
        case Some("RegionBegin") => Json.obj("regionBegin" -> g.granuleoption.value.toString)
        case Some("StartDate") => Json.obj("startDate" -> g.granuleoption.value.toString)
        // should never happen
        case _ => Json.obj()
      } 
      val optParams = g.granuleoption2 match {
        case Some(o) if(o.key == Some("RegionEnd")) => 
          Json.obj("regionEnd" -> o.value.toString)
        case Some(o) if(o.key == Some("StopDate")) =>
          Json.obj("stopDate" -> o.value.toString)
        // should never happen
        case _ => Json.obj()
      }
      Json.obj("resourceId" -> g.ResourceID, "releaseDate" -> g.ReleaseDate, 
          "parentId" -> g.ParentID)++params++optParams++Json.obj("source" -> g.Source)
    }
  }
  
  // @TODO there are a few more options (not used at the moment)
  implicit val sourceWrites: Writes[Source] = new Writes[Source] {
    def writes(s: Source): JsValue = {
      Json.obj("sourceType" -> s.SourceType, "url" -> s.URL)
    }
  }
  
  implicit val sourceTypeWrites: Writes[EnumSourceType] = new Writes[EnumSourceType] {
    def writes(s: EnumSourceType) = JsString(s.toString)
  }
  
  
}