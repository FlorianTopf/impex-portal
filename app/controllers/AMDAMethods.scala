package controllers

import models.binding._
import models.provider._
import models.enums._
import play.api.mvc._
import play.api.libs.json._
import play.api.Logger
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil
import javax.ws.rs._
import javax.ws.rs.core.MediaType._
import scalaxb._
import soapenvelope11._
import java.net.URI
import java.text.ParseException
import java.util.concurrent.ExecutionException


@Api(
    value = "/methods", 
    description = "operations for using the IMPEx data acess services")
@Path("/methods/AMDA")
@Produces(Array(APPLICATION_XML, APPLICATION_JSON))
object AMDAMethods extends MethodsController {
  
  val amda = new Methods_AMDASoapBindings with Soap11Clients with DispatchHttpClients {}
  
  def isAlive() = PortalAction {
    try {
	  val future = amda.service.isAlive() 
	  future.fold((fault) => Ok(JsBoolean(false)),(alive) => Ok(JsBoolean(alive)))
    } catch {
      // happens if there is any error calling the WS
      case e: ExecutionException => {
        Logger.error("\n timeout: isAlive at SINP \n error: "+e)
        Ok(JsBoolean(false))
      }
    }
  }
  
  /*@GET
  @ApiOperation(
      value = "getTimeTableList at AMDA", 
      nickname = "getTimeTableListAMDA",
      notes = "returns a list of time tables owned by a user", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "userId", 
        value = "AMDA User Id", 
        defaultValue = "impex",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "password", 
        value = "AMDA User Password",
        //defaultValue = "impex",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  @Path("/getTimeTableList")*/
  def getTimeTableList = PortalAction { implicit request =>
    // optional
    val userId = request.req.get("userId")
    val password = request.req.get("password")
    
    val result = amda.service.getTimeTablesList(userId, password)
    result.fold(
        fault => BadRequest(Json.toJson(
            ServiceResponse(
                EServiceResponse.BAD_REQUEST, 
                fault.original.asInstanceOf[Fault].faultstring, request.req))), 
        // response must be JSON
        response => { 
          Ok(Json.toJson(ServiceResponseJson(EServiceResponse.OK, Json.toJson(response) , request.req)))
        }
    )
  }
  
  
  /* @GET
  @ApiOperation(
      value = "getTimeTable at AMDA", 
      nickname = "getTimeTableAMDA",
      notes = "returns the contents of a specific time table", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "ttId", 
        value = "Time Table Id", 
        defaultValue = "sharedtt_0",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "userId", 
        value = "AMDA User Id", 
        defaultValue = "impex",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "password", 
        value = "AMDA User Password",
        //defaultValue = "impex",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  @Path("/getTimeTable") */
  def getTimeTable = PortalAction { implicit request =>
    try {
    // mandatory
    val ttId = request.req.get("ttId").get
    // optional
    val userId = request.req.get("userId")
    val password = request.req.get("password")
    
    val result = amda.service.getTimeTable(userId, password, ttId)
    result.fold(
        fault => BadRequest(Json.toJson(
            ServiceResponse(
                EServiceResponse.BAD_REQUEST, 
                fault.original.asInstanceOf[Fault].faultstring, request.req))), 
        // response must be JSON
        response => { 
          Ok(Json.toJson(ServiceResponseJson(EServiceResponse.OK, Json.toJson(response) , request.req)))
        }
    )
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
    }
  }
  

