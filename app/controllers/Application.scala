package controllers

import models.actor._
import models.binding._
import models.actor.ConfigService._
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
import java.net.URI
import javax.xml.datatype._


object Application extends BaseController {
  
  // route for portal client
  def index = Action { Ok(views.html.portal()) }
  
  // route for swagger ui
  def apiview = Action { Ok(views.html.apiview()) }
  
  // route for testing
  def test = PortalAction {

    val fileName = "mocks/getDPV_LATMOS.xml"
    val xml = scala.xml.XML.loadFile(fileName)
    val obj = scalaxb.fromXML[VOTABLE](xml)
    val json = Json.toJson(obj)
    
    // we must do it like this
    val obj2 = json.as[VOTABLE]
    
    
    Ok(Json.toJson(obj2))
    //Ok(json)
  }
  
}