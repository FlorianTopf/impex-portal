package controllers

import models.binding._
import models.provider._
import models.enums._
import play.api.mvc._
import play.api.libs.json._
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil
import javax.ws.rs._
import javax.ws.rs.core.MediaType._
import scalaxb._
import java.net.URI
import java.text.ParseException

// @TODO add default values (and later ranges) which are given in the ICD
@Api(
    value = "/methods", 
    description = "operations for using the IMPEx data access services")
@Path("/methods/SINP")
@Produces(Array(APPLICATION_XML, APPLICATION_JSON))
object SINPMethods extends MethodsController {
  val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
  
  def isAlive() = PortalAction {
    val future = sinp.service.isAlive() 
    future.fold((fault) => Ok(JsBoolean(false)), (alive) => Ok(JsBoolean(alive)) )
  }
  
  @GET
  @ApiOperation(
      value = "getDataPointValue at SINP", 
      nickname = "getDataPointValueSINP",
      notes = "returns interpolated magnetic field values of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/SINP/Earth/2003-11-20UT12",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_52points.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "variable",
        value = "Parameter Keys",
        required = false,
        dataType = "list(string)",
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
    
    val extraParams = ExtraParams_getDataPointValueSINP(
        filetype, // output filetype
        None // interpolation method (only linear)
    )
    
    val result = sinp.service.getDataPointValue(
        id, // resourceId
        validateOptStringSeq(variable), // variable 
        Some(new URI(url)), // votable_url
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
    }
  }
  
  @GET
  @ApiOperation(
      value = "calculateDataPointValueFixedTime at SINP", 
      nickname = "calculateDataPointValueFixedTime",
      notes = "returns interpolated magnetic field values for Earth of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/SINP/Earth/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "Start Time",
        defaultValue = "2012-03-08T14:06:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_b",
        value = "IMF B [nT]",
        required = false,
        dataType = "list(double)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_density", 
        value = "SW Density [1/cm^3]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_velocity", 
        value = "SW Velocity [km/s]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "al", 
        value = "Al [nT]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "dst", 
        value = "Dst [nT]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/calculateDataPointValueFixedTime")
  def calculateDataPointValueFixedTime = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val url = request.req.get("votable_url").get
    val startTime = request.req.get("start_time").get
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val imf = request.req.get("imf_b")
    val swD = request.req.get("sw_density")
    val swV = request.req.get("sw_velocity")
    val dst = request.req.get("dst")
    val al = request.req.get("al")
    
    val imf_b = validateOptDoubleSeq(imf) match {
      case Some(i) if(i.length == 3) => 
        	Some(ListOfDouble(Some(i(0)), Some(i(1)), Some(i(2))))
      case _ => None
    }
    /* ListOfDouble(
        Some(-1.0), // x
        Some(4.0), // y
        Some(1.0) // z
    ) */
          
    val extraParams = ExtraParams_calculateDataPointValueFixedTime(
        swD,//Some(4.0), // sw density
        swV,//Some(400.0), // sw velocity 
        imf_b, // imf b 
        dst,//Some(30.0), // dst
        al,//Some(150.0), // al
        filetype // output filetype
    )
        	
