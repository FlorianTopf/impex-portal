package controllers

import models.provider._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import com.typesafe.plugin._


object Feedback extends BaseController {
  
  // feedback mailer
  def sendForm = PortalAction { implicit request =>
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
        //println(e)
        Ok(Json.obj("success" -> false, "message" -> "Message could not be sent. Mailer Error."))
      }
    }
  }

}