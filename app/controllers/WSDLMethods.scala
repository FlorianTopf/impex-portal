package controllers

import models.actor.ConfigService._
import models.binding.Database
import models.enums._
import models.provider._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
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
    description = "operations for using the IMPEx data access services")
@Path("/methods")
@Produces(Array(APPLICATION_JSON, APPLICATION_XML))
object WSDLMethods extends BaseController {
  
  // Returns WSDLs of individual databases  
  @GET
  @ApiOperation(
      value = "get WSDL file", 
      nickname = "getWSDL",
      notes = "returns the WDSL file of a database", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown provider"), 
    new ApiResponse(code = 400, message = "not implemented")))
  def methods(
      @ApiParam(value = "database name stored in the configuration", 
          defaultValue = "FMI-HYBRID") @PathParam("dbName") dbName: String) = PortalAction.async {
    for {
      databases <- models.actor.ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- Some(dbName) match {
        case Some(name) if(databases.exists(d => name == d.name)) => {
          val db: Database = databases.find(d => name == d.name).get
          val rId: String = UrlProvider.encodeURI(db.id)
          val fileName: String = PathProvider.getPath("methods", rId, db.methods.head)
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
  
  // INTERMEDIATE: Returns modified WSDL files for Taverna
  @GET
  @ApiOperation(
      value = "get WSDL file for Taverna", 
      nickname = "getWSDLTaverna",
      notes = "returns the WDSL file of a database supported by Taverna", 
      httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "unkown provider"), 
    new ApiResponse(code = 400, message = "not implemented")))
  @Path("/taverna")
  def methodsTaverna(
      @ApiParam(value = "database name stored in the configuration", 
          defaultValue = "SINP") @PathParam("dbName") dbName: String) = PortalAction.async {
    for {
      databases <- models.actor.ConfigService.request(GetDatabases).mapTo[Seq[Database]]
      provider <- Some(dbName) match {
        case Some(name) if(databases.exists(d => name == d.name)) => {
          val db: Database = databases.find(d => name == d.name).get
          val rId: String = UrlProvider.encodeURI(db.id)
          // there is always only one methods file
          val modFileName: String = db.methods.head.take((db.methods.head.indexOf(".")))+"-MOD.wsdl"
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