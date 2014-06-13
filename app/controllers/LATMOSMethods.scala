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

// @TODO catch WS errors of any kind
// @TODO add validation for mandatory parameters
// @TODO include request parameters to responses

@Api(
    value = "/methods", 
    description = "operations for using the IMPEx data acess services")
@Path("/methods/LATMOS")
@Produces(Array(APPLICATION_XML, APPLICATION_JSON))
object LATMOSMethods extends Controller {
  import controllers.Helpers._
  
  val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
  
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
        value = "resource id", 
        defaultValue = "impex://LATMOS/Hybrid/Mars_13_02_13/Mag/3D",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "url_xyz",
        value = "votable url",
        defaultValue = "http://impex.latmos.ipsl.fr/Vmvrv5e.xml",
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
        paramType = "query")))
  @Path("/getDataPointValue")
  def getDataPointValue = PortalAction { implicit request =>

    val id = request.req.get("id")
    val url = request.req.get("url_xyz")
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse(VOTableType.toString)
    
    val extraParams = ExtraParams_getDataPointValueLATMOS(
        None, // imf clockangle (@TODO to be added)
        validateFiletype(filetype) // output filetype
    )
    
    val result = latmos.service.getDataPointValue(
        id.get, // resourceId
        None, // variable (@TODO to be added)
        new URI(url.get), // url_xyz
        Some(extraParams) // extra params
     )
    
    returnDefaultResult(result)
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
        value = "resource id", 
        defaultValue = "impex://LATMOS/Hybrid/Mars_13_02_13/The/3D",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sc_name",
        value = "spacecraft name",
        defaultValue = "MEX",
        allowableValues = "MEX,MGS,VEX",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "start time",
        defaultValue = "2007-07-12T00:00:00",
        required = true,
        dataType = "ISO 8601 datetime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "stop time",
        defaultValue = "2007-07-13T00:00:00",
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
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/getDataPointValueSpacecraft")
  def getDataPointValueSpacecraft = PortalAction { implicit request => 
    
    val id = request.req.get("id")
    val scName = request.req.get("sc_name")
    val startTime = request.req.get("start_time")
    val stopTime = request.req.get("stop_time")
    val sampling = request.req.get("sampling")
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse(VOTableType.toString)
    
    val extraParams = ExtraParams_getDataPointValueLATMOS(
        Some(120.0), // imf clockangle (@TODO to be added)
        validateFiletype(filetype) // output filetype
    )
                  
    val result = latmos.service.getDataPointValueSpacecraft(
        id.get, // resourceId
        None, // variable (@TODO to be added)
        SpacecraftType.fromString(scName.get, 
            scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")), // spacecraft name
        TimeProvider.getISODate(startTime.get), // start time
        TimeProvider.getISODate(stopTime.get), // stop time
        TimeProvider.getDuration(sampling.get), // sampling
        Some(extraParams)) // extra params
    
    returnDefaultResult(result)
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
        value = "resource id", 
        defaultValue = "impex://LATMOS/Hybrid/Mars_13_02_13/The/3D",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "plane_point", 
        value = "plane point", 
        defaultValue = "0.0,0.0,0.0",
        required = true, 
        dataType = "list(float)", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "plane_normal_vector", 
        value = "plane normal vector", 
        defaultValue = "0.0,0.0,1.0",
        required = true, 
        dataType = "list(float)", 
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
    
    val id = request.req.get("id")
    val plane_point = request.req.get("plane_point").getOrElse("0.0,0.0,0.0")
    val plane_n_vector = request.req.get("plane_normal_vector").getOrElse("0.0,0.0,1.0")
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse(VOTableType.toString)
    
    val extraParams = ExtraParams_getSurfaceLATMOS(
        None, //resolution (@TODO TO BE TESTED)
        Some(0), // imf clockangle (@TODO to be added)
        validateFiletype(filetype) // output filetype
    )
           
    val result = latmos.service.getSurface(
        id.get, // resourceId
        None, // variable (@TODO to be added)
        validateFloatSeq(plane_point), // plane point
        validateFloatSeq(plane_n_vector), // plane normal vector
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result)
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
        value = "resource id", 
        defaultValue = "impex://LATMOS/Hybrid/Mars_14_01_13/Mag/MEX/0.0",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "start time",
        defaultValue = "2007-07-12T00:00:00",
        required = true,
        dataType = "ISO 8601 datetime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "stop time",
        defaultValue = "2007-07-13T00:00:00",
        required = true,
        dataType = "ISO 8601 datetime",
        paramType = "query")))    
  @Path("/getFileURL")
  def getFileURL = PortalAction { implicit request => 
    
    val id = request.req.get("id")
    val startTime = request.req.get("start_time")
    val stopTime = request.req.get("stop_time")
    
    val result = latmos.service.getFileURL(
        id.get, // resourceId
        TimeProvider.getISODate(startTime.get), // start time
        TimeProvider.getISODate(stopTime.get) // stop time
    )
           
    //@TODO save votable file on disk and provide link to it
    // first "use case" for session
           
    result.fold(
        fault => BadRequest(Json.toJson(
            ServiceResponse(
                EServiceResponse.BAD_REQUEST, 
                fault.original.asInstanceOf[Fault].faultstring))), 
        votable => Ok(scalaxb.toXML[VOTABLE](votable, "VOTABLE", scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")))
    )
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
        value = "resource id", 
        defaultValue = "impex://LATMOS/Hybrid/Mars_13_02_13/Mag/3D",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "url_xyz",
        value = "votable url",
        defaultValue = "http://impex.latmos.ipsl.fr/Vmvrv5e.xml",
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
    
    val id = request.req.get("id")
    val url = request.req.get("url_xyz")
    // extra params
    val direction = request.req.get("direction").getOrElse("Both")
    val filetype = request.req.get("output_filetype").getOrElse(VOTableType.toString)
    
    val extraParams = ExtraParams_getFieldLineLATMOS(
        validateDirection(direction), // direction
        None, // stepsize (@TODO TO BE TESTED)
        validateFiletype(filetype) // output filetype
    )
     
    val result = latmos.service.getFieldLine(
        id.get, // resourceId
        None, // variable (@TODO to be added)
        new URI(url.get), // url_xyz
        Some(extraParams) // extra params
    )
     
    returnDefaultResult(result)
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
        value = "resource id", 
        defaultValue = "impex://LATMOS/Hybrid/Mars_14_03_14/IonSpectra",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "url_xyz",
        value = "votable url",
        defaultValue = "http://impex.latmos.ipsl.fr/Vmrmf2.xml",
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
        paramType = "query")))
  @Path("/getDataPointSpectra")
  def getDataPointSpectra = PortalAction { implicit request => 
    
    val id = request.req.get("id")
    val url = request.req.get("url_xyz")
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse(VOTableType.toString)
    
    val extraParams = ExtraParams_getDataPointSpectraLATMOS(
        None, // imf clockangle (@TODO TO BE TESTED)
        validateFiletype(filetype), // output filetype
        None // energy channel (@TODO TO BE TESTED)
    )
           
    val result = latmos.service.getDataPointSpectra(
        id.get, // resourceId
        new URI(url.get), // url_xyz
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result)
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
        value = "resource id", 
        defaultValue = "impex://LATMOS/Hybrid/Mars_14_03_14/IonSpectra",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "sc_name",
        value = "spacecraft name",
        defaultValue = "MEX",
        allowableValues = "MEX,MGS,VEX",
        required = true,
        dataType = "string",
        paramType = "query"),
    new ApiImplicitParam(
        name = "start_time",
        value = "start time",
        defaultValue = "2010-01-01T18:00:00",
        required = true,
        dataType = "ISO 8601 datetime",
        paramType = "query"),
    new ApiImplicitParam(
        name = "stop_time",
        value = "stop time",
        defaultValue = "2010-01-01T19:00:00",
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
        name = "output_filetype",
        value = "output filetype",
        defaultValue = "VOTable",
        allowableValues = "VOTable,netCDF",
        required = false,
        dataType = "string",
        paramType = "query")))
  @Path("/getDataPointSpectraSpacecraft")
  def getDataPointSpectraSpacecraft = PortalAction { implicit request => 
    
    val id = request.req.get("id")
    val scName = request.req.get("sc_name")
    val startTime = request.req.get("start_time")
    val stopTime = request.req.get("stop_time")
    val sampling = request.req.get("sampling")
    // extra params
    val filetype = request.req.get("output_filetype").getOrElse(VOTableType.toString)
    
    val extraParams = ExtraParams_getDataPointSpectraLATMOS(
        None, // imf clockangle (@TODO TO BE TESTED)
        validateFiletype(filetype), // output filetype
        None // energy channel (@TODO TO BE TESTED)
    )
           
    val result = latmos.service.getDataPointSpectraSpacecraft(
        id.get, // resourceId
        SpacecraftType.fromString(scName.get, 
            scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")), // spacecraft name
        TimeProvider.getISODate(startTime.get), // start time
        TimeProvider.getISODate(stopTime.get), // stop time
        TimeProvider.getDuration(sampling.get), // sampling
        Some(extraParams) // extra params
    )
  
    returnDefaultResult(result)
  }
    

}