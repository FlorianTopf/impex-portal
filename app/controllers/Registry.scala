package controllers

import models.actor._
import models.actor.DataProvider
import models.binding._
import models.enums._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil
import javax.ws.rs._
import javax.ws.rs.core.MediaType._


@Api(
    value = "/registry", 
    description = "operations for using the IMPEx registry services")
@Path("/registry")
@Produces(Array(APPLICATION_XML, APPLICATION_JSON))
object Registry extends BaseController {

  // gets update and validation status
  def status() = PortalAction.async {
    RegistryService.getStatus.map(s => Ok(Json.toJson(s)))
  }

  @GET
  @ApiOperation(
      value = "get regions", 
      nickname = "getRegions",
      notes = "returns a list of all stored targets in the registry", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown provider"), 
    new ApiResponse(code = 400, message = "not implemented")))
  def getRegions() = PortalAction.async { 
    for {
      // we can take observatories/simulationruns here (is faster)
      f1 <- RegistryService.getObservatory(None, false)
      f2 <- RegistryService.getSimulationRun(None, false)
    } yield { (f1, f2) match {
      	case (Left(obs), Left(run)) => {
      	   val obsRegions = obs.ResourceEntity.flatMap{ r => 
      	     val obs = r.as[Observatory]
      	     obs.Location.ObservatoryRegion.map(_.toString)
      	   }
      	   val runRegions = run.ResourceEntity.flatMap{ r => 
             val run = r.as[SimulationRun]
             run.SimulatedRegion
      	   }
      	   Ok(Json.toJson((obsRegions++runRegions).map(r => {
      	     if(r.contains(".")) r.split("\\.").head
      	     else r 
      	     // @FIXME hack for AMDA (Asteroid Region is not valid atm)
      	   }).distinct.filterNot(_.contains("Asteroid"))))
      	}
      	case (Right(error), _) => BadRequest(Json.toJson(error))
      	case (_, Right(error)) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  @GET
  @ApiOperation(
      value = "filter region", 
      nickname = "filterRegion",
      notes = "returns a list of matching repositories to a given target", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "unkown element")))
  def filterRegion(
      @ApiParam(
          value = "name of region available in the registry",
          defaultValue = "Earth")
      @PathParam("regionName") regionName: String) = PortalAction.async { 
	for {
	  // we must take data and output here (we need repository reference)
      f1 <- RegistryService.getNumericalData(None, false)
      f2 <- RegistryService.getNumericalOutput(None, false)
    } yield { (f1, f2) match {
      	case (Left(data), Left(output)) => {
      	   val dataRegions = data.ResourceEntity.flatMap{ r => 
      	     val data = r.as[NumericalData]
      	     // matches e.g. Earth => Earth, Earth.Magnetosphere
      	     if(data.AccessInformation.length > 0 && 
      	         data.ObservedRegion.filter(_.toString.contains(regionName)).length > 0)
      	       Some(data.AccessInformation.head.RepositoryID)
      	     else
      	       None
      	   }
      	   val outRegions = output.ResourceEntity.flatMap{ r => 
            val run = r.as[NumericalOutput]
            // matches e.g. Earth => Earth, Earth.Magnetosphere
            if(run.AccessInformation.length > 0 && 
                run.SimulatedRegion.filter(_.contains(regionName)).length > 0) {
              Some(run.AccessInformation.head.RepositoryID)
            } else
              None
      	   }
      	   (outRegions++dataRegions).distinct match {
      	     case x::xs => Ok(Json.toJson(x::xs))
      	     case Nil => BadRequest(Json.toJson(RequestError(ERequestError.UNKNOWN_ENTITY)))
      	   }
      	}
      	case (Right(error), _) => BadRequest(Json.toJson(error))
      	case (_, Right(error)) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  @GET
  @ApiOperation(
      value = "get registry", 
      nickname = "getRegistry",
      notes = "returns the tree of a database or a full IMPEx registry", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown provider"), 
    new ApiResponse(code = 400, message = "not implemented")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config", 
        // provide default value for swagger-ui stability
        defaultValue = "spase://IMPEX/Repository/SINP",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def registry(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml") =  PortalAction.async { implicit request => 
    val future = RegistryService.getTree(request.req.get("id"))
    future.map { (_, fmt.toLowerCase) match {
       case (Left(spase), "json") => Ok(Json.toJson(spase))
       case (Left(spase), _) => 
         Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
       case (Right(error), _) => BadRequest(Json.toJson(error))
    }}
  }

  @GET
  @ApiOperation(
      value = "get simulation repositories", 
      nickname = "getSimulationRepository",
      notes = "returns the repository elements of simulation databases", 
      httpMethod = "GET")
  @Path("/simulations")
  def simulations(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml") = PortalAction.async { 
     val future = RegistryService.getRepositoryType(Simulation).mapTo[Spase]
     future.map { spase => 
       fmt.toLowerCase match {
         case "json" => Ok(Json.toJson(spase))
         case _ => 
           Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
       }
     }
  }

  @GET
  @ApiOperation(
      value = "get observation repositories", 
      nickname = "getObservationRepository",
      notes = "returns the repository elements of observation databases", 
      httpMethod = "GET")
  @Path("/observations")
  def observations(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml") = PortalAction.async {
     val future = RegistryService.getRepositoryType(Observation).mapTo[Spase]
     future.map { spase => 
       fmt.toLowerCase match {
         case "json" => Ok(Json.toJson(spase))
         case _ => 
           Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
       }
     } 
  }

  @GET
  @ApiOperation(
      value = "get repositories", 
      nickname = "getRepository",
      notes = "returns the repository elements of all databases", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown element")))
  @Path("/repository")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config or repository id from a tree", 
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def repository(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml") = PortalAction.async { implicit request =>
    val future = RegistryService.getRepository(request.req.get("id"))
    future.map { (_, fmt.toLowerCase) match {
      case (Left(spase), "json") => Ok(Json.toJson(spase))
      case (Left(spase), _) => 
        Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
      case (Right(error), _) => BadRequest(Json.toJson(error))
    }}
  }

  @GET
  @ApiOperation(
      value = "get simulation models", 
      nickname = "getSimulationModel",
      notes = "returns the simulation model elements of simulation databases", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown element")))
  @Path("/simulationmodel")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config, repository id or simulation model id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "spase://IMPEX/Repository/FMI/GUMICS",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def simulationmodel(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = PortalAction.async { implicit request =>
    val future = RegistryService.getSimulationModel(request.req.get("id"), r)
    future.map { (_, fmt.toLowerCase) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }

  @GET
  @ApiOperation(
      value = "get simulation runs", 
      nickname = "getSimulationRun",
      notes = "returns the simulation run elements of simulation databases", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown element")))
  @Path("/simulationrun")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config, model id or simulation run id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "spase://IMPEX/Repository/FMI/HYB",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def simulationrun(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = PortalAction.async { implicit request =>
    val future = RegistryService.getSimulationRun(request.req.get("id"), r)
    future.map { (_, fmt) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }

  @GET
  @ApiOperation(
      value = "get numerical outputs", 
      nickname = "getNumericalOutput",
      notes = "returns the numerical output elements of simulation databases", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown element")))
  @Path("/numericaloutput")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config, run id or numerical output id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "spase://IMPEX/Repository/SINP",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def numericaloutput(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = PortalAction.async { implicit request =>
    val future = RegistryService.getNumericalOutput(request.req.get("id"), r)
    future.map { (_, fmt.toLowerCase) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }

  @GET
  @ApiOperation(
      value = "get granules", 
      nickname = "getGranule",
      notes = "returns the granule elements of simulation databases", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown element")))
  @Path("/granule")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config, output id or granule id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "spase://IMPEX/Repository/LATMOS",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def granule(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = PortalAction.async { implicit request =>
    val future = RegistryService.getGranule(request.req.get("id"), r)
    future.map { (_, fmt.toLowerCase) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  @GET
  @ApiOperation(
      value = "get observatories", 
      nickname = "getObservatory",
      notes = "returns the observatory elements of observation databases", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown element")))
  @Path("/observatory")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config, repository id or observatory id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "spase://IMPEX/Repository/AMDA",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def observatory(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = PortalAction.async { implicit request => 
    val future = RegistryService.getObservatory(request.req.get("id"), r)
    future.map { (_, fmt.toLowerCase) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }  
  }
  
  @GET
  @ApiOperation(
      value = "get instruments", 
      nickname = "getInstrument",
      notes = "returns the instrument elements of observation databases", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown element")))
  @Path("/instrument")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config, observatory id or instrument id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "spase://IMPEX/Repository/AMDA",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def instrument(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = PortalAction.async { implicit request => 
    val future = RegistryService.getInstrument(request.req.get("id"), r)
    future.map { (_, fmt.toLowerCase) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }  
  }
  
  @GET
  @ApiOperation(
      value = "get numerical data", 
      nickname = "getNumericalData",
      notes = "returns the numerical data elements of observation databases", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown element")))
  @Path("/instrument")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config, instrument id or numerical data id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "spase://IMPEX/Repository/AMDA",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def numericaldata(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = PortalAction.async { implicit request => 
    val future = RegistryService.getNumericalData(request.req.get("id"), r)
    future.map { (_, fmt.toLowerCase) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }  
  }
  
}