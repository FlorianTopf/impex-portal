package controllers

import play.api.mvc._
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil
import models.actor.ConfigService
import scala.xml._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

object Config extends Controller {
  import models.actor.ConfigService._
  
  def config(fmt: String = "xml") = Action.async {
    val future = ConfigService.request(GetConfig(fmt))
    fmt.toLowerCase match {
      case "xml" => future.mapTo[NodeSeq].map(config => Ok(config))
      case "json" => future.mapTo[JsValue].map(config => Ok(config))
    }
  }
}