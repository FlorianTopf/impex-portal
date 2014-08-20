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
import play.Play


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
  
  // testing filters
  def getRegions() = PortalAction.async { implicit request => 
    val future = RegistryService.getNumericalOutput(None, false)
    future.map { _ match {
        case Left(spase) => { 
          val regions = spase.ResourceEntity.flatMap{ r => 
            val run = r.as[NumericalOutput]
            run.SimulatedRegion
          }
          // @TODO should we only return those 
          // with/without appendices (e.g. .Magnetosphere)
          // maybe introduce empty error case
          Ok(Json.toJson(regions.distinct))
        }
        case Right(error) => BadRequest(Json.toJson(error))
      }
    } 
  }
  
  // testing filters
  def filterRegion(regionName: String) = PortalAction.async { implicit request =>
    val future = RegistryService.getNumericalOutput(None, false)
    future.map { _ match {
        case Left(spase) => { 
          val regions = spase.ResourceEntity.flatMap{ r => 
            val run = r.as[NumericalOutput]
            // matches e.g. Earth => Earth, Earth.Magnetosphere or
            // Earth.Magnetosphere => Earth, Earth.Magnetosphere
            val region = regionName.replace(".Magnetosphere", "")
            if(run.AccessInformation.length > 0 && 
                run.SimulatedRegion.filter(p => p.contains(region)).length > 0) {
              Some(run.AccessInformation.head.RepositoryID)
            } else
              None
          }
          // maybe introduce empty error case
          Ok(Json.toJson(regions.distinct))
        }
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }
    
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
        val name = request.body.file("votable").map(_.filename).get
        val host = Play.application().configuration().getString("swagger.api.basepath")
        UserService.addFileUserData(request.sessionId, votable, name) map { data =>
          val json = Json.obj("id" -> JsString(data.id), "name" -> JsString(name),
              "url" -> JsString(host+"/userdata/"+data.name))
          Ok(json).withSession("id" -> request.sessionId)
        }   
      }
      case None => future { BadRequest("Upload Failed").withSession("id" -> request.sessionId) }
    }
  }
  
  // alternative route (not used atm => no name available!)
  def addXMLUserData = PortalAction.async { implicit request =>
    request.body.asXml.map(_.asInstanceOf[Node]) match {
      case Some(votable) => { 
        val host = Play.application().configuration().getString("swagger.api.basepath")
        UserService.addXMLUserData(request.sessionId, votable) map { data => 
          val json = Json.obj("id" -> JsString(data.id), 
              "url" -> JsString(host+"/userdata/"+data.name))
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
        val host = Play.application().configuration().getString("swagger.api.basepath")
        Json.obj("id" -> JsString(data.id), 
            "name" -> JsString(data.name.replace("-"+data.id, "")),
        "url" -> JsString(host+"/userdata/"+data.name))
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