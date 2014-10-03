package controllers

import models.actor.ConfigService
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
    value = "/config", 
    description = "operations for extracting the IMPEx configuration")
@Path("/config")
@Produces(Array(APPLICATION_JSON, APPLICATION_XML))
object Config extends BaseController {
  import models.actor.ConfigService._
  
  @GET
  @ApiOperation(
      value = "get config", 
      nickname = "getConfig",
      notes = "returns the configuration file", 
      httpMethod = "GET")
  def config(
      @ApiParam(value = "format in XML or JSON")
      @QueryParam("fmt")
      @DefaultValue("xml") fmt: String = "xml") = PortalAction.async { implicit request =>
    val future = ConfigService.request(GetConfig(fmt))
    fmt.toLowerCase match {
      case "xml" => future.mapTo[NodeSeq].map(config => Ok(config).withSession("id" -> request.sessionId))
      case "json" => future.mapTo[JsValue].map(config => Ok(config).withSession("id" -> request.sessionId))
      case _ => future.mapTo[JsValue].map(error => BadRequest(error))
    }
  }
}