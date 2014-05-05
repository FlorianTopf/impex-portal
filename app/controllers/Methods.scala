package controllers

import models.actor.RegistryService
import models.enums._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import scala.xml._
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil
import javax.ws.rs._
import javax.ws.rs.core.MediaType._

@Api(
    value = "/methods", 
    description = "operations for using the IMPEx data acess services")
@Path("/methods")
@Produces(Array(APPLICATION_JSON, APPLICATION_XML))
object Methods extends Controller {
  
  // only returning WSDLs of individual databases
  @GET
  @ApiOperation(
      value = "get WSDL file", 
      nickname = "getWSDL",
      notes = "returns the WDSL file of a database", 
      response = classOf[Elem], 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 501, message = "unkown provider"), 
    new ApiResponse(code = 502, message = "not implemented")))
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
        name = "id", 
        value = "database id stored in the config", 
        defaultValue = "impex://FMI",
        required = true, 
        dataType = "string", 
        paramType = "query")))
  def methods = PortalAction.async { implicit request =>
    val future = RegistryService.getMethods(request.req.get("id"))
    future.map { _ match {
      // @FIXME we return a merged version of multiple WSDLs
      case Left(tree) => { 
        if(!tree.isEmpty)
        	Ok(tree.reduce(_++_))
        else
            // if there are no methods return error 502
        	BadRequest(Json.toJson(RequestError(ERequestError.NOT_IMPLEMENTED)))
      }
      case Right(error) => BadRequest(Json.toJson(error))
    }}
  }
  
  
  // @TODO provide routes for modified files (taverna)
  
  
}