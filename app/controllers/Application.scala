package controllers

import models.actor._
import models.binding._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import scala.xml._
import scala.collection.mutable.ListBuffer
import views.html._
import models.enums._
import akka.actor._
import akka.pattern.ask
import play.libs.Akka
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil


object Application extends Controller {
  
  def index = Action {
    Ok(views.html.portal())
  }
  
  // route for testing
  def test = Action.async {
    val future = RegistryService.getSimulationRun(Some("impex://FMI"), "false")
    future.map { _ match {
      case Left(tree) => {
        Ok(Json.toJson(tree))
      }
      case Right(error) => BadRequest(Json.toJson(error))
    }}
  }

}