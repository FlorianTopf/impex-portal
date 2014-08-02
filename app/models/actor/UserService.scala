package models.actor

import play.libs._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import akka.pattern._
import akka.util.Timeout
import org.bson.types.ObjectId
import play.libs.Akka
import java.io.File
import scala.xml._
import play.api.libs.Files._
import org.apache.commons.io.FileUtils


case class AddXMLData(xml: Node)
case class AddFileData(file: TemporaryFile)
case object GetUserData
case object StopUserService

// @TODO improve error handling (timeouts etc.)
class UserService(val id: ObjectId) extends Actor {
  implicit val timeout = Timeout(10.seconds)
  
  val fileDir = new File("userdata/"+id+"/")
  var files: Seq[String] = Nil
  
  // this is needed when the actor died, but the session data is still existing
  override def preStart: Unit = {
     if(fileDir.exists) {
       // fetch all existing files
       val exFiles = fileDir.listFiles.filter(_.getName.endsWith(".xml"))
       files = files ++ exFiles.map(f => f.getName())
     }
     else {
       fileDir.mkdir()
     }
  }
  
  def receive = {
    case GetUserData => sender ! files
    case AddXMLData(xml) => {
    	val fileName = "votable-"+new ObjectId()+".xml"
    	val file = new File("userdata/"+id+"/"+fileName)
    	if(!file.exists) file.createNewFile()
    	// here we add the fileName
    	files = files ++ Seq(fileName)
    	// here we save the content
    	scala.xml.XML.save("userdata/"+id+"/"+fileName, xml, "UTF-8")
    }
    case AddFileData(file) => {
    	val fileName = "votable-"+new ObjectId()+".xml"
    	file.moveTo(new File("userdata/"+id+"/"+fileName))
    	// here we add the fileName
    	files = files ++ Seq(fileName)
    }
    case StopUserService => { 
      context.stop(self)
      // delete all files connected to the actor
      FileUtils.deleteDirectory(fileDir)
    }
  }
  
}


object UserService {
   implicit val timeout = Timeout(10.seconds)
  
   // returns ActorRef
  def checkIfExists(userId: String): ActorRef = {
    val actorSel: ActorSelection = Akka.system.actorSelection("user/"+userId) 
    val askSel: AskableActorSelection = new AskableActorSelection(actorSel)
    val identityFuture: Future[ActorIdentity] = (askSel ? Identify(None)).mapTo[ActorIdentity]
    val identity: ActorIdentity = Await.result(identityFuture, 10.seconds)
    val actorRef: Option[ActorRef] = identity.ref
    
    actorRef match {
      case Some(actorRef) => actorRef
      case None => { 
        val actorRef: ActorRef = 
        	Akka.system.actorOf(Props(new UserService(new ObjectId(userId))), name = userId)
        // user actors get killed after 24 hours
        Akka.system.scheduler.scheduleOnce(24.hours, actorRef, StopUserService)
        actorRef
      }
    }
      
  }
   
  def getUserData(userId: String): Future[Seq[String]] = {
    val actorRef: ActorRef = checkIfExists(userId)
    (actorRef ? GetUserData).mapTo[Seq[String]]
  }
  
  def addXMLUserData(userId: String, xml: Node) = {
    val actorRef: ActorRef = checkIfExists(userId) 
    (actorRef ? AddXMLData(xml))   
  }
  
  def addFileUserData(userId: String, file: TemporaryFile) = {
    val actorRef: ActorRef = checkIfExists(userId)
    (actorRef? AddFileData(file))
  }
    
  
}