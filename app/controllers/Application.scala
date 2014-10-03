package controllers

import models.actor._
import models.binding._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import views.html._


object Application extends BaseController {
  
  // route for portal client
  def index = Action { Ok(views.html.portal()) }
  
  // route for swagger ui
  def apiview = Action { Ok(views.html.apiview()) }
  
  // general testing route
  def test = PortalAction.async { implicit request =>

    println("Host="+request.host)
    
    val fileName = "mocks/getDPV_LATMOS.xml"
    val xml = scala.xml.XML.loadFile(fileName)
    val obj = scalaxb.fromXML[VOTABLE](xml)
    val json = Json.toJson(obj)
    
    // we must do it like this
    val obj2 = json.as[VOTABLE]
    
    UserService.addXMLUserData(request.sessionId, xml)
    
    val future = UserService.getUserData(request.sessionId)
    
    future map { files => 
      val json = Json.toJson(files map { data => 
        Json.obj("id" -> JsString(data.id.toString), 
        "url" -> JsString("http://"+request.host+"/userdata/"+data.name))
      })
      Ok(json).withSession("id" -> request.sessionId)
    }
    
    //Ok(Json.toJson(obj2)).withSession("id" -> request.sessionId)
    //Ok(json)
  }
  
  // test form for uploader
  def uploadTest = PortalAction { implicit request => 
    Ok(views.html.uploadTest()).withSession("id" -> request.sessionId) 
  }
    
}