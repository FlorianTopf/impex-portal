package controllers

import play.api.mvc._
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil
import models.actor.RegistryService
import scala.xml._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

object Methods extends Controller {
  
  // @TODO return in json 
  def methods = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getMethods(req.get("id"))
    future.map { _ match {
      // @FIXME we return a merged version of multiple WSDLs
      case Left(tree) => Ok(tree.reduce(_++_))
      case Right(error) => BadRequest(Json.toJson(error))
    }}
  }
}