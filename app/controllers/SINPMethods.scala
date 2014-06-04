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
@Path("/methods/SINP")
@Produces(Array(APPLICATION_XML, APPLICATION_JSON))
object SINPMethods extends Controller {

  @GET
  @ApiOperation(
      value = "getDataPointValue", 
      nickname = "getDataPointValueSINP",
      notes = "returns interpolated simulation of a given VOTable", 
      httpMethod = "GET")
  //@ApiResponses(Array(
  //  new ApiResponse(code = 400, message = "unkown provider"), 
  //  new ApiResponse(code = 400, message = "not implemented")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "resource id", 
        defaultValue = "impex://SINP/NumericalOutput/Earth/2003-11-20UT12",
        required = true, 
        dataType = "string", 
        paramType = "query"),
    new ApiImplicitParam(
        name = "url_xyz",
        value = "vo table url",
        defaultValue = "http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_52points.vot",
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
    
    val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
    
    val extraParams = ExtraParams_getDataPointValueSINP(
        Some(VOTableType), // output filetype
        None // interpolation method (TO BE TESTED)
    )
    
    val result = sinp.service.getDataPointValue(
        id.get, // resourceId
        None, // variables (TO BE TESTED)
        Some(new URI(url.get)), // url_xyz
        Some(extraParams) // extra params
    )
    
    result.fold(f => BadRequest(f.toString), u => {
        val promise = WS.url(u.toString).get()
        val result = Await.result(promise, Duration(1, "minute")).xml
        Ok(result)
    })
    
  }
  
  
  
}