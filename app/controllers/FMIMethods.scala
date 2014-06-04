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


@Api(
    value = "/methods", 
    description = "operations for using the IMPEx data acess services")
@Path("/methods/FMI")
object FMIMethods extends Controller {
  
  @GET
  @ApiOperation(
      value = "getDataPointValue", 
      nickname = "getDataPointValueFMI",
      notes = "returns interpolated simulation of a given VOTable", 
      httpMethod = "GET")
  //@ApiResponses(Array(
  //  new ApiResponse(code = 400, message = "unkown provider"), 
  //  new ApiResponse(code = 400, message = "not implemented")))
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
        value = "vo table url",
        defaultValue = "http://impex-fp7.fmi.fi/ws_tests/input/getDataPointValue_input.vot",
        required = true,
        dataType = "string",
        paramType = "query")))
  @Path("/getDataPointValue")
  def getDataPointValue = PortalAction { implicit request =>
    // @TODO add appropriate input validation
    // @TODO we should check if a resourceid is valid => BUT we must also check the registry
    // => registry must not return empty spase elements (but an error)
    val id = request.req.get("id")
    val url = request.req.get("url_xyz")
    
    val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
    
    val extraParams = ExtraParams_getDataPointValueFMI(
        None, // interpolation method
        Some(VOTableType) // output filetype
    )
    
    val result = fmi.service.getDataPointValue(
        id.get, // resourceId
        None, // variable
        new URI(url.get), // url_xyz
        Some(extraParams) // extra params
    )
    
    result.fold(f => BadRequest(f.toString), u => {
        val promise = WS.url(u.toString).get()
        val result = Await.result(promise, Duration(1, "minute")).xml
        Ok(result)
    })
    
  }
  
  

}