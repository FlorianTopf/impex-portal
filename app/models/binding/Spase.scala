package models.binding

import play.api.libs.json._
import models.binding._ 
import scala.xml._
import javax.xml.datatype._

/** 
               Space Physics Archive Search and Extract (SPASE).
               The outermost container or envelope for SPASE
               metadata. This indicates the start of the
               SPASE metadata.
            
*/
case class Spase(Version: models.binding.EnumVersion,
  ResourceEntity: Seq[scalaxb.DataRecord[Any]] = Nil,
  lang: String)


object Spase {
 type JsField = (String, JsValue)
  
 implicit val spaseWrites: Writes[Spase] = new Writes[Spase] {
    def writes(s: Spase): JsValue = {
      Json.obj("spase" -> Json.obj("version" -> s.Version.toString, "resources" -> s.ResourceEntity, "lang" -> s.lang))
    }
  }
  
  // @TODO some elements make problems when directly converting (e.g. in full tree)
  implicit val datarecordsWrites: Writes[scalaxb.DataRecord[Any]] = new Writes[scalaxb.DataRecord[Any]] {
    def writes(d: scalaxb.DataRecord[Any]) = {
      d.key match {
        case Some("Repository") => {
          d.value match {
            case n: NodeSeq => Json.obj("repository" -> scalaxb.fromXML[Repository](d.as[NodeSeq]))
            case r: Repository => Json.obj("repository" -> d.as[Repository])
          }
        }
        case Some("SimulationModel") => Json.obj("simulationModel" -> d.as[SimulationModelType])
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
        case _ => Json.obj()
      }
    }
  }
  
  implicit val repositoryWrites: Writes[Repository] = new Writes[Repository] {
    def writes(r: Repository) = {
      Json.obj("resourceId" -> r.ResourceID, "resourceHeader" -> r.ResourceHeader, 
          "accessUrl" -> r.AccessURL)
    }
  }
  
  implicit val accessURLWrites: Writes[AccessURL] = new Writes[AccessURL] {
    def writes(a: AccessURL) = Json.obj("url" -> a.URL, "productKey" -> a.ProductKey)
  }
  
  implicit val resourceHeaderWrites: Writes[ResourceHeader] = new Writes[ResourceHeader] {
    def writes(h: ResourceHeader) = {
      Json.obj("resourceName" -> h.ResourceName, "releaseDate" -> h.ReleaseDate, 
          "description" -> h.Description, "contact" -> h.Contact, "informationurl" -> h.InformationURL)
    }
  }
  
  implicit val timeStampWrites: Writes[XMLGregorianCalendar] = new Writes[XMLGregorianCalendar] {
    def writes(t: XMLGregorianCalendar) = JsString(t.toString)
  }
  
  implicit val infoURLWrites: Writes[InformationURL] = new Writes[InformationURL] {
    def writes(u: InformationURL) = JsString(u.URL)
  }
  
  implicit val contactWrites: Writes[Contact] = new Writes[Contact] {
    def writes(c: Contact) = {
      Json.obj("personId" -> c.PersonID, "role" -> c.Role)
    }
  }
  
  implicit val roleWrites: Writes[EnumRole] = new Writes[EnumRole] {
    def writes(r: EnumRole) = JsString(r.toString)
  }
  
  implicit val simulationModelWrites: Writes[SimulationModelType] = new Writes[SimulationModelType] {
    def writes(s: SimulationModelType) = {
      Json.obj("resourceID" -> s.ResourceID, "resourceHeader" -> s.ResourceHeader, "versions" -> s.Versions, 
          "simulationType" -> s.SimulationType.toString)
    }
  }
  
  implicit val versionsWrites: Writes[Versions] = new Writes[Versions] {
    def writes(v: Versions) = {
      Json.obj("modelVersion" -> v.ModelVersion)
    }
  }
  
  implicit val modelVersionWrites: Writes[ModelVersion] = new Writes[ModelVersion] {
    def writes(v: ModelVersion) = { 
      Json.obj("versionId" -> v.VersionID, "releaseDate" -> v.ReleaseDate, 
         "description" -> v.Description.getOrElse("").toString)
    }
  }

  implicit val simulationRunWrites: Writes[SimulationRun] = new Writes[SimulationRun] {
    def writes(r: SimulationRun) = { 
      Json.obj("resourceId" -> r.ResourceID, "resourceHeader" -> r.ResourceHeader, 
          "modelId" -> r.Model.ModelID, "temporalDependence" -> r.TemporalDependence.getOrElse("").toString, 
          "simulatedRegion" -> r.SimulatedRegion, "likelyhoodRating" -> r.LikelihoodRating.toString, 
          "simulationTime" -> r.SimulationTime)
    }
  }
  
  implicit val simulationTimeWrites: Writes[SimulationTime] = new Writes[SimulationTime] {
    def writes(s: SimulationTime) = {
      Json.obj("duration" -> s.Duration.getOrElse("").toString, "timeStart" -> s.TimeStart.getOrElse("").toString, 
          "timeStop" -> s.TimeStop.getOrElse("").toString, "timeStep" -> s.TimeStep.getOrElse("").toString)
    }
  }
  
  implicit val numericalOutputWrites: Writes[NumericalOutput] = new Writes[NumericalOutput] {
    def writes(n: NumericalOutput) = {
      val nOption = n.numericaloutputoption match {
            case Some(o) if (o.key.get == "SpatialDescription") => Json.obj("spatialDescription" -> o.as[SpatialDescription])
            case Some(o) if (o.key.get == "TemporalDescription") => Json.obj("temporalDescription" -> o.as[TemporalDescription])
            case _ => Json.obj()
      }
      
      Json.obj("resourceId" -> n.ResourceID, "resourceHeader" -> n.ResourceHeader, "accessInformation" -> n.AccessInformation,
          "measurementType" -> n.MeasurementType)++nOption++
      Json.obj("simulatedRegion" -> n.SimulatedRegion, "inputResourceId" -> n.InputResourceID, "parameter" -> n.Parameter)
    }
  }
  