    val result = sinp.service.calculateDataPointValueFixedTime(
        id, // resourceId
        TimeProvider.getISODate(startTime), // start time
        Some(extraParams), // extra params
        new URI(url)) // votable_url
                
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e: ParseException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "time parameter not in ISO 8601 format", request.req)))
      case e @ (_:NumberFormatException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "illegal number provided", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "calculateDataPointValue at SINP", 
      nickname = "calculateDataPointValue",
      notes = "calculates magnetic field values for Earth of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/SimulationModel/SINP/Earth/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot",
        required = true,
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
  @Path("/calculateDataPointValue")
  def calculateDataPointValue = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val url = request.req.get("votable_url").get
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val extraParams = ExtraParams_calculateDataPointValue(
        filetype // output filetype
    )
          
    val result = sinp.service.calculateDataPointValue(
        id, // resourceId
        Some(extraParams), // extra params
        new URI(url) // votable_url  
    )
           
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "calculateDataPointValueSpacecraft at SINP", 
      nickname = "calculateDataPointValueSpacecraft",
      notes = "calculates magnetic field values for Earth along a given spacecraft trajectory", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/SimulationModel/SINP/Earth/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "spacecraft_name",
        value = "Spacecraft Name",
        defaultValue = "CLUSTER1",
        allowableValues = "Cassini_Public,Galileo,Voyager_1,Voyager_2,Pioneer_10,Pioneer_11,PVO,ACE,VEX,MEX,MGS,MAVEN,MESSENGER,ULYSSES,Stereo-A,Stereo-B,WIND,THEMIS-A,THEMIS-B,THEMIS-C,THEMIS-D,THEMIS-E,CLUSTER1,CLUSTER2,CLUSTER3,CLUSTER4,DoubleStar1,IMP-8,GEOTAIL,POLAR,INTERBALL-Tail,ISEE-1,ISEE-2",       
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "Start Time",
        defaultValue = "2010-01-12T13:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "Stop Time",
        defaultValue = "2010-01-13T03:45:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "sampling",
        value = "Sampling",
        defaultValue = "PT600S",
        required = true,
        dataType = "duration",
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/calculateDataPointValueSpacecraft")
  def calculateDataPointValueSpacecraft = PortalAction { implicit request =>
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val scName = request.req.get("spacecraft_name").get
    val startTime = request.req.get("start_time").get
    val stopTime = request.req.get("stop_time").get
    val sampling = request.req.get("sampling").get
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val extraParams = ExtraParams_calculateDataPointValueSpacecraft(
        filetype // output filetype
    )
          
    val result = sinp.service.calculateDataPointValueSpacecraft(
        id, // resourceId
        SpacecraftTypeSINP.fromString(scName, 
            scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")), // spacecraft name
        TimeProvider.getISODate(startTime), // start time
        TimeProvider.getISODate(stopTime), // stop time
        TimeProvider.getDuration(sampling), // sampling
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result, request.req)
    } catch  {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:ParseException | _:IllegalArgumentException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "time parameter not in ISO 8601 format", request.req)))
      case e: MatchError => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "unkown spacecraft name", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "calculateFieldLine at SINP", 
      nickname = "calculateFieldLine",
      notes = "calculates magnetic field lines for Earth of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/SimulationModel/SINP/Earth/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "Start Time",
        defaultValue = "2010-01-12T13:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
    	name = "line_length",
    	value = "Line Length",
    	required = false,
    	dataType = "double",
    	paramType = "query"),
    new ApiImplicitParam(
    	name = "step_size",
    	value = "Step Size",
    	required = false,
    	dataType = "double",
    	paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/calculateFieldline")
  def calculateFieldLine = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val startTime = request.req.get("start_time").get
    val url = request.req.get("votable_url").get
    // extra params
    val lineLength = request.req.get("line_length")
    val stepSize = request.req.get("step_size")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val extraParams = ExtraParams_calculateFieldLine(
        lineLength, // line length 
	  	stepSize, // step size 
        filetype // output filetype
	)
	  	  
	val result = sinp.service.calculateFieldLine(
	  	id, // resourceId
	  	TimeProvider.getISODate(startTime), // start time
	  	Some(extraParams), // extra params
	  	new URI(url) // votable_url
	)
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:ParseException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "time parameter not in ISO 8601 format", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "calculateCube at SINP", 
      nickname = "calculateCube",
      notes = "returns an interpolated cube of the magnetic field of Earth", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/SINP/Earth/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "Start Time",
        defaultValue = "2005-09-11T02:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "sampling",
        value = "Sampling",
        defaultValue = "0.7",
        required = true,
        dataType = "double",
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_b",
        value = "IMF B [nT]",
        required = false,
        dataType = "list(double)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_density", 
        value = "SW Density [1/cm^3]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sw_velocity", 
        value = "SW Velocity [km/s]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "al", 
        value = "Al [nT]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "dst", 
        value = "Dst [nT]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/calculateCube")
  def calculateCube = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val startTime = request.req.get("start_time").get
    val sampling = request.req.get("sampling").get
    // extra params
    val imf = request.req.get("imf_b")
    val swD = request.req.get("sw_density")
    val swV = request.req.get("sw_velocity")
    val dst = request.req.get("dst")
    val al = request.req.get("al")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val imf_b = validateOptDoubleSeq(imf) match {
      case Some(i) if(i.length == 3) => 
        	Some(ListOfDouble(Some(i(0)), Some(i(1)), Some(i(2))))
      case _ => None
    }
    /*ListOfDouble(
        Some(5.1), // x
        Some(-8.9), // y
        Some(4.5) // z
    )*/
	
	val extraParams = ExtraParams_calculateCube(
	    swD,//Some(5.0), // sw density
	    swV,//Some(800.0), // sw velocity
	    imf_b, // imf b
	    dst,//Some(-23.0), // dst
	    al,//Some(-1117.0), // al
        filetype // output filetype
	)
	
	// @TODO add cube parameters as GET
	val cubeSize = Cube_size_array(
	    Some(BigInt(-40)), // x_low
	    Some(BigInt(10)), // x_high
	    Some(BigInt(-15)), // y_low
	    Some(BigInt(15)), // y_high
	    Some(BigInt(-10)), // z_low
	    Some(BigInt(10)) // z_high
	)
	  	  
	val result = sinp.service.calculateCube(
	  	id, // resourceId
	  	TimeProvider.getISODate(startTime), // start time 
	  	Some(extraParams), // extra params
	  	Some(sampling.toDouble), // sampling 
	    Some(cubeSize) // cube size
	)
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:ParseException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "time parameter not in ISO 8601 format", request.req)))
      case e @ (_:NumberFormatException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "illegal number provided", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "calculateCubeMercury at SINP", 
      nickname = "calculateCubeMercury",
      notes = "returns an interpolated cube of the magnetic field of Mercury", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/SimulationModel/SINP/Mercury/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_b",
        value = "IMF B [nT]",
        required = false,
        dataType = "list(double)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "bd", 
        value = "BD [nT]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "flux", 
        value = "Flux [MWb]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rss", 
        value = "RSS [Rm]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "r2", 
        value = "R2 [Rm]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "dz", 
        value = "DZ [Rm]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/calculateCubeMercury")
  def calculateCubeMercury = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    // extra params
    val imf = request.req.get("imf_b")
    val bd = request.req.get("bd")
    val flux = request.req.get("flux")
    val rss = request.req.get("rss")
    val r2 = request.req.get("r2")
    val dz = request.req.get("dz")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val imf_b = validateOptDoubleSeq(imf) match {
      case Some(i) if(i.length == 3) => 
        	Some(ListOfDouble(Some(i(0)), Some(i(1)), Some(i(2))))
      case _ => None
    }  
    /*ListOfDouble(
        Some(5.1), // x
        Some(-8.9), // y
        Some(4.5) // z
    )*/
	
	val extraParams = ExtraParams_calculateCubeMercury(
	    bd,//Some(196.0), // bd
	  	flux,//Some(4.0), // flux
	  	rss,//Some(1.5), // rss
	  	r2,//Some(1.5), // r2
	  	dz,//Some(0.0), // dz
	  	imf_b, // imf b
        filetype // output filetype
	)
	  	  
	val result = sinp.service.calculateCubeMercury(
	    id, // resourceId
	  	Some(extraParams) // extra params
	)
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:NumberFormatException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "illegal number provided", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "calculateDataPointValueMercury at SINP", 
      nickname = "calculateDataPointValueMercury",
      notes = "returns interpolated magnetic field values for Mercury of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/SimulationModel/SINP/Mercury/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://dec1.sinp.msu.ru/~lucymu/paraboloid/XYZ_calcDPVMercury.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_b",
        value = "IMF B [nT]",
        required = false,
        dataType = "list(double)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "bd", 
        value = "BD [nT]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "flux", 
        value = "Flux [MWb]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rss", 
        value = "RSS [Rm]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "r2", 
        value = "R2 [Rm]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "dz", 
        value = "DZ [Rm]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/calculateDataPointValueMercury")
  def calculateDataPointValueMercury = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val url = request.req.get("votable_url").get
    // extra params
    val imf = request.req.get("imf_b")
    val bd = request.req.get("bd")
    val flux = request.req.get("flux")
    val rss = request.req.get("rss")
    val r2 = request.req.get("r2")
    val dz = request.req.get("dz")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val imf_b = validateOptDoubleSeq(imf) match {
      case Some(i) if(i.length == 3) => 
        	Some(ListOfDouble(Some(i(0)), Some(i(1)), Some(i(2))))
      case _ => None
    }  
    /*ListOfDouble(
        Some(5.1), // x
        Some(-8.9), // y
        Some(4.5) // z
    ) */
    
    val extraParams = ExtraParams_calculateDataPointValueMercury(
        filetype, // output filetype
        bd,//Some(-196.0), // bd
        flux,//Some(131.0), // flux
        rss,//Some(1.35), // rss
        r2,//Some(1.32), // r2
        dz,//Some(0.0), // dz
        imf_b // imf b
    )
	  	  
	val result = sinp.service.calculateDataPointValueMercury(
	  	id, // resourceId
	  	Some(extraParams), // extra params
	  	new URI(url) // votable_url
	)
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:NumberFormatException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "illegal number provided", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "calculateCubeSaturn at SINP", 
      nickname = "calculateCubeSaturn",
      notes = "returns an interpolated cube of the magnetic field of Saturn", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/SimulationModel/SINP/Saturn/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time", 
        value = "Start Time", 
        defaultValue = "2008-09-10T12:00:00",
        required = true, 
        dataType = "dateTime", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sampling", 
        value = "Sampling", 
        defaultValue = "1.0",
        required = true, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_b",
        value = "IMF B [nT]",
        required = false,
        dataType = "list(double)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "bdc", 
        value = "BDC [nT]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "bt", 
        value = "BT [nT]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rd2", 
        value = "RD2 [Rs]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rd1", 
        value = "RD1 [Rs]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "r2", 
        value = "R2 [Rs]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rss", 
        value = "RSS [Rs]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/calculateCubeSaturn")
  def calculateCubeSaturn = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val startTime = request.req.get("start_time").get
    val sampling = request.req.get("sampling").get
    // extra params
    val imf = request.req.get("imf_b")
    val bdc = request.req.get("bdc")
    val bt = request.req.get("bt")
    val rd2 = request.req.get("rd2")
    val rd1 = request.req.get("rd1")
    val r2 = request.req.get("r2")
    val rss = request.req.get("rss")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val imf_b = validateOptDoubleSeq(imf) match {
      case Some(i) if(i.length == 3) => 
        	Some(ListOfDouble(Some(i(0)), Some(i(1)), Some(i(2))))
      case _ => None
    }  
    /*ListOfDouble(
        Some(0.0), // x
        Some(0.0), // y
        Some(0.0) // z
    )*/
    
    val extraParams = ExtraParams_calculateCubeSaturn(
	    bdc,//Some(3.0), // bdc
	    bt,//Some(-7.0), // bt
	    rd2,//Some(6.5), // rd2
	    rd1,//Some(15.0), // rd1
	    r2,//Some(18.0), // r2
	    rss,//Some(22.0), // rss
	    imf_b, // imf b
        filetype // output filetype
	)
	
	// @TODO add cube parameters as GET
	val cubeSize = Cube_size_array(
	    Some(BigInt(-6)), // x_low
		Some(BigInt(7)), // x_high
	  	Some(BigInt(-3)), // y_low
	  	Some(BigInt(3)), // y_high
	  	Some(BigInt(-3)), // z_low
	  	Some(BigInt(3)) // z_high
	)
	  	   
	val result = sinp.service.calculateCubeSaturn(
	    id, // resourceId
	  	TimeProvider.getISODate(startTime),  // start time
	  	Some(extraParams), // extra params
	  	Some(sampling.toDouble), // sampling 
	  	Some(cubeSize) // cube size
	)
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:ParseException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "time parameter not in ISO 8601 format", request.req)))
      case e @ (_:NumberFormatException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "illegal number provided", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "calculateDataPointValueSaturn at SINP", 
      nickname = "calculateDataPointValueSaturn",
      notes = "returns interpolated magnetic field values for Saturn of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/SimulationModel/SINP/Saturn/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "votable url",
        defaultValue = "http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_b",
        value = "IMF B [nT]",
        required = false,
        dataType = "list(double)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "bdc", 
        value = "BDC [nT]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "bt", 
        value = "BT [nT]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rd2", 
        value = "RD2 [Rs]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rd1", 
        value = "RD1 [Rs]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "r2", 
        value = "R2 [Rs]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rss", 
        value = "RSS [Rs]", 
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/calculateDataPointValueSaturn")
  def calculateDataPointValueSaturn = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val url = request.req.get("votable_url").get
    // extra params
    val imf = request.req.get("imf_b")
    val bdc = request.req.get("bdc")
    val bt = request.req.get("bt")
    val rd2 = request.req.get("rd2")
    val rd1 = request.req.get("rd1")
    val r2 = request.req.get("r2")
    val rss = request.req.get("rss")
    val filetype = request.req.get("output_filetype").getOrElse("")

    val imf_b = validateOptDoubleSeq(imf) match {
      case Some(i) if(i.length == 3) => 
        	Some(ListOfDouble(Some(i(0)), Some(i(1)), Some(i(2))))
      case _ => None
    } 
	/*ListOfDouble(
	    Some(0.0), // x
        Some(0.0), // y
        Some(0.0) // z
    )*/
	 
	val extraParams = ExtraParams_calculateDataPointValueSaturn(
        filetype, // output filetype
	  	bdc,//Some(3.0), // bdc
	  	bt,//Some(-7.0), // bt
	  	rd2,//Some(6.5), // rd2
	  	rd1,//Some(15.0), // rd1
	  	r2,//Some(18.0), // r2
	  	rss,//Some(22.0), // rss
	  	imf_b // imf b
	)
	  	   
	val result = sinp.service.calculateDataPointValueSaturn(
	    id, // resourceId
	  	Some(extraParams), // extra params
	  	new URI(url) // votable_url
	)
	  	
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:NumberFormatException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "illegal number provided", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "getSurface at SINP", 
      nickname = "getSurfaceSINP",
      notes = "returns a surface of interpolated simulation parameters", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/SINP/Earth/2003-11-20UT12",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "plane_point", 
        value = "Plane Point", 
        defaultValue = "0.0,0.0,0.0",
        required = true, 
        dataType = "list(float)", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "plane_normal_vector", 
        value = "Plane Normal Vector", 
        defaultValue = "0.0,0.0,1.0",
        required = true, 
        dataType = "list(float)", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "variable",
        value = "Parameter Keys",
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
    val plane_point = request.req.get("plane_point").get
    val plane_n_vector = request.req.get("plane_normal_vector").get
    // opt params
    val variable = request.req.get("variable")
    // extra params
    val resolution = request.req.get("resolution")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val extraParams = ExtraParams_getSurfaceSINP(
        resolution,//Some(0.2), // resolution
        filetype // output filetype
    )
           
    val result = sinp.service.getSurface(
        id, // resourceId
        validateOptStringSeq(variable), // variable
        plane_point, // plane point
        plane_n_vector, // plane normal vector
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
    }
  }
  
  @GET
  @ApiOperation(
      value = "calculateCubeJupiter at SINP", 
      nickname = "calculateCubeJupiter",
      notes = "returns an interpolated cube of the magnetic field of Jupiter", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/SimulationModel/SINP/Jupiter/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time", 
        value = "Start Time", 
        defaultValue = "2008-09-10T12:00:00",
        required = true, 
        dataType = "dateTime", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sampling", 
        value = "Sampling", 
        defaultValue = "10.0",
        required = true, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_b",
        value = "IMF B [nT]",
        defaultValue = "0,0,0",
        required = false,
        dataType = "list(double)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "bdc", 
        value = "BDC [nT]", 
        defaultValue ="2.5",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "bt", 
        value = "BT [nT]", 
        defaultValue ="-2.5",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rd2", 
        value = "RD2 [Rs]", 
        defaultValue = "80.0",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rd1", 
        value = "RD1 [Rs]", 
        defaultValue = "19.0",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "r2", 
        value = "R2 [Rs]", 
        defaultValue = "100.0",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rss", 
        value = "RSS [Rs]", 
        defaultValue = "80.0",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "Output Filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/calculateCubeJupiter")
  def calculateCubeJupiter = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val startTime = request.req.get("start_time").get
    val sampling = request.req.get("sampling").get
    // extra params
    val imf = request.req.get("imf_b")
    val bdc = request.req.get("bdc")
    val bt = request.req.get("bt")
    val rd2 = request.req.get("rd2")
    val rd1 = request.req.get("rd1")
    val r2 = request.req.get("r2")
    val rss = request.req.get("rss")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val imf_b = validateOptDoubleSeq(imf) match {
      case Some(i) if(i.length == 3) => 
        	Some(ListOfDouble(Some(i(0)), Some(i(1)), Some(i(2))))
      case _ => None
    }  
    /*ListOfDouble(
        Some(0.0), // x
        Some(0.0), // y
        Some(0.0) // z
    )*/
    
    val extraParams = ExtraParams_calculateCubeJupiter(
    	bdc, //Some(2.5), // bdc
	  	bt, //Some(-2.5), // bt
	  	rd2, //Some(80.0), // rd2
	  	rd1, //Some(19.0), // rd1
	  	r2, //Some(80.0), // r2
	  	rss, //Some(100.0), // rss
	  	imf_b, // imf b
	  	Some(VOTableType) // output filetype
	)
	
	// @TODO add cube parameters as GET
	val cubeSize = Cube_size_array(
	  	Some(BigInt(-450)), // x_low
	  	Some(BigInt(150)), // x_high
	  	Some(BigInt(-300)), // y_low
	  	Some(BigInt(300)), // y_high
	  	Some(BigInt(-300)), // z_low
	  	Some(BigInt(300)) // z_high
	)
	  	   
	val result = sinp.service.calculateCubeJupiter(
	    id, // resourceId
	  	TimeProvider.getISODate(startTime),  // start time
	  	Some(extraParams), // extra params
	  	Some(sampling.toDouble), // sampling 
	  	Some(cubeSize) // cube size
	)
    
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:ParseException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "time parameter not in ISO 8601 format", request.req)))
      case e @ (_:NumberFormatException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "illegal number provided", request.req)))
    }
  }  
  
  
  
  
  @GET
  @ApiOperation(
      value = "calculateDataPointValueJupiter at SINP", 
      nickname = "calculateDataPointValueJupiter",
      notes = "returns interpolated magnetic field values for Saturn of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        //value = "resource id", 
        value = "SimulationModel, NumericalOutput",
        defaultValue = "spase://IMPEX/SimulationModel/SINP/Jupiter/OnFly",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "votable url",
        defaultValue = "http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_b",
        value = "IMF B [nT]",
        defaultValue = "0,0,0",
        required = false,
        dataType = "list(double)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "bdc", 
        value = "BDC [nT]", 
        defaultValue ="2.5",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "bt", 
        value = "BT [nT]", 
        defaultValue ="-2.5",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rd2", 
        value = "RD2 [Rs]", 
        defaultValue ="80.0",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rd1", 
        value = "RD1 [Rs]", 
        defaultValue ="19.0",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "r2", 
        value = "R2 [Rs]", 
        defaultValue ="100.0",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "rss", 
        value = "RSS [Rs]", 
        defaultValue ="80.0",
        required = false, 
        dataType = "double", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/calculateDataPointValueJupiter")
  def calculateDataPointValueJupiter = PortalAction { implicit request =>
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val url = request.req.get("votable_url").get
    // extra params
    val imf = request.req.get("imf_b")
    val bdc = request.req.get("bdc")
    val bt = request.req.get("bt")
    val rd2 = request.req.get("rd2")
    val rd1 = request.req.get("rd1")
    val r2 = request.req.get("r2")
    val rss = request.req.get("rss")
    val filetype = request.req.get("output_filetype").getOrElse("")

    val imf_b = validateOptDoubleSeq(imf) match {
      case Some(i) if(i.length == 3) => 
        	Some(ListOfDouble(Some(i(0)), Some(i(1)), Some(i(2))))
      case _ => None
    } 
	/*ListOfDouble(
	    Some(0.0), // x
        Some(0.0), // y
        Some(0.0) // z
    )*/
	 
	val extraParams = ExtraParams_calculateDataPointValueJupiter(
        filetype, // output filetype
	  	bdc,//Some(2.5), // bdc
	  	bt,//Some(-2.5), // bt
	  	rd2,//Some(80.0), // rd2
	  	rd1,//Some(19.0), // rd1
	  	r2,//Some(100.0), // r2
	  	rss,//Some(80.0), // rss
	  	imf_b // imf b
	)
	  	   
	val result = sinp.service.calculateDataPointValueJupiter(
	    id, // resourceId
	  	Some(extraParams), // extra params
	  	new URI(url) // votable_url
	)
	  	
    returnDefaultResult(result, request.req)
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e @ (_:NumberFormatException) => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "illegal number provided", request.req)))
    }
     
  }
  
  
   
   
  
}