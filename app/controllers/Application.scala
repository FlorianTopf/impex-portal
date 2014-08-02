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
import akka.actor._
import akka.pattern.ask
import java.net.URI
import javax.xml.datatype._
import org.bson.types.ObjectId
import java.io.File


object Application extends BaseController {
  
  // route for portal client
  def index = Action { Ok(views.html.portal()) }
  
  // route for swagger ui
  def apiview = Action { Ok(views.html.apiview()) }
  
  // route for testing
  def test = PortalAction.async { implicit request =>

    val fileName = "mocks/getDPV_LATMOS.xml"
    val xml = scala.xml.XML.loadFile(fileName)
    val obj = scalaxb.fromXML[VOTABLE](xml)
    val json = Json.toJson(obj)
    
    // we must do it like this
    val obj2 = json.as[VOTABLE]
    
    UserService.addXMLUserData(request.sessionId, xml)
    
    val future = UserService.getUserData(request.sessionId)
    
    future.map(f => Ok(Json.toJson(f)).withSession("id" -> request.sessionId))
    //future.mapTo[String].map(f => Ok(f).withSession("id" -> request.sessionId))
    
    //Ok(Json.toJson(obj2)).withSession("id" -> request.sessionId)
    //Ok(json)
  }
  

  // test form for uploader
  def uploadTest = PortalAction { implicit request => 
    Ok(views.html.uploadTest()).withSession("id" -> request.sessionId) 
  }
  
  // route for upload forms
  def addUserData = PortalAction(parse.multipartFormData) { implicit request =>
    request.body.file("votable") map { votable =>
      UserService.addFileUserData(request.sessionId, votable.ref)
    }
    Ok("Ok").withSession("id" -> request.sessionId)
  }
  /*def addUserData = PortalAction { implicit request =>
  request.body.asXml.map { xml =>
    UserService.addXMLUserData(request.sessionId, xml.asInstanceOf[Node])
  }
  Ok("Ok").withSession("id" -> request.sessionId)
  }*/
  
  // route for listing userdata
  def listUserdata = PortalAction.async { implicit request => 
    val future = UserService.getUserData(request.sessionId)
    future.map(files => Ok(Json.toJson(files)).withSession("id" -> request.sessionId))   
  }
  
  // route for getting one file
  def getUserdata(fileName: String) = PortalAction { implicit request => 
    // catch error here! 
    val file = new File("userdata/"+request.sessionId+"/"+fileName)
    val xml = scala.xml.XML.loadFile(file)
    Ok(xml).withSession("id" -> request.sessionId)
  }
  
}