  implicit val parameterWrites: Writes[ParameterType] = new Writes[ParameterType] {
    def writes(p: ParameterType) = {
      val param = p.ParameterEntity.key match {
        case Some("Particle") => Json.obj("particle" -> p.ParameterEntity.as[Particle])
        case _ => Json.obj()
      }
      
      Json.obj("name" -> p.Name, "parameterKey" -> p.ParameterKey.getOrElse("").toString, 
          "description" -> p.Description.getOrElse("").toString, "units" -> p.Units)++param
    }
  }
  
  implicit val particleWrites: Writes[Particle] = new Writes[Particle] {
    def writes(p: Particle) = {
      Json.obj("populationId" -> p.PopulationID, "particleType" -> p.ParticleType, "particleQuantitiy" -> p.ParticleQuantity,
          "chemicalFormula" -> p.ChemicalFormula.getOrElse("").toString, "atomicNumber" -> p.AtomicNumber, 
          "populationMassNumber" -> p.PopulationMassNumber, "populationChargeState" -> p.PopulationChargeState)
    }
  }
  
  implicit val particleTypeWrites: Writes[EnumParticleType] = new Writes[EnumParticleType] {
    def writes(p: EnumParticleType) = JsString(p.toString)
  }
  
  implicit val particleQuantityWrites: Writes[EnumParticleQuantity] = new Writes[EnumParticleQuantity] {
    def writes(p: EnumParticleQuantity) = JsString(p.toString)
  }
  
  implicit val spatialDescriptionWrites: Writes[SpatialDescription] = new Writes[SpatialDescription] {
    def writes(s: SpatialDescription) = {
      val spatialDesc = s.spatialdescriptionoption.key match {
        case Some("CubeDescriptionSequence") => Json.obj("cubeDescription" -> s.spatialdescriptionoption.as[CubesDescriptionSequence])
        case Some("CutsDescriptionSequence") => Json.obj("cutsDescription" -> s.spatialdescriptionoption.as[CutsDescriptionSequence])
        case _ => Json.obj()
      }
      Json.obj("dimension" -> s.Dimension.toString, "coordinateSystem" -> s.CoordinateSystem, "coordinatesLabel" -> s.CoordinatesLabel, 
          "units" -> s.Units)++spatialDesc
    }
  }
  
  implicit val cubesDescriptionWrites: Writes[CubesDescriptionSequence] = new Writes[CubesDescriptionSequence] {
    def writes(c: CubesDescriptionSequence) = {
      Json.obj("regionBegin" -> c.RegionBegin, "regionEnd" -> c.RegionEnd)
    }
  }
  
  implicit val cutsDescriptionWrites: Writes[CutsDescriptionSequence] = new Writes[CutsDescriptionSequence] {
    def writes(c: CutsDescriptionSequence) = {
      Json.obj("planeNormalVector" -> c.PlaneNormalVector, "planePoint" -> c.PlanePoint)
    }
  }
  
  implicit val coordinateSystemWrites: Writes[CoordinateSystem] = new Writes[CoordinateSystem] {
    def writes(c: CoordinateSystem) = {
      Json.obj("coordinateRepresentation" -> c.CoordinateRepresentation.toString, 
          "coordinateSystem" -> c.CoordinateSystemName.toString)
    }
    
  }
  
  implicit val temporalDescriptionWrites: Writes[TemporalDescription] = new Writes[TemporalDescription] {
    def writes(t: TemporalDescription) = {
      Json.obj("cadence" -> t.Cadence.getOrElse("").toString, 
          "exposure" -> t.Exposure.getOrElse("").toString, "timespan" -> t.TimeSpan)
    }
  }
  
  // @TODO stopDate makes problems => see hack below
  implicit val timeSpanWrites: Writes[TimeSpan] = new Writes[TimeSpan] {
    def writes(t: TimeSpan) = {
      Json.obj("startDate" -> t.StartDate.toString, 
          "stopDate" -> scalaxb.fromXML[XMLGregorianCalendar](t.StopDateEntity.as[NodeSeq]), "note" -> t.Note)
    }
  }
  
  implicit val measurementTypeWrites: Writes[EnumMeasurementType] = new Writes[EnumMeasurementType] {
    def writes(m: EnumMeasurementType) = JsString(m.toString)
  }
  
  implicit val accessInformationWrites: Writes[AccessInformation] = new Writes[AccessInformation] {
    def writes(a: AccessInformation) = {
      Json.obj("repositoryId" -> a.RepositoryID, "availability" -> a.Availability.getOrElse("").toString,
          "accessUrl" -> a.AccessURL, "format" -> a.Format.toString, "encoding" -> a.Encoding.getOrElse("").toString, 
          "dataExtent" -> a.DataExtent)
    }
  }
  
  // @TODO there are more elements existing here
  implicit val dataExtentWrites: Writes[DataExtent] = new Writes[DataExtent] {
    def writes(d: DataExtent) = Json.obj("quantity" -> d.Quantity)
  }
  
  implicit val granuleWrites: Writes[Granule] = new Writes[Granule] {
    def writes(g: Granule) = {
      Json.obj("resourceId" -> g.ResourceID)
    }
  }
  
  
}