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
@Produces(Array(APPLICATION_JSON, APPLICATION_XML))
object Registry extends Controller {

  @GET
  @ApiOperation(
      value = "get registry", 
      notes = "returns the tree of a database or a full IMPEx registry", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 501, message = "unkown provider"), 
    new ApiResponse(code = 502, message = "not implemented")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "database id stored in the config", required = false, dataType = "string", paramType = "query")))
  def registry(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml") = CORS { Action.async { implicit request => 
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getTree(req.get("id"))
    future.map { (_, fmt.toLowerCase) match {
       case (Left(spase), "json") => Ok(Json.toJson(spase))
       case (Left(spase), _) => 
         Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
       case (Right(error), _) => BadRequest(Json.toJson(error))
    }}
  }}

  @GET
  @ApiOperation(
      value = "get simulation repositories", 
      notes = "returns the repository elements of simulation databases", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @Path("/simulations")
  def simulations(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml") = CORS { Action.async { 
     val future = RegistryService.getRepositoryType(Simulation).mapTo[Spase]
     future.map { spase => 
       fmt.toLowerCase match {
         case "json" => Ok(Json.toJson(spase))
         case _ => 
           Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
       }
     }
  }}

  @GET
  @ApiOperation(
      value = "get observation repositories", 
      notes = "returns the repository elements of observation databases", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @Path("/observations")
  def observations(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml") = CORS { Action.async {
     val future = RegistryService.getRepositoryType(Observation).mapTo[Spase]
     future.map { spase => 
       fmt.toLowerCase match {
         case "json" => Ok(Json.toJson(spase))
         case _ => 
           Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
       }
     } 
  }}

  @GET
  @ApiOperation(
      value = "get repositories", 
      notes = "returns the repository elements all databases", 
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
      @DefaultValue("xml") fmt: String = "xml") = CORS { Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getRepository(req.get("id"))
    future.map { (_, fmt.toLowerCase) match {
      case (Left(spase), "json") => Ok(Json.toJson(spase))
      case (Left(spase), _) => 
        Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
      case (Right(error), _) => BadRequest(Json.toJson(error))
    }}
  }}

  @GET
  @ApiOperation(
      value = "get simulation models", 
      notes = "returns the simulation model elements all databases", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @Path("/simulationmodel")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config or simulation model id from a tree", 
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def simulationmodel(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = CORS { Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getSimulationModel(req.get("id"), r)
    future.map { (_, fmt.toLowerCase) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }}

  @GET
  @ApiOperation(
      value = "get simulation runs", 
      notes = "returns the simulation run elements all databases", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @Path("/simulationrun")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config or simulation run id from a tree", 
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def simulationrun(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = CORS { Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getSimulationRun(req.get("id"), r)
    future.map { (_, fmt) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }}

  @GET
  @ApiOperation(
      value = "get numerical outputs", 
      notes = "returns the numerical output elements all databases", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @Path("/numericaloutput")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config or numerical output id from a tree", 
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def numericaloutput(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = CORS { Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getNumericalOutput(req.get("id"), r)
    future.map { (_, fmt.toLowerCase) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }}
  }

  @GET
  @ApiOperation(
      value = "get granules", 
      notes = "returns the granule elements all databases", 
      response = classOf[JsObject], 
      httpMethod = "GET")
  @Path("/granule")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config or granule id from a tree", 
        required = false, 
        dataType = "string", 
        paramType = "query")))
  def granule(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml", 
      @ApiParam(value = "recursive tree including all ancestor elements")
      @QueryParam("r")
      @DefaultValue("false") r: String = "false") = CORS { Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getGranule(req.get("id"), r)
    future.map { (_, fmt.toLowerCase) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }}
  
  // @TODO finalise routes for observations
  // @TODO return in json and recursive
  def observatory(fmt: String = "xml", r: String = "false") = CORS { Action.async { implicit request => 
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getObservatory(req.get("id"))
    future map { _ match {
        case Left(spase) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }  
  }}
  
  def instrument(fmt: String = "xml", r: String = "false") = ???
  
  def numericaldata(fmt: String = "xml", r: String = "false") = ???
  
}