package controllers

import models.binding._
import models.provider._
import models.enums._
import play.api.mvc._
import play.api.libs.json._
import play.api.Logger
import scala.language.postfixOps
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
@Path("/methods/LATMOS")
@Produces(Array(APPLICATION_XML, APPLICATION_JSON))
object LATMOSMethods extends MethodsController {
  val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
  
  def isAlive() = PortalAction {
    try {
	  val future = latmos.service.isAlive() 
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
      value = "getDataPointValue at LATMOS", 
      nickname = "getDataPointValueLATMOS",
      notes = "returns interpolated simulation parameters of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_13_02_13/Mag/3D",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://impex.latmos.ipsl.fr/Vmvrv5e.xml",
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
        name = "imf_clockangle",
        value = "IMF Clockangle",
        defaultValue = "90.0",
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
  @Path("/getDataPointValue")
  def getDataPointValue = PortalAction { implicit request =>
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val url = request.req.get("votable_url").get
    // opt params
    val variable = request.req.get("variable")
    // extra params
    val imf = request.req.get("imf_clockangle")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val extraParams = ExtraParams_getDataPointValueLATMOS(
        imf, // imf clockangle
        filetype // output filetype
    )
    
    val result = latmos.service.getDataPointValue(
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
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "getDataPointValueSpacecraft at LATMOS", 
      nickname = "getDataPointValueSpacecraftLATMOS",
      notes = "returns interpolated simulation parameters along a given spacecraft trajectory", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_13_02_13/The/3D",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "spacecraft_name",
        value = "Spacecraft Name",
        defaultValue = "MEX",
        allowableValues = "MEX,MGS,VEX,MESSENGER,MMO,MPO,JUICE,Galileo",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "Start Time",
        defaultValue = "2007-07-12T00:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "Stop Time",
        defaultValue = "2007-07-13T00:00:00",
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
        defaultValue = "Ux,Uy,Uz,Utot",
        required = false,
        dataType = "list(string)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_clockangle",
        value = "IMF Clockangle",
        defaultValue = "120.0",
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
    val imf = request.req.get("imf_clockangle")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val extraParams = ExtraParams_getDataPointValueLATMOS(
        imf, // imf clockangle 
        filetype // output filetype
    )
                  
    val result = latmos.service.getDataPointValueSpacecraft(
        id, // resourceId
        validateOptStringSeq(variable), // variable
        SpacecraftType.fromString(scName, 
        scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")), // spacecraft name
        TimeProvider.getISODate(startTime), // start time
        TimeProvider.getISODate(stopTime), // stop time
        TimeProvider.getDuration(sampling), // sampling
        Some(extraParams)) // extra params
    
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
    }
  }
  
  @GET
  @ApiOperation(
      value = "getSurface at LATMOS", 
      nickname = "getSurfaceLATMOS",
      notes = "returns a surface of interpolated simulation parameters", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_13_02_13/The/3D",
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
        defaultValue = "Ux,Uy,Uz,Utot",
        required = false,
        dataType = "list(string)",
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_clockangle",
        value = "IMF Clockangle",
        defaultValue = "90.0",
        required = false,
        dataType = "double",
        paramType = "query"),
    new ApiImplicitParam(
        name = "resolution",
        value = "Resolution",
        defaultValue = "100.0",
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
    val imf = request.req.get("imf_clock_angle")
    val resolution = request.req.get("resolution")
    val filetype = request.req.get("output_filetype").getOrElse("")
    
    val extraParams = ExtraParams_getSurfaceLATMOS(
        resolution, // resolution 
        imf, // imf clockangle 
        filetype // output filetype
    )
           
    val result = latmos.service.getSurface(
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
      value = "getFileURL at LATMOS", 
      nickname = "getFileURL",
      notes = "returns the URL of a granule for a given time range", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_14_01_13/Mag/MEX/0.0",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "Start Time",
        defaultValue = "2007-07-12T00:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "Stop Time",
        defaultValue = "2007-07-13T00:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query")))    
  @Path("/getFileURL")
  def getFileURL = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val startTime = request.req.get("start_time").get
    val stopTime = request.req.get("stop_time").get
    
    val result = latmos.service.getFileURL(
        id, // resourceId
        TimeProvider.getISODate(startTime), // start time
        TimeProvider.getISODate(stopTime) // stop time
    )
                
    result.fold(
        fault => BadRequest(Json.toJson(
            ServiceResponse(
                EServiceResponse.BAD_REQUEST, 
                fault.original.asInstanceOf[Fault].faultstring, request.req))), 
        votable => { 
          val tables = votable.RESOURCE flatMap {r => r.resourcesequence1 map { s => s.resourceoption2.as[models.binding.Table] }}
          // there is always only one table (and fields)
          val fields = tables.head.tableoption.map(_.as[models.binding.Field])
          // there is alwaays only one table (and data)
          val data = tables.head.DATA.map(_.dataoption.as[models.binding.TableData]).get
          val values = data.TR map { t => t.TD map { t => t.value }}
          val result = values map {  v => (fields zip v).map{ case (f, v) => (f.name -> JsString(v)) }}
          
          //Ok(Json.toJson(ServiceResponseJson(EServiceResponse.OK, Json.toJson(votable), request.req))) 
           Ok(Json.toJson(ServiceResponseJson(EServiceResponse.OK, Json.toJson(result.map(_.toMap)) , request.req))) 
          //Ok(scalaxb.toXML[VOTABLE](votable, "VOTABLE", scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")))
        }
    )
    } catch {
      case e: NoSuchElementException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "mandatory parameter missing", request.req)))
      case e: ParseException => 
        BadRequest(Json.toJson(ServiceResponse(EServiceResponse.BAD_REQUEST, 
                "time parameter not in ISO 8601 format", request.req)))
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "getFieldLine at LATMOS", 
      nickname = "getFieldLineLATMOS",
      notes = "returns magnetic field lines of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id",  
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_13_02_13/Mag/3D",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://impex.latmos.ipsl.fr/Vmvrv5e.xml",
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
        // @TODO add remaining value
        defaultValue = "",
        required = false,
        dataType = "double",
        paramType = "query"),
    new ApiImplicitParam(
        name = "direction",
        value = "Direction",
        defaultValue = "Both",
        allowableValues = "Both,Forward,Backward",
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
    
    val extraParams = ExtraParams_getFieldLineLATMOS(
        direction, // direction
        stepSize, // stepsize 
        filetype // output filetype
    )
     
    val result = latmos.service.getFieldLine(
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
    }
  }
  
  
  @GET
  @ApiOperation(
      value = "getDataPointSpectra at LATMOS", 
      nickname = "getDataPointSpectraLATMOS",
      notes = "returns spectra of a given VOTable", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "SimulationRun, NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_14_03_14/IonSpectra",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "votable_url",
        value = "VOTable URL",
        defaultValue = "http://impex.latmos.ipsl.fr/Vmrmf2.xml",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "imf_clockangle",
        value = "IMF Clockangle",
        defaultValue = "90.0",
        required = false,
        dataType = "double",
        paramType = "query"),
    new ApiImplicitParam(
        name = "energy_channel",
        value = "Energy Channel",
        defaultValue = "EnergySpectra",
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
  @Path("/getDataPointSpectra")
  def getDataPointSpectra = PortalAction { implicit request => 
    try {
    // mandatory
    val id = request.req.get("id").get
    val url = request.req.get("votable_url").get
    // extra params
    val imf = request.req.get("imf_clockangle")
    val filetype = request.req.get("output_filetype").getOrElse("")
    val energyChannel = request.req.get("energy_channel")
    
    val extraParams = ExtraParams_getDataPointSpectraLATMOS(
        imf, // imf clockangle 
        filetype, // output filetype
        validateOptStringSeq(energyChannel) // energy channel 
    )
           
    val result = latmos.service.getDataPointSpectra(
        id, // resourceId
        new URI(url), // votable_url
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
      value = "getDataPointSpectraSpacecraft at LATMOS", 
      nickname = "getDataPointSpectraSpacecraftLATMOS",
      notes = "returns spectra along a given spacecraft trajectory", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "request failed")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "NumericalOutput",
        defaultValue = "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_14_03_14/IonSpectra",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "spacecraft_name",
        value = "Spacecraft Name",
        defaultValue = "MEX",
        allowableValues = "MEX,MGS,VEX,MESSENGER,MMO,MPO,JUICE,Galileo",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "Start Time",
        defaultValue = "2010-01-01T18:00:00",
        required = true,
        dataType = "dateTime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "Stop Time",
        defaultValue = "2010-01-01T19:00:00",
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
        name = "imf_clockangle",
        value = "IMF Clockangle",
        defaultValue = "90.0",
        required = false,
        dataType = "double",
        paramType = "query"),
    new ApiImplicitParam(
        name = "energy_channel",
        value = "Energy Channel",
        defaultValue = "EnergySpectra",
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
  @Path("/getDataPointSpectraSpacecraft")
  def getDataPointSpectraSpacecraft = PortalAction { implicit request => 
    try {
    // mandatory parameters
    val id = request.req.get("id").get
    val scName = request.req.get("spacecraft_name").get
    val startTime = request.req.get("start_time").get
    val stopTime = request.req.get("stop_time").get
    val sampling = request.req.get("sampling").get
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse("")
    val imf = request.req.get("imf_clockangle")
    val energyChannel = request.req.get("energy_channel")
    
    val extraParams = ExtraParams_getDataPointSpectraLATMOS(
        imf, // imf clockangle 
        filetype, // output filetype
        validateOptStringSeq(energyChannel) // energy channel 
    )
           
    val result = latmos.service.getDataPointSpectraSpacecraft(
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
    }
  }
    

}