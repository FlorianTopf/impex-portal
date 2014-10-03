package controllers

import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent._
import scala.xml._
import java.io.File
import models.actor._


object UserData extends BaseController {
  
  // route for upload forms
  def addFileUserData = PortalAction.async(parse.multipartFormData) { implicit request =>
    request.body.file("votable").map(_.ref) match {
      case Some(votable) => {
        val name = request.body.file("votable").map(_.filename).get
        val host = current.configuration.getString("swagger.api.basepath").get
        UserService.addFileUserData(request.sessionId, votable, name) map { data =>
          val json = Json.obj("id" -> JsString(data.id.toString), "name" -> JsString(name),
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
        val host = current.configuration.getString("swagger.api.basepath").get
        UserService.addXMLUserData(request.sessionId, votable) map { data => 
          val json = Json.obj("id" -> JsString(data.id.toString), 
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
        val host = current.configuration.getString("swagger.api.basepath").get
        Json.obj("id" -> JsString(data.id.toString), 
            "name" -> JsString(data.name.replace("-"+data.id, "")),
        "url" -> JsString(host+"/userdata/"+data.name))
      })
      Ok(json).withSession("id" -> request.sessionId)
    }   
  }
  
  // route for getting one file => url is public (we need to send it to tools)
  def getUserdata(fileName: String) = PortalAction { implicit request => 
    def getRecursiveListOfFiles(dir: File): Array[File] = {
    	val these = dir.listFiles
    	these ++ these.filter(_.isDirectory).flatMap(getRecursiveListOfFiles)
    }
    try {
    //val file = new File("userdata/"+request.sessionId+"/"+fileName)
      val dir = new File("userdata/")
      
      val allFiles = getRecursiveListOfFiles(dir)
      val file = allFiles.filter((f) => { f.getName() == fileName }).head
      
      val xml = scala.xml.XML.loadFile(file)
      Ok(xml).withSession("id" -> request.sessionId)

    } catch {
      //case e: FileNotFoundException => BadRequest("File Not Found")
      case e: NoSuchElementException => BadRequest("File Not Found")
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