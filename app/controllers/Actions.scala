package controllers

import play.api.mvc._
import play.api.mvc.Results._
import play.api.mvc.BodyParsers._
import play.api.libs.json._
import play.api.Play.current
import play.api.cache._
import scala.concurrent.Future
import models.binding._
import models.enums._
import soapenvelope11._
import org.bson.types.ObjectId


class BaseController extends Controller {

  // @TODO needed for preflight request of some browsers
  def options(entity: Any*) = CORS {
    Action { implicit request =>
      Ok("")
    }
  }

}

// custom portal request mapper
case class PortalRequest[A](val req: Map[String, String], val sessionId: String, request: Request[A]) extends WrappedRequest(request)

// custom portal action
object PortalAction extends ActionBuilder[PortalRequest] {
  def invokeBlock[A](request: Request[A], block: (PortalRequest[A]) => Future[SimpleResult]) = {
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    // creating or getting the session id
    val sessionId: String = request.session.get("id") match {
      case Some(id) => id.toString
      case None => { 
        val id = new ObjectId
        id.toString
      }
    }
    
    block(PortalRequest(req, sessionId, request))
  }
  //composing PortalAction with CACHE and CORS action
  override def composeAction[A](action: Action[A]) = CACHE(CORS(action))
  
}

// cache wrapper
case class CACHE[A](action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[SimpleResult] = {
    println("caching: "+request.uri)
    // applying response cache (with uri identifier)
    Cached(request => request.uri, 3600)(action)
    action(request)
  }
 
  lazy val parser = action.parser
 
}

// cors wrapper
case class CORS[A](action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[SimpleResult] = {
    CORSHeaders.withHeaders(action(request))
  }

  lazy val parser = action.parser
  
}

// cors headers
object CORSHeaders {
  import play.api.libs.concurrent.Execution.Implicits._
  
  def withHeaders(r: Future[SimpleResult]): Future[SimpleResult] = {
    r.map(_.withHeaders("Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Credentials" -> "true",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Content-Type, X-Requested-With, Accept",
      "Access-Control-Max-Age" -> (60 * 60 * 24).toString//,
      // we should not dictate the content type
      //"Content-Type" -> "application/json; charset=utf-8"
      ))
  }

}


class MethodsController extends BaseController {
  
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
      // @FIXME in any error case return empty sequence
      case _: Throwable => Seq()
    }
  }
  
  def validateOptFloatSeq(floats: Option[String]): Option[Seq[Float]] = {
    try {
      floats match {
        case Some(f) => Some(f.split(",").map(_.toFloat))
        case None => None
      }
    } catch {
      // @FIXME in any error case return none
      case _: Throwable => None
    }
  }
  
  def validateOptDoubleSeq(doubles: Option[String]): Option[Seq[Double]] = {
    try {
      doubles match {
        case Some(f) => Some(f.split(",").map(_.toDouble))
        case None => None
      }
    } catch {
      // @FIXME in any error case return none
      case _: Throwable => None
    }
  }
  
  // helper method for validation string sequences
  def validateOptStringSeq(strings: Option[String]): Option[Seq[String]] = {
    strings match {
      case Some(s) => Some(s.split(","))
      case None => None
    }
  }
  
  def validateOptDouble(double: Option[String]): Option[Double] = {
    try {
      double match {
        case Some(d) => Some(d.toDouble)
        case None => None
      }
    } catch {
      // @FIXME in any error case return none
      case _: Throwable => None
    }
  }
  
  def validateOptBigInt(bigInt: Option[String]): Option[BigInt] = {
    try {
      bigInt match {
        case Some(b) => Some(BigInt(b))
        case None => None
      }
    } catch {
      // @FIXME in any error case return none
      case _: Throwable => None
    }
  }
  
  // helper method for validating directions
  def validateDirection(direction: String): Option[EnumDirection] = {
    try {
      Some(EnumDirection.fromString(direction, scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
    } catch {
      // just use None if something wrong is entered
      case _: MatchError => None
    }
  }
  
  // helper method for validating interpolation methods
  def validateInterpolation(interpolation: String): Option[EnumInterpolation] = {
    try {
      Some(EnumInterpolation.fromString(interpolation, scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
    } catch {
      // just use None if something wrong is entered
      case _: MatchError => None
    }
  }
  
  // helper method for validating SW params (getMostRelevantRun)
  def validateSWParams(param: String, request: Map[String, String]): Option[SW_parameter] = {
    request.get(param+"_value") match {
      case Some(value) => Some(SW_parameter( // sw parameter
  	        value.toDouble, // value
            request.get(param+"_weight").map(_.toDouble), // weight
            request.get(param+"_scale").map(_.toDouble), // scale
            request.get(param+"_fun") // function
        ))
      case _ => None
    }
  }
  
  // helper method which returns the default result of WS (URI or FAULT)
  def returnDefaultResult(result: Either[scalaxb.Soap11Fault[Any], java.net.URI], request: Map[String, String]): SimpleResult = {
    result.fold(
        fault => BadRequest(Json.toJson(
            ServiceResponse(
                EServiceResponse.BAD_REQUEST, 
                fault.original.asInstanceOf[Fault].faultstring, request))), 
        uri => Ok(Json.toJson(ServiceResponse(EServiceResponse.OK, uri.toString, request)))
    )
  }
  

}