package controllers

import models.binding._
import models.provider._
import models.enums._
import models.binding.VOTableURL._
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
    description = "operations for using the IMPEx data access services")
@Path("/methods/FMI")
@Produces(Array(APPLICATION_XML, APPLICATION_JSON))
object FMIMethods extends MethodsController {
  val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}

  def isAlive() = PortalAction {
    try {
	  val future = fmi.service.isAlive() 
	  future.fold((fault) => Ok(JsBoolean(false)),(alive) => Ok(JsBoolean(alive)))
    } catch {
      // happens if there is any error calling the WS
      case e: ExecutionException => {
        Logger.error("\n timeout: isAlive at SINP \n error: "+e)
        Ok(JsBoolean(false))
      }
    }
  }
  
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
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/mars/spiral_angle_runset_20130607_mars_20deg/Mag",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://impex-fp7.fmi.fi/ws_tests/input/getDataPointValue_input.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "variable",
        value = "Parameter Keys",
        defaultValue = "Bx,By,Bz,Btot",
        required = false,
        dataType = "list(string)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "Interpolation Method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
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
    val url = request.req.get("votable_url").get
    // opt params
    val variable = request.req.get("variable")
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getDataPointValueFMI(
        interpolation, // interpolation method
        filetype // output filetype
    )
    
    val result = fmi.service.getDataPointValue(
        id, // resourceId
        validateOptStringSeq(variable), // variable 
        new URI(url), // votable_url
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result, request.req)
  	} catch {
  	  case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // this is because there are errors not encoded as SOAP-FAULT
      //case e: RuntimeException => 
      //  NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
      //          "unknown external web service error", request.req)))
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
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/GUMICS/earth/synth_stationary/solarmin/EARTH___n_T_Vx_Bx_By_Bz__7_100_600_3p_03_15m/tilt15p/H+_mstate",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "spacecraft_name",
        value = "Spacecraft Name",
        defaultValue = "CLUSTER1",
        allowableValues = "MEX,MGS,VEX,MAVEN,MESSENGER,CLUSTER1,CLUSTER2,CLUSTER3,CLUSTER4,IMP-8,GEOTAIL,POLAR",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "Start Time",
        defaultValue = "2010-08-02T00:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "Stop Time",
        defaultValue = "2010-08-02T01:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "sampling",
        value = "Sampling",
        defaultValue = "PT60S",
        required = true,
        dataType = "duration",
        paramType = "query"),
    new ApiImplicitParam(
        name = "variable",
        value = "Parameter Keys",
        defaultValue = "Density,Temperature,Pressure,Ux,Uy,Uz",
        required = false,
        dataType = "list(string)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "Interpolation Method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
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
    val scName = request.req.get("spacecraft_name").get
    val startTime = request.req.get("start_time").get
    val stopTime = request.req.get("stop_time").get
    val sampling = request.req.get("sampling").get
    // opt params
    val variable = request.req.get("variable")
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getDataPointValueFMI(
        interpolation, // interpolation method
        filetype // output filetype
    )
           
    val result = fmi.service.getDataPointValueSpacecraft(
        id, // resourceId
        validateOptStringSeq(variable), // variable
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
      // this is because there are errors not encoded as SOAP-FAULT
      //case e: RuntimeException =>
      //  NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
      //          "unknown external web service error", request.req)))
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
        //value = "resource id", 
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/mars/Mars_testrun_lowres/O+_ave_hybstate",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "plane_point", 
        value = "Plane Point", 
        defaultValue = "1.0,0.0,0.0",
        required = true, 
        dataType = "list(float)", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "plane_normal_vector", 
        value = "Plane Normal Vector", 
        defaultValue = "3.7e6,0.0,0.0",
        required = true, 
        dataType = "list(float)", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "variable",
        value = "Parameter Keys",
        defaultValue = "Density,Temperature,Pressure,Ux,Uy,Uz",
        required = false,
        dataType = "list(string)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "resolution",
        value = "Resolution",
        required = false,
        dataType = "double",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "Interpolation Method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
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
    val planePoint = request.req.get("plane_point").get
    val planeVector = request.req.get("plane_normal_vector").get
    // opt params
    val variable = request.req.get("variable")
    // extra params
    val resolution = request.req.get("resolution")
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getSurfaceFMI(
        resolution, // resolution
        filetype, // output filetype
        interpolation // interpolation method
    )
           
    val result = fmi.service.getSurface(
        id, // resoureId
        validateOptStringSeq(variable), // variable
        planePoint, // plane point
        planeVector, // plane normal vector
        Some(extraParams) // extra params
    ) 
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // this is because there are errors not encoded as SOAP-FAULT
      //case e: RuntimeException =>
      //  NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
      //          "unknown external web service error", request.req)))
    }
  }
  
  
  // @FIXME the request data type is not displayed correctly 
  // (wait for new swagger release)
  @POST
  @ApiOperation(
      value = "getVOTableURL at FMI", 
      nickname = "getVOTableURL",
      notes = "returns an URL to a VOTable XML file based on request JSON object", 
      //response = classOf[models.binding.VOTableURL],
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
        defaultValue = "Earth",
        allowableValues = "Earth,Venus,Mars,Comet",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "run_count", 
        value = "Run Count", 
        defaultValue = "2",
        required = false, 
        dataType = "integer", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_density_value", 
        value = "SW Density [1/m^3]", 
        defaultValue = "5e6",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_density_weight", 
        value = "SW Density Weight", 
        defaultValue = "2",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_density_scale", 
        value = "SW Density Scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_density_fun", 
        value = "SW Density Function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_temp_value", 
        value = "SW Temperature [K]", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_temp_weight", 
        value = "SW Temperature Weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_temp_scale", 
        value = "SW Temperature Scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_temp_fun", 
        value = "SW Temperature Function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_utot_value", 
        value = "SW Total Velocity [m/s]", 
        defaultValue = "4.5e5",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_utot_weight", 
        value = "SW Total Velocity Weight", 
        defaultValue = "1",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_utot_scale", 
        value = "SW Total Velocity Scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_utot_fun", 
        value = "SW Total Velocity Function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bx_value", 
        value = "SW Bx [T]", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bx_weight", 
        value = "SW Bx Weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bx_scale", 
        value = "SW Bx Scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"), 
    new ApiImplicitParam(
        name = "sw_bx_fun", 
        value = "SW Bx Function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_by_value", 
        value = "SW By [T]", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_by_weight", 
        value = "SW By Weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_by_scale", 
        value = "SW By Scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_by_fun", 
        value = "SW By Function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bz_value", 
        value = "SW Bz [T]", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bz_weight", 
        value = "SW Bz Weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bz_scale", 
        value = "SW Bz Scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_bz_fun", 
        value = "SW Bz Function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_btot_value", 
        value = "SW Total B [T]", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_btot_weight", 
        value = "SW Total B Weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_btot_scale", 
        value = "SW Total B Scale", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_btot_fun", 
        value = "SW Btot Function", 
        defaultValue = "",
        required = false, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_fun_value", 
        value = "SW Function", 
        defaultValue = "0.5",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_fun_weight", 
        value = "SW Function Weight", 
        defaultValue = "",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_fun_scale", 
        value = "SW Function Scale", 
        defaultValue = "1",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_fun_fun", 
        value = "SW Function Function", 
        defaultValue = "abs(SW_Bx/SW_Btot)",
        required = false, 
        dataType = "string", 
        paramType = "query")))
  @Path("/getMostRelevantRun")
  def getMostRelevantRun = PortalAction { implicit request => 
  	try {
    // mandatory parameter
    val region = request.req.get("object").get
    // optional parameters
    val runCount = request.req.get("run_count")
    // sw parameters
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
        None, // solar F10.7 (@TODO to be added, SFU (Solar Flux Unit)
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
      // this is because there are errors not encoded as SOAP-FAULT
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
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/mars/spiral_angle_runset_20130607_mars_90deg/Mag",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://impex-fp7.fmi.fi/ws_tests/input/getFieldLine_input.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "variable",
        value = "Parameter Keys",
        defaultValue = "Bx,By,Bz,Btot",
        required = false,
        dataType = "list(string)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "step_size",
        value = "Step Size",
        required = false, 
        dataType = "double",
        paramType = "query"),
    new ApiImplicitParam(
        name = "max_steps",
        value = "Max Steps",
        defaultValue = "100",
        required = false,
        dataType = "integer",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_cond_radius",
    	value = "Stop Condition Radius",
    	defaultValue = "0.0",
    	required = false,
    	dataType = "double",
    	paramType = "query"),
    new ApiImplicitParam(
        name = "stop_cond_region",
        value = "Stop Condition Region",
        required = false,
        dataType = "list(float)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "direction",
        value = "Direction",
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
    val url = request.req.get("votable_url").get
    // opt params
    val variable = request.req.get("variable")
    // extra params
    val direction = request.req.get("direction").getOrElse("")
    val filetype = request.req.get("output_filetype").getOrElse("")
    val stepSize = request.req.get("step_size")
    val maxSteps = request.req.get("max_steps")
    val stopCondRadius = request.req.get("stop_cond_radius")
    val stopCondRegion = request.req.get("stop_cond_region")
    
    val extraParams = ExtraParams_getFieldLineFMI(
        direction, // direction
        stepSize, // step size
        maxSteps,//Some(BigInt(100)), // max steps 
        stopCondRadius,//Some(0.0), // stop condition radius 
        stopCondRegion, // stop condition region 
        filetype // output filetype
    )
          
    val result = fmi.service.getFieldLine(
        id, // resourceId
        validateOptStringSeq(variable), // variable
        new URI(url), // votable_url
        Some(extraParams) // extra params
    )
           
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // this is because there are errors not encoded as SOAP-FAULT
      //case e: RuntimeException =>
      //  NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
      //          "unknown external web service error", request.req)))
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
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/mars/spiral_angle_runset_20130607_mars_90deg/Mag",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://impex-fp7.fmi.fi/ws_tests/input/getParticleTrajectory_input.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "step_size",
        value = "Step Size",
        required = false, 
        dataType = "double",
        paramType = "query"),
    new ApiImplicitParam(
        name = "max_steps",
        value = "Max Steps",
        defaultValue = "200",
        required = false,
        dataType = "integer",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_cond_radius",
    	value = "Stop Condition Radius",
    	defaultValue = "0.0",
    	required = false,
    	dataType = "double",
    	paramType = "query"),
    new ApiImplicitParam(
        name = "stop_cond_region",
        value = "Stop Condition Region",
        required = false,
        dataType = "list(float)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "Interpolation Method",
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
    val url = request.req.get("votable_url").get
    // extra params
    val direction = request.req.get("direction").getOrElse("Both")
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    val stepSize = request.req.get("step_size")
    val maxSteps = request.req.get("max_steps")
    val stopCondRadius = request.req.get("stop_cond_radius")
    val stopCondRegion = request.req.get("stop_cond_region")
    
    val extraParams = ExtraParams_getParticleTrajectory(
        direction, // direction
        stepSize,//Some(1.0), // step size
        maxSteps,//Some(BigInt(200)), // max steps
        stopCondRadius,//Some(0.0), // stop condition radius
        stopCondRegion, // stop condition region
        interpolation, // interpolation method
        filetype // output filetype
    )
          
    val result = fmi.service.getParticleTrajectory(
        id, // resourceId
        new URI(url), // votable_url
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // this is because there are errors not encoded as SOAP-FAULT
      //case e: RuntimeException =>
      //  NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
      //          "unknown external web service error", request.req)))
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
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/venus/run01_venus_nominal_spectra_20140417/H+_spectra",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://impex-fp7.fmi.fi/ws_tests/input/getDataPointSpectra_input.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "energy_channel",
        value = "Energy Channel",
        defaultValue = "EnergySpectra",
        required = false,
        dataType = "list(string)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "Interpolation Method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/getDataPointSpectra")
  def getDataPointSpectra = PortalAction { implicit request => 
    try {
    // mandatory
    val id = request.req.get("id").get
    val url = request.req.get("votable_url").get
    // opt params
    val energyChannel = request.req.get("energy_channel")
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getDataPointSpectraFMI(
        interpolation, // interpolation method
        filetype, // output filetype
        validateOptStringSeq(energyChannel) // energy channel
    )
           
    val result = fmi.service.getDataPointSpectra(
        id, // resourceId
        new URI(url), // votable_url
        Some(extraParams) // extra params
    ) 
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      // this is because there are errors not encoded as SOAP-FAULT
      //case e: RuntimeException =>
      //  NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
      //          "unknown external web service error", request.req)))
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
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/FMI/HYB/venus/run01_venus_nominal_spectra_20140417/H+_spectra",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "spacecraft_name",
        value = "Spacecraft Name",
        defaultValue = "VEX",
        allowableValues = "MEX,MGS,VEX,MAVEN,MESSENGER,CLUSTER1,CLUSTER2,CLUSTER3,CLUSTER4,IMP-8,GEOTAIL,POLAR",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "Start Time",
        defaultValue = "2010-08-02T06:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "Stop Time",
        defaultValue = "2010-08-02T09:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "sampling",
        value = "Sampling",
        defaultValue = "PT60S",
        required = true,
        dataType = "duration",
        paramType = "query"),
    new ApiImplicitParam(
        name = "energy_channel",
        value = "Energy Channel",
        defaultValue = "EnergySpectra",
        required = false,
        dataType = "list(string)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "interpolation_method",
        value = "Interpolation Method",
        defaultValue = "Linear",
        allowableValues = "Linear,NearestGridPoint",
        required = false,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/getDataPointSpectraSpacecraft")
  def getDataPointSpectraSpacecraft = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val scName = request.req.get("spacecraft_name").get
    val startTime = request.req.get("start_time").get
    val stopTime = request.req.get("stop_time").get
    val sampling = request.req.get("sampling").get
    // opt params
    val energyChannel = request.req.get("energy_channel")
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val interpolation = request.req.get("interpolation_method").getOrElse("")
    
    val extraParams = ExtraParams_getDataPointSpectraFMI(
        interpolation, // interpolation method
        filetype, // output filetype
        validateOptStringSeq(energyChannel) // energy channel
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
      // this is because there are errors not encoded as SOAP-FAULT
      //case e: RuntimeException =>
      //  NotImplemented(Json.toJson(ServiceResponse(EServiceResponse.NOT_IMPLEMENTED, 
      //          "unknown external web service error", request.req)))
    }
  }
  

}