  /* @GET
  @ApiOperation(
      value = "getParameterList at AMDA", 
      nickname = "getParameterListAMDA",
      notes = "returns a list of parameters belonging to a user", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "userId", 
        value = "AMDA User Id", 
        defaultValue = "impex",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "password", 
        value = "AMDA User Password",
        //defaultValue = "impex",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  @Path("/getParameterList") */
  def getParameterList = PortalAction { implicit request =>
    try {
    // mandatory
    val userId = request.req.get("userId").get
    // optional
    val password = request.req.get("password")
    
    val result = amda.service.getParameterList(userId, password)
    result.fold(
        fault => BadRequest(Json.toJson(
            ServiceResponse(
                EServiceResponse.BAD_REQUEST, 
                fault.original.asInstanceOf[Fault].faultstring, request.req))), 
        // response must be JSON
        response => { 
          Ok(Json.toJson(ServiceResponseJson(EServiceResponse.OK, Json.toJson(response) , request.req)))
        }
    )
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "getParameter at AMDA", 
      nickname = "getParameterAMDA",
      notes = "returns data corresponding to a given ParameterKey from the tree", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "startTime",
        value = "Start Time",
        defaultValue = "1996-01-15T00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stopTime",
        value = "Stop Time",
        defaultValue = "1996-01-17T00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "parameterId",
        value = "Parameter Key",
        defaultValue = "b_it",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "sampling",
        value = "Sampling [s]",
        defaultValue = "60",
        required = false,
        dataType = "number",
        paramType = "query"),
    new ApiImplicitParam(
        name = "userId", 
        value = "AMDA User Id", 
        defaultValue = "impex",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "password", 
        value = "AMDA User Password",
        //defaultValue = "impex",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "outputFormat", 
        value = "Output Format",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "timeFormat", 
        value = "Time Format",
        defaultValue = "ISO8601",
        allowableValues = "ISO8601,unixtime",
        required = false, 
        dataType = "string", 
        paramType = "query"),
     new ApiImplicitParam(
        name = "gzip", 
        value = "gzip",
        defaultValue = "false",
        allowableValues = "false,true",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  @Path("/getParameter")
  def getParameter = PortalAction { implicit request =>
    try {
    // mandatory
    val startTime = request.req.get("startTime").get
    val stopTime = request.req.get("stopTime").get
    val parameter = request.req.get("parameterId").get
    // optional
    val sampling = request.req.get("sampling")
    val userId = request.req.get("userId")
    val password = request.req.get("password")
    val outputFormat = request.req.get("outputFormat")
    val timeFormat = request.req.get("timeFormat")
    val gzip = request.req.get("gzip") match {
      case Some(v) if(v == "true") => Some(BigInt(1))
      case Some(v) if(v == "false") => Some(BigInt(0))
      case _ => None
    }
    
    val result = amda.service.getParameter(
        startTime, // starttime
        stopTime, // stoptime
        parameter, // parameter
        sampling, // sampling
        userId, // userId
        password, // password
        outputFormat, // output format
        timeFormat, // time format
        gzip) // gzip
    result.fold(
        fault => BadRequest(Json.toJson(
            ServiceResponse(
                EServiceResponse.BAD_REQUEST, 
                fault.original.asInstanceOf[Fault].faultstring, request.req))), 
        // response must be JSON
        response => { 
          Ok(Json.toJson(ServiceResponseJson(EServiceResponse.OK, Json.toJson(response) , request.req)))
        }
    )
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
    }
  }
  
  @GET
  @ApiOperation(
      value = "getOrbits at AMDA", 
      nickname = "getOrbitsAMDA",
      notes = "returns orbits for a given observatory name from the tree", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "startTime",
        value = "Start Time",
        defaultValue = "2007-03-15T00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stopTime",
        value = "Stop Time",
        defaultValue = "2007-03-16T00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    // possible values from current ICD
    new ApiImplicitParam(
        name = "spacecraft",
        value = "Spacecraft Name",
        defaultValue = "VEX",
        allowableValues = "CASSINI,GALILEO,Voyager_1,Voyager_2,Pioneer_10,Pioneer_11,PVO,ACE,VEX,MEX,MGS,MAVEN,MESSENGER,ULYSSES,STEREO-A,STEREO-B,WIND,THEMIS-A,THEMIS-B,THEMIS-C,THEMIS-D,THEMIS-E,CLUSTER1,CLUSTER2,CLUSTER3,CLUSTER4,DOUBLESTAR1,IMP-8,GEOTAIL,POLAR,INTERBALL-TAIL,ISEE-1,ISEE-2",
        required = true,
        dataType = "string",
        paramType = "query"),
    // possible values from current ICD
    new ApiImplicitParam(
        name = "coordinateSystem",
        value = "Coordinate System",
        defaultValue = "VSO",
        allowableValues = "CPHIO,GPHIO,IPHIO,EPHIO,Equatorial,CGM,Carrington,DM,GEI,GEO,GSE,GSEQ,GSM,HAE,HCC,HCI,HCR,HEE,HEEQ,HG,HGI,HPC,HPR,J2000,LGM,MAG,MFA,RTN,SC,SE,SM,SR,SR2,SSE,SSE_L,SpacecraftOrbitPlane,WGS84,MSO,VSO,JSM,JSE,IRC,SYS3,KSM,KRTP,KSO,MBF,CSEQ,CSE",
        required = true,
        dataType = "string",
        paramType = "query"),
    // possible values from current ICD
    new ApiImplicitParam(
        name = "units",
        value = "Units",
        defaultValue = "Rv",
        allowableValues = "km,Rs,Rj,Rca,Rga,Rio,Reu,Rv,Rm,Re,AU",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "sampling",
        value = "Sampling [s]",
        defaultValue = "60",
        required = false,
        dataType = "number",
        paramType = "query"),
    new ApiImplicitParam(
        name = "userId", 
        value = "AMDA User Id", 
        defaultValue = "impex",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "password", 
        value = "AMDA User Password",
        //defaultValue = "impex",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "outputFormat", 
        value = "Output Format",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "timeFormat", 
        value = "Time Format",
        defaultValue = "ISO8601",
        allowableValues = "ISO8601,unixtime",
        required = false, 
        dataType = "string", 
        paramType = "query"),
     new ApiImplicitParam(
        name = "gzip", 
        value = "gzip",
        defaultValue = "false",
        allowableValues = "false,true",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  @Path("/getParameter")
  def getOrbits = PortalAction { implicit request =>
    try {
    // mandatory
    val startTime = request.req.get("startTime").get
    val stopTime = request.req.get("stopTime").get
    val spacecraft = request.req.get("spacecraft").get
    val coordSystem = request.req.get("coordinateSystem").get
    // optional
    val units = request.req.get("units")
    val sampling = request.req.get("sampling")
    val userId = request.req.get("userId")
    val password = request.req.get("password")
    val outputFormat = request.req.get("outputFormat")
    val timeFormat = request.req.get("timeFormat")
    val gzip = request.req.get("gzip") match {
      case Some(v) if(v == "true") => Some(BigInt(1))
      case Some(v) if(v == "false") => Some(BigInt(0))
      case _ => None
    }
    
    val result = amda.service.getOrbites(
        startTime, // starttime
        stopTime,  // stoptime
        EnumSpacecraft.fromString( // spacecraft
            spacecraft, 
            scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")),
        EnumCoordinateSystemType.fromString( // coordinate system
            coordSystem, 
            scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")), 
        units, // units 
        sampling, // sampling
        userId, // userId
        password, // password
        outputFormat, // output format
        timeFormat, // time format
        gzip) // gzip
    result.fold(
        fault => BadRequest(Json.toJson(
            ServiceResponse(
                EServiceResponse.BAD_REQUEST, 
                fault.original.asInstanceOf[Fault].faultstring, request.req))), 
        // response must be JSON
        response => { 
          Ok(Json.toJson(ServiceResponseJson(EServiceResponse.OK, Json.toJson(response) , request.req)))
        }
    )
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // @TODO improve matching error
      case e: MatchError => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
          "spacecraft or coordinate system unkown", request.req)))
    }
  }
  
}
