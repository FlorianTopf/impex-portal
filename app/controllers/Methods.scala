package controllers

import models.actor.RegistryService
import models.actor.ConfigService._
import models.binding.Database
import models.enums._
import models.provider._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import scala.xml._
import scala.concurrent._
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil
import javax.ws.rs._
import javax.ws.rs.core.MediaType._
import java.net.URI
import java.io.FileNotFoundException

@Api(
    value = "/methods", 
    description = "operations for using the IMPEx data acess services")
@Path("/methods")
@Produces(Array(APPLICATION_JSON, APPLICATION_XML))
object Methods extends Controller {
  
  // Returns WSDLs of individual databases  
  @GET
  @ApiOperation(
      value = "get WSDL file", 
      nickname = "getWSDL",
      notes = "returns the WDSL file of a database", 
      response = classOf[Elem], 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown provider"), 
    new ApiResponse(code = 400, message = "not implemented")))
  //alternative route for returning WSDS of individual databases (by name path)
  def methods(
      @ApiParam(value = "database name part of the id stored in the config", 
          defaultValue = "SINP") @PathParam("dbName") dbName: String) = PortalAction.async {
    // @FIXME prepare for possible changes with the prefixes! (spase://?)
    val future = RegistryService.getMethods(Some("impex://"+dbName))
    future.map { _ match {
      // @FIXME we return a merged version of multiple WSDLs
      case Left(methods) => { 
        if(!methods.isEmpty)
        	Ok(methods.reduce(_++_))
        else
            // if there are no methods return error 501
        	BadRequest(Json.toJson(RequestError(ERequestError.NOT_IMPLEMENTED)))
      }
      case Right(error) => BadRequest(Json.toJson(error))
    }}
  }
  
  // INTERMEDIATE: Provides paths to modified WSDL files for Taverna
  @GET
  @ApiOperation(
      value = "get WSDL file for Taverna", 
      nickname = "getWSDLTaverna",
      notes = "returns the WDSL file of a database supported by Taverna", 
      response = classOf[Elem], 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown provider"), 
    new ApiResponse(code = 400, message = "not implemented")))
  def methodsTaverna(
      @ApiParam(value = "database name part of the id stored in the config", 
          defaultValue = "SINP") @PathParam("dbName") dbName: String) = PortalAction.async {
    for {
      databases <- models.actor.ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      // @FIXME prepare for possible changes with the prefixes! (spase://?)
      provider <- Some("impex://"+dbName) match {
        case Some(id) if(databases.exists(d => id.contains(d.id.toString))) => {
          val db: Database = databases.find(d => id.contains(d.id.toString)).get
          val rId: String = UrlProvider.encodeURI(db.id)
          val modFileName: String = db.methods.head.take((db.methods.head.indexOf(".")))+"-MOD.wsdl"
          println(modFileName)
          val fileName: String = PathProvider.getPath("methods", rId, modFileName)
          future { 
            try { 
              Left(scala.xml.XML.load(fileName))
            } catch {
              case e: FileNotFoundException => Right(RequestError(ERequestError.NOT_IMPLEMENTED))
            }
          }
        }
        case _ => future { Right(RequestError(ERequestError.UNKNOWN_PROVIDER)) }
      }
    } yield provider match {
      case Left(methods) => Ok(methods)
      case Right(error) => BadRequest(Json.toJson(error))
    }
  }
  
}