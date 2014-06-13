package controllers

import play.api.mvc._
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil
import javax.ws.rs._
import javax.ws.rs.core.MediaType._
import scalaxb._
import models.binding._
import java.net.URI
import play.api.libs.ws._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout
import models.provider._
import models.enums._
import play.api.libs.json._
import soapenvelope11._
import java.text.ParseException

// @TODO include request parameters to responses (serialise rew map)

@Api(
    value = "/methods", 
    description = "operations for using the IMPEx data acess services")
@Path("/methods/FMI")
object FMIMethods extends Controller {
  import controllers.Helpers._
  
  val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
  
  @GET
  @ApiOperation(
      value = "getDataPointValue at FMI", 
      nickname = "getDataPointValueFMI",
      notes = "returns interpolated simulation parameter of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "resource id", 
        defaultValue = "impex://FMI/NumericalOutput/HYB/mars/spiral_angle_runset_20130607_mars_20deg/Mag",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "url_xyz",
        value = "votable url",
        defaultValue = "http://impex-fp7.fmi.fi/ws_tests/input/getDataPointValue_input.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "interpolation method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/getDataPointValue")
  def getDataPointValue = PortalAction { implicit request =>
  	try {
    // mandatory parameters
    val id = request.req.get("id").get
    val url = request.req.get("url_xyz").get
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getDataPointValueFMI(
        validateInterpolation(interpolation), // interpolation method (@TODO to be added)
        validateFiletype(filetype) // output filetype
    )
    
    val result = fmi.service.getDataPointValue(
        id, // resourceId
        None, // variable (@TODO to be added)
        new URI(url), // url_xyz
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result, request.req)
  	} catch {
  	  case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // @FIXME this is because there are errors not encoded as SOAP-FAULT
      case e: RuntimeException =>
        NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
                "unknown external web service error", request.req)))
  	}
  }
  
  @GET
  @ApiOperation(
      value = "getDataPointValueSpacecraft at FMI", 
      nickname = "getDataPointValueSpacecraftFMI",
      notes = "returns interpolated simulation parameters along a given spacecraft trajectory", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "resource id", 
        defaultValue = "impex://FMI/NumericalOutput/GUMICS/earth/synth_stationary/solarmin/EARTH___n_T_Vx_Bx_By_Bz__7_100_600_3p_03_15m/tilt15p/H+_mstate",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sc_name",
        value = "spacecraft name",
        defaultValue = "CLUSTER1",
        // @TODO add all possible values here
        allowableValues = "CLUSTER1,CLUSTER2,CLUSTER3,CLUSTER4",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "start time",
        defaultValue = "2010-08-02T00:00:00",
        required = true,
        dataType = "ISO 8601 datetime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "stop time",
        defaultValue = "2010-08-02T01:00:00",
        required = true,
        dataType = "ISO 8601 datetime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "sampling",
        value = "sampling",
        defaultValue = "PT60S",
        required = true,
        dataType = "ISO 8601 duration",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "interpolation method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/getDataPointValueSpacecraft")
  def getDataPointValueSpacecraft = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val scName = request.req.get("sc_name").get
    val startTime = request.req.get("start_time").get
    val stopTime = request.req.get("stop_time").get
    val sampling = request.req.get("sampling").get
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getDataPointValueFMI(
        validateInterpolation(interpolation), // interpolation method
        validateFiletype(filetype) // output filetype
    )
           
    val result = fmi.service.getDataPointValueSpacecraft(
        id, // resourceId
        None, // variable (@TODO to be added)
        SpacecraftType.fromString(scName, 
            scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")), // spacecraft name
        TimeProvider.getISODate(startTime), // start time
        TimeProvider.getISODate(stopTime), // stop time
        TimeProvider.getDuration(sampling), // sampling
        Some(extraParams) // extra params
    )
               
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:ParseException | _:IllegalArgumentException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "time parameter not in ISO 8601 format", request.req)))
      // @FIXME this is because there are errors not encoded as SOAP-FAULT
      case e: RuntimeException =>
        NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
                "unknown external web service error", request.req)))
    }
  }
  
  @GET
  @ApiOperation(
      value = "getSurface at FMI", 
      nickname = "getSurfaceFMI",
      notes = "returns a surface of interpolated simulation parameters", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "resource id", 
        defaultValue = "impex://FMI/NumericalOutput/HYB/mars/Mars_testrun_lowres/O+_ave_hybstate",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "plane_point", 
        value = "plane point", 
        defaultValue = "1.0,0.0,0.0",
        required = true, 
        dataType = "list(float)", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "plane_normal_vector", 
        value = "plane normal vector", 
        defaultValue = "3.7e6,0.0,0.0",
        required = true, 
        dataType = "list(float)", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "interpolation method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/getSurface")
  def getSurface = PortalAction { implicit request =>
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val plane_point = request.req.get("plane_point").get
    val plane_n_vector = request.req.get("plane_normal_vector").get
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getSurfaceFMI(
        None, // resolution (@TODO TO BE TESTED)
        validateFiletype(filetype), // output filetype
        validateInterpolation(interpolation) // interpolation method (@TODO to be added)
    )
           
    val result = fmi.service.getSurface(
        "impex://FMI/NumericalOutput/HYB/mars/Mars_testrun_lowres/O+_ave_hybstate", // resoureId
        None, // variable (@TODO to be added)
        validateFloatSeq(plane_point), // plane point
        validateFloatSeq(plane_n_vector), // plane normal vector
        Some(extraParams) // extra params
    ) 
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // @FIXME this is because there are errors not encoded as SOAP-FAULT
      case e: RuntimeException =>
        NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
                "unknown external web service error", request.req)))
    }
  }
  
  
  // @FIXME how to include this method to the API?
  @GET
  @ApiOperation(
      value = "getVOTableURL at FMI", 
      nickname = "getVOTableURL",
      notes = "", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @Path("/getVOTableURL")
  def getVOTableURL = ??? 
  
  
  // @FIXME how to include this method to the API?
  @GET
  @ApiOperation(
      value = "getMostRelevantRun at FMI", 
      nickname = "getMostRelevantRun",
      notes = "returns a surface of interpolated simulation parameters", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @Path("/getMostRelevantRun")
  def getMostRelevantRun = ???
  
  
  @GET
  @ApiOperation(
      value = "getFieldLine at FMI", 
      nickname = "getFieldLineFMI",
      notes = "returns magnetic field lines of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "resource id", 
        defaultValue = "impex://FMI/NumericalOutput/HYB/mars/spiral_angle_runset_20130607_mars_90deg/Mag",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "url_xyz",
        value = "votable url",
        defaultValue = "http://impex-fp7.fmi.fi/ws_tests/input/getFieldLine_input.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "direction",
        value = "direction",
        defaultValue = "Both",
        allowableValues = "Both,Forward,Backward",
        required = false,
        dataType = "string",
        paramType = "query")))  
  @Path("/getFieldLine")
  def getFieldLine = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val url = request.req.get("url_xyz").get
    // extra params
    val direction = request.req.get("direction").getOrElse("")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val extraParams = ExtraParams_getFieldLineFMI(
        validateDirection(direction), // direction
        None, // step size (@TODO TO BE TESTED)
        Some(BigInt(100)), // max steps (@TODO to be added)
        Some(0), // stop condition radius (@TODO to be added)
        None, // stop condition region (@TODO TO BE TESTED)
        validateFiletype(filetype) // output filetype
    )
          
    val result = fmi.service.getFieldLine(
        id, // resourceId
        None, // variable (not supported ATM) 
        new URI(url), // url_xyz
        Some(extraParams) // extra params
    )
           
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // @FIXME this is because there are errors not encoded as SOAP-FAULT
      case e: RuntimeException =>
        NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
                "unknown external web service error", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "getParticleTrajectory at FMI", 
      nickname = "getParticleTrajectory",
      notes = "returns the particle trajectory of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "resource id", 
        defaultValue = "impex://FMI/NumericalOutput/HYB/mars/spiral_angle_runset_20130607_mars_90deg/Mag",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "url_xyz",
        value = "votable url",
        defaultValue = "http://impex-fp7.fmi.fi/ws_tests/input/getParticleTrajectory_input.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "interpolation method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "direction",
        value = "direction",
        defaultValue = "Both",
        allowableValues = "Both,Forward,Backward",
        required = false,
        dataType = "string",
        paramType = "query")))  
  @Path("/getParticleTrajectory")
  def getParticleTrajectory = PortalAction { implicit request =>
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val url = request.req.get("url_xyz").get
    // extra params
    val direction = request.req.get("direction").getOrElse("Both")
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getParticleTrajectory(
        validateDirection(direction), // direction
        Some(1), // step size (@TODO to be added)
        Some(BigInt(200)), // max steps (@TODO to be added)
        Some(0), // stop condition radius (@TODO to be added)
        None, // stop condition region (@TODO TO BE TESTED)
        validateInterpolation(interpolation), // interpolation method (@TODO TO BE TESTED)
        validateFiletype(filetype) // output filetype
    )
          
    val result = fmi.service.getParticleTrajectory(
        id, // resourceId
        new URI(url), // url_xyz
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // @FIXME this is because there are errors not encoded as SOAP-FAULT
      case e: RuntimeException =>
        NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
                "unknown external web service error", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "getDataPointSpectra at FMI", 
      nickname = "getDataPointSpectraFMI",
      notes = "returns spectra of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "resource id", 
        defaultValue = "impex://FMI/NumericalOutput/HYB/venus/run01_venus_nominal_spectra_20140417/H+_spectra",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "url_xyz",
        value = "votable url",
        defaultValue = "http://impex-fp7.fmi.fi/ws_tests/input/getDataPointSpectra_input.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "interpolation method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/getDataPointsSpectra")
  def getDataPointSpectra = PortalAction { implicit request => 
    try {
    // mandatory
    val id = request.req.get("id").get
    val url = request.req.get("url_xyz").get
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getDataPointSpectraFMI(
        validateInterpolation(interpolation), // interpolation method (@TODO TO BE TESTED)
        validateFiletype(filetype), // output filetype
        None // energy channel (@TODO TO BE TESTED)
    )
           
    val result = fmi.service.getDataPointSpectra(
        id, // resourceId
        new URI(url), // url_xyz
        Some(extraParams) // extra params
    ) 
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // @FIXME this is because there are errors not encoded as SOAP-FAULT
      case e: RuntimeException =>
        NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
                "unknown external web service error", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "getDataPointSpectraSpacecraft at FMI", 
      nickname = "getDataPointSpectraSpacecraftFMI",
      notes = "returns spectra along a given spacecraft trajectory", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "resource id", 
        defaultValue = "impex://FMI/NumericalOutput/HYB/venus/run01_venus_nominal_spectra_20140417/H+_spectra",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sc_name",
        value = "spacecraft name",
        defaultValue = "VEX",
        // @TODO add all possible values here
        allowableValues = "MEX,MGS,VEX",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "start time",
        defaultValue = "2010-08-02T06:00:00",
        required = true,
        dataType = "ISO 8601 datetime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "stop time",
        defaultValue = "2010-08-02T09:00:00",
        required = true,
        dataType = "ISO 8601 datetime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "sampling",
        value = "sampling",
        defaultValue = "PT60S",
        required = true,
        dataType = "ISO 8601 duration",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "interpolation method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/getDataPointsSpectraSpacecraft")
  def getDataPointSpectraSpacecraft = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val scName = request.req.get("sc_name").get
    val startTime = request.req.get("start_time").get
    val stopTime = request.req.get("stop_time").get
    val sampling = request.req.get("sampling").get
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getDataPointSpectraFMI(
        validateInterpolation(interpolation), // interpolation method (@TODO TO BE TESTED)
        validateFiletype(filetype), // output filetype
        None // energy channel (@TODO TO BE TESTED)
    )
           
    val result = fmi.service.getDataPointSpectraSpacecraft(
        id, // resourceId
        SpacecraftType.fromString(scName, 
            scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")), // spacecraft name
        TimeProvider.getISODate(startTime), // start time
        TimeProvider.getISODate(stopTime), // stop time
        TimeProvider.getDuration(sampling), // sampling
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:ParseException | _:IllegalArgumentException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "time parameter not in ISO 8601 format", request.req)))
      case e: MatchError => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "unkown spacecraft name", request.req)))
      // @FIXME this is because there are errors not encoded as SOAP-FAULT
      case e: RuntimeException =>
        NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
                "unknown external web service error", request.req)))
    }
  }
  

}