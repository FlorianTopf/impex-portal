package controllers

import models.actor._
import models.binding._
import models.enums._
import views.html._
import play.api.mvc._
import play.libs.Akka
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import scala.xml._
import akka.actor._
import akka.pattern.ask
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil
import javax.ws.rs._
import javax.ws.rs.core.MediaType._


// @TODO improve all namespaces of returned XML files
@Api(
    value = "/registry", 
    description = "operations for using the IMPEx registry services")
@Path( "/registry" )
@Produces(Array(APPLICATION_XML, APPLICATION_JSON))
object Registry extends Controller {

  @GET
  @ApiOperation(
      value = "get registry", 
      nickname = "getRegistry",
      notes = "returns the tree of a database or a full IMPEx registry", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 501, message = "unkown provider"), 
    new ApiResponse(code = 502, message = "not implemented")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config", 
        // provide default value for swagger-ui stability
        defaultValue = "impex://SINP",
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
      response = classOf[JsObject], 
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
      response = classOf[JsObject], 
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
      response = classOf[JsObject], 
      httpMethod = "GET")
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
      notes = "returns the simulation model elements of all databases", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @Path("/simulationmodel")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config or simulation model id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "impex://FMI/",
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
    val future = RegistryService.getSimulationModel(request.req.get("id"), r.toBoolean)
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
      notes = "returns the simulation run elements of all databases", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @Path("/simulationrun")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config or simulation run id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "impex://LATMOS",
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
    val future = RegistryService.getSimulationRun(request.req.get("id"), r.toBoolean)
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
      notes = "returns the numerical output elements of all databases", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @Path("/numericaloutput")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config or numerical output id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "impex://SINP",
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
    val future = RegistryService.getNumericalOutput(request.req.get("id"), r.toBoolean)
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
      notes = "returns the granule elements of all databases", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @Path("/granule")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config or granule id from a tree", 
        // provide default value for swagger-ui stability
        defaultValue = "impex://LATMOS",
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
    val future = RegistryService.getGranule(request.req.get("id"), r.toBoolean)
    future.map { (_, fmt.toLowerCase) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  // @TODO really return in json and recursive
  def observatory(fmt: String = "xml", r: String = "false") = PortalAction.async { implicit request => 
    val future = RegistryService.getObservatory(request.req.get("id"), false)
    future map { _ match {
        case Left(spase) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }  
  }
  
  // @TODO really return in json and recursive
  def instrument(fmt: String = "xml", r: String = "false") = PortalAction.async { implicit request => 
    val future = RegistryService.getInstrument(request.req.get("id"), false)
    future map { _ match {
        case Left(spase) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }  
  }
  
  // @TODO really return in json and recursive
  def numericaldata(fmt: String = "xml", r: String = "false") = PortalAction.async { implicit request => 
    val future = RegistryService.getNumericalData(request.req.get("id"), false)
    future map { _ match {
        case Left(spase) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }  
  }
  
}