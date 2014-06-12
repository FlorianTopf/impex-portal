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
    
    val extraParams = ExtraParams_getDataPointValueFMI(
        None, // interpolation method
        validateFiletype(filetype) // output filetype
    )
    
    val result = fmi.service.getDataPointValue(
        id.get, // resourceId
        None, // variable
        new URI(url.get), // url_xyz
        Some(extraParams) // extra params
    )
    
    returnDefaultResult(result)
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
        defaultValue = "PT600S",
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
    
    val extraParams = ExtraParams_getDataPointValueFMI(
        Some(LinearValue), // interpolation method
        validateFiletype(filetype) // output filetype
    )
           
    val result = fmi.service.getDataPointValueSpacecraft(
        // resourceId
        "impex://FMI/NumericalOutput/GUMICS/earth/synth_stationary/solarmin/EARTH___n_T_Vx_Bx_By_Bz__7_100_600_3p_03_15m/tilt15p/H+_mstate", 
        None, // variable (@TODO to be added)
        SpacecraftType.fromString(scName.get, 
            scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")), // spacecraft name
        TimeProvider.getISODate(startTime.get), // start time
        TimeProvider.getISODate(stopTime.get), // stop time
        TimeProvider.getDuration(sampling.get), // sampling
        Some(extraParams) // extra params
    )
               
    returnDefaultResult(result)
  }
  
  
  def getSurface = ???
  
  
  def getVOTableURL = ???
  
  
  def getMostRelevantRun = ???
  
  
  def getFieldLine = ???
  
  
  def getParticleTrajectory = ???
  
  
  def getDataPointSpectra = ???
  
  
  def getDataPointSpectraSpacecraft = ???
  

}