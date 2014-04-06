package controllers

import models.actor._
import models.binding._
import models.enums._
import views.html._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.libs.Akka
import scala.xml._
import scala.collection.mutable.ListBuffer
import akka.actor._
import akka.pattern.ask


object Application extends Controller {
  
  // route for portal client
  def index = Action {
    Ok(views.html.portal())
  }
  
  // route for swagger ui
  def apiview = ???
  
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