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
import scala.concurrent._
import java.io.FileNotFoundException


object Application extends BaseController {
  
  // route for portal client
  def index = Action { Ok(views.html.portal()) }
  
  // route for swagger ui
  def apiview = Action { Ok(views.html.apiview()) }
  
  // route for testing
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
        Json.obj("id" -> JsString(data.id), 
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
  
  // @TODO refactor all responses for userdata actions
  // route for upload forms
  def addFileUserData = PortalAction.async(parse.multipartFormData) { implicit request =>
    request.body.file("votable").map(_.ref) match {
      case Some(votable) => {
        UserService.addFileUserData(request.sessionId, votable) map { data =>
          val json = Json.obj("id" -> JsString(data.id), 
              "url" -> JsString("http://"+request.host+"/userdata/"+data.name))
          Ok(json).withSession("id" -> request.sessionId)
        }   
      }
      case None => future { BadRequest("Upload Failed").withSession("id" -> request.sessionId) }
    }
  }
  
  // alternative route (not used atm)
  def addXMLUserData = PortalAction.async { implicit request =>
    request.body.asXml.map(_.asInstanceOf[Node]) match {
      case Some(votable) => { 
        UserService.addXMLUserData(request.sessionId, votable) map { data => 
          val json = Json.obj("id" -> JsString(data.id), 
              "url" -> JsString("http://"+request.host+"/userdata/"+data.name))
          Ok(json).withSession("id" -> request.sessionId)
        }
      }
      case None => future { BadRequest("Upload Failed").withSession("id" -> request.sessionId) }
    }
  }
  
  // route for listing userdata
  def listUserdata = PortalAction.async { implicit request => 
    val future = UserService.getUserData(request.sessionId)
    future map { files => 
      val json = Json.toJson(files map { data => 
        Json.obj("id" -> JsString(data.id), 
        "url" -> JsString("http://"+request.host+"/userdata/"+data.name))
      })
      Ok(json).withSession("id" -> request.sessionId)
    }   
  }
  
  // route for getting one file
  def getUserdata(fileName: String) = PortalAction { implicit request => 
    try {
      val file = new File("userdata/"+request.sessionId+"/"+fileName)
      val xml = scala.xml.XML.loadFile(file)
      Ok(xml).withSession("id" -> request.sessionId)
    } catch {
      case e: FileNotFoundException => BadRequest("File Not Found")
    }
  }
  
  // route for removing one file
  def deleteUserData(fileName: String) = PortalAction.async { implicit request => 
    val future = UserService.deleteUserData(request.sessionId, fileName)
    future map { _ match {
      case true => Ok("File Deleted").withSession("id" -> request.sessionId)
      case false => BadRequest("File Not Deleted").withSession("id" -> request.sessionId)
    }}
  }
  
}