package controllers

import play.api.mvc._
import play.api.mvc.Results._
import play.api.mvc.BodyParsers._
import play.api.libs.json._
import scala.concurrent.Future

object Actions extends Controller {

  // needed for preflight request of some browsers
  def options(entity: Any*) = CORS {
    Action { implicit request =>
      Ok("")
    }
  }

}

case class CORS[A](action: Action[A]) extends Action[A] {

  def apply(request: Request[A]): Future[SimpleResult] = {
    CORSHeaders.withHeaders(action(request))
  }

  lazy val parser = action.parser

}

object CORSHeaders {
  import play.api.libs.concurrent.Execution.Implicits._
  
  def withHeaders(r: Future[SimpleResult]): Future[SimpleResult] = {
    r.map(_.withHeaders("Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Credentials" -> "true",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Content-Type, X-Requested-With, Accept",
      "Access-Control-Max-Age" -> (60 * 60 * 24).toString//,
      // @FIXME we should not dictate the content type
      //"Content-Type" -> "application/json; charset=utf-8"
      ))
  }

}
