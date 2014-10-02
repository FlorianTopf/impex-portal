package controllers

import models.actor._
import models.binding._
import models.actor.ConfigService._
import models.enums._
import models.provider._
import views.html._
import play.api.Play.current
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
import com.typesafe.plugin._


object Application extends BaseController {
  
  // route for portal client
  def index = Action { Ok(views.html.portal()) }
  
  // route for swagger ui
  def apiview = Action { Ok(views.html.apiview()) }
  
  // feedback mailer
  // @TODO check if config is available!
  def feedback = PortalAction { implicit request =>
    val mail = use[MailerPlugin].email
    //println(request.body.asFormUrlEncoded)
    
    //@TODO if parameters are not present return this
    //Ok(Json.obj("success" -> false, "message" -> "Please fill out the form completely."))
    try {
    	val subject = request.body.asFormUrlEncoded.get("inputSubject")(0)
    	val email = request.body.asFormUrlEncoded.get("inputEmail")(0)
    	val name = request.body.asFormUrlEncoded.get("inputName")(0)
    	val tool = request.body.asFormUrlEncoded.get("inputTool")(0)
    	val message = request.body.asFormUrlEncoded.get("inputMessage")(0)
    	val challenge = request.body.asFormUrlEncoded.get("captcha[challenge]")(0)
    	val response = request.body.asFormUrlEncoded.get("captcha[response]")(0)
    	// we need the remote ip for the captcha
    	val remoteIp = request.remoteAddress
    	val impex = "IMPEx Support <impex-support@googlegroups.com>"
    	val user = name+"<"+email+">"
    	
    	val isValid: Boolean = CaptchaProvider.check(remoteIp, challenge , response)
    	//println(subject+" "+email+" "+name+" "+tool+" "+message+" "+challenge+" "+remoteIp)
    	
    	isValid match {
    	  case true => {
    	    mail.setSubject("["+tool+"] "+subject)
    		// recipient 
    		//mail.setRecipient("Florian Topf <florian.topf@oeaw.ac.at>")
    		// set google support group as CC
    		mail.setRecipient(impex)
    		mail.setReplyTo(email)
    		//mail.setReplyTo(impex)
    		//or use a list
    		//mail.setBcc(List("Dummy <example@example.org>", "Dummy2 <example@example.org>"):_*)
    		// from is the e-mail from the form
    		mail.setFrom(user)
    		
    		val html = <html> 
    		<p> Name: {name} <br/>
    	    Email: {email} <br/>
    	    Tool: {tool} <br/>
    	    Subject: {subject} <br/>
    	    Message : {message}<br/>
    	    </p></html>
    		
    		//sends html
    		mail.sendHtml(html.toString())
    		//sends text/text
    		//mail.send(message)
    		//sends both text and html
    		//mail.send( "text", "<html>html</html>")*/
    	    Ok(Json.obj("success" -> true, "message" -> "Thanks! We have received your message."))
    	  }
    	  case _ => {
    	    Ok(Json.obj("success" -> false, "message" -> "Message could not be sent. Captcha Invalid."))
    	  } 
    	}
      
    } catch {
      case e: Throwable => {
        println(e)
        Ok(Json.obj("success" -> false, "message" -> "Message could not be sent. Mailer Error."))
      }
    }
  }
  
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
          // we return the base without appendices
          // maybe introduce empty error case
          Ok(Json.toJson(regions.map(_.replace(".Magnetosphere", "")).distinct))
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
            // matches e.g. Earth => Earth, Earth.Magnetosphere
            if(run.AccessInformation.length > 0 && 
                run.SimulatedRegion.filter(p => p.contains(regionName)).length > 0) {
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
  
  // route for upload forms
  def addFileUserData = PortalAction.async(parse.multipartFormData) { implicit request =>
    request.body.file("votable").map(_.ref) match {
      case Some(votable) => {
        val name = request.body.file("votable").map(_.filename).get
        val host = current.configuration.getString("swagger.api.basepath").get
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
        val host = current.configuration.getString("swagger.api.basepath").get
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
        val host = current.configuration.getString("swagger.api.basepath").get
        Json.obj("id" -> JsString(data.id), 
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