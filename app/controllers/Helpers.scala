package controllers

import play.api.mvc._
import models.binding._
import models.enums._
import play.api.libs.json._
import soapenvelope11._

// @TODO add validators for timestamps, duration, sc names and URIs
object Helpers extends Controller {
    
  // helper method for validating the output filetype 
  def validateFiletype(filetype: String): Option[OutputFormatType] = {
    try {
      Some(OutputFormatType.fromString(filetype, scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
    } catch {
      // just use VOTable if a match error occurs
      case e: MatchError => Some(VOTableType)
    }
  }
  
  // helper method for validating float sequences
  def validateFloatSeq(floats: String): Seq[Float] = {
    try {
      floats.split(",").map(_.toFloat)      
    } catch {
      // in any error case return NULL vector
      case _: Throwable => Seq(0.0f, 0.0f, 0.0f)
    }
  }
  
  // helper method for validating directions
  def validateDirection(direction: String): Option[EnumDirection] = {
    try {
      Some(EnumDirection.fromString(direction, scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
    } catch {
      // just use Both if something wrong is entered
      case _: MatchError => Some(Both)
    }
  }
  
  // helper method which returns the default result of WS (URI or FAULT)
  def returnDefaultResult(result: Either[scalaxb.Soap11Fault[Any], java.net.URI]): SimpleResult = {
    result.fold(
        fault => BadRequest(Json.toJson(
            ServiceResponse(
                EServiceResponse.BAD_REQUEST, 
                fault.original.asInstanceOf[Fault].faultstring))), 
        uri => Ok(Json.toJson(ServiceResponse(EServiceResponse.OK, uri.toString)))
    )
  }
  
  

}