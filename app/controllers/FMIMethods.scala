package controllers

import play.api.mvc._
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil
import javax.ws.rs._
import javax.ws.rs.core.MediaType._
import scalaxb._
import models.binding._
import models.provider._
import models.enums._
import java.net.URI
import play.api.libs.json._
import java.text.ParseException
import soapenvelope11._
import models.binding.VOTableURL._


@Api(
    value = "/methods", 
    description = "operations for using the IMPEx data acess services")
@Path("/methods/FMI")
@Produces(Array(APPLICATION_XML, APPLICATION_JSON))
object FMIMethods extends MethodsController {
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
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/mars/spiral_angle_runset_20130607_mars_20deg/Mag",
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
        validateInterpolation(interpolation), // interpolation method
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
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/GUMICS/earth/synth_stationary/solarmin/EARTH___n_T_Vx_Bx_By_Bz__7_100_600_3p_03_15m/tilt15p/H+_mstate",
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
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/mars/Mars_testrun_lowres/O+_ave_hybstate",
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
        validateInterpolation(interpolation) // interpolation method
    )
           
    val result = fmi.service.getSurface(
        id, // resoureId
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
  
  
  // @FIXME the request data type is not displayed correctly 
  // (maybe remove it from the API view, or wait for new swagger release)
  @POST
  @ApiOperation(
      value = "getVOTableURL at FMI", 
      nickname = "getVOTableURL",
      notes = "returns an URL to a VOTable XML file based on request JSON object", 
      httpMethod = "POST")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
 @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "body",
        value = "VOTableURL object encoding the VOTable fields to be created", 
        required = true, 
        dataType = "VOTableURL", 
        paramType = "body")))
  @Path("/getVOTableURL")
  def getVOTableURL = PortalAction { implicit request => 
    try {
    request.body.asJson.map {
      json => {
        val voTable = json.as[VOTableURL]
        val result = fmi.service.getVOTableURL(voTable.Table_name, voTable.Description, voTable.Fields)
           
        result.fold(
            fault => BadRequest(Json.toJson(
              VOTableURLResponse(
                  EServiceResponse.BAD_REQUEST, 
                  fault.original.asInstanceOf[Fault].faultstring, json))), 
            uri => Ok(Json.toJson(VOTableURLResponse(EServiceResponse.OK, uri.toString, json)))
        )
        
      }}.getOrElse(BadRequest(Json.toJson(VOTableURLResponse(EServiceResponse.BAD_REQUEST,
          "mandatory parameter missing", Json.obj()))))
    } catch {
      case e: JsResultException => BadRequest(Json.toJson(VOTableURLResponse(EServiceResponse.BAD_REQUEST,
          "input object malformed", request.body.asJson.get)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "getMostRelevantRun at FMI", 
      nickname = "getMostRelevantRun",
      notes = "returns a surface of interpolated simulation parameters", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "object", 
        value = "Object", 
        // @TODO at the moment only earth is existing
        // we need to add a dropdown later
        defaultValue = "Earth", 
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "run_count", 
        value = "Run count", 
        defaultValue = "2",
        required = false, 
        dataType = "integer", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_density_value", 
        value = "SW density value", 
        defaultValue = "5e6",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_density_weight", 
        value = "SW density weight", 
        defaultValue = "2",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_density_scale", 
        value = "SW density scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_density_fun", 
        value = "SW density function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_temp_value", 
        value = "SW temperature value", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_temp_weight", 
        value = "SW temperature weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_temp_scale", 
        value = "SW temperature scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_temp_fun", 
        value = "SW temperature function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_utot_value", 
        value = "SW total velocity value", 
        defaultValue = "4.5e5",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_utot_weight", 
        value = "SW total velocity weight", 
        defaultValue = "1",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_utot_scale", 
        value = "SW total velocity scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_utot_fun", 
        value = "SW total velocity function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bx_value", 
        value = "SW Bx value", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bx_weight", 
        value = "SW Bx weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bx_scale", 
        value = "SW Bx scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"), 
    new ApiImplicitParam(
        name = "sw_bx_fun", 
        value = "SW Bx function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_by_value", 
        value = "SW By value", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_by_weight", 
        value = "SW By weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_by_scale", 
        value = "SW By scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_by_fun", 
        value = "SW By function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bz_value", 
        value = "SW Bz value", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bz_weight", 
        value = "SW Bz weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bz_scale", 
        value = "SW Bz scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bz_fun", 
        value = "SW Bz function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_btot_value", 
        value = "SW total B value", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_btot_weight", 
        value = "SW total B weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_btot_scale", 
        value = "SW total B scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_btot_fun", 
        value = "SW Btot function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_fun_value", 
        value = "SW function value", 
        defaultValue = "0.5",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_fun_weight", 
        value = "SW function weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_fun_scale", 
        value = "SW function scale", 
        defaultValue = "1",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_fun_fun", 
        value = "SW function function", 
        defaultValue = "abs(SW_Bx/SW_Btot)",
        required = false, 
        dataType = "string", 
        paramType = "query")
  ))
  @Path("/getMostRelevantRun")
  def getMostRelevantRun = PortalAction { implicit request => 
  	try {
    // mandatory parameter
    val region = request.req.get("object").get
    // optional parameters
    val runCount = request.req.get("run_count")
    // sw parameters
  	// note: these are also mandatory but we send an empty list
  	// if the user enters no SW parameters => 
  	// @TODO service returns malformed JSON then
    val swDensityParam = validateSWParams("sw_density", request.req)
    val swTempParam = validateSWParams("sw_temp", request.req)
    val swUtotParam = validateSWParams("sw_utot", request.req)
    val swBxParam = validateSWParams("sw_bx", request.req)
    val swByParam = validateSWParams("sw_by", request.req)
    val swBzParam = validateSWParams("sw_bz", request.req)
    val swBtotParam = validateSWParams("sw_btot", request.req)
    val swFunParam = validateSWParams("sw_fun", request.req) 
  	val swParameters = SW_parameter_list(
  	    swDensityParam, // sw density
        swUtotParam, // sw Utot 
        swTempParam, // sw temperature
        swUtotParam, // sw Btot
        swBxParam, // sw Bx
        swByParam, //  sw By
        swBzParam, // sw Bz
        None, // solar F10.7 (@TODO to be added)
        swFunParam // sw function
    )
           
    val result = fmi.service.getMostRelevantRun(
        EnumRegion.fromString(region, 
            scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")), // object value (enumRegion)
        runCount.map(BigInt(_)), // run count
        swParameters // sw parameters
    ) 
    
    result.fold(
        fault => BadRequest(Json.toJson(
            ServiceResponse(
                EServiceResponse.BAD_REQUEST, 
                fault.original.asInstanceOf[Fault].faultstring, request.req))), 
        json => { 
          // only extracting the runs 
          val result = Json.obj("runs" -> Json.parse(json).\("runs"))
          Ok(Json.toJson(ServiceResponseJson(EServiceResponse.OK, result, request.req))) 
        }
    )
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e: MatchError => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "unkown object name", request.req)))
      case e @ (_:NumberFormatException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "illegal number provided", request.req)))
      // @FIXME this is because there are errors not encoded as SOAP-FAULT
      //case e: RuntimeException =>
      //  NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
      //          "unknown external web service error", request.req)))
    }
  }
  
  
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
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/mars/spiral_angle_runset_20130607_mars_90deg/Mag",
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
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/mars/spiral_angle_runset_20130607_mars_90deg/Mag",
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
        validateInterpolation(interpolation), // interpolation method
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
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/venus/run01_venus_nominal_spectra_20140417/H+_spectra",
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
        validateInterpolation(interpolation), // interpolation method
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
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/venus/run01_venus_nominal_spectra_20140417/H+_spectra",
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
        validateInterpolation(interpolation), // interpolation method
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