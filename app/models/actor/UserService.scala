package models.actor

import play.libs._
import play.api.libs.Files._
import play.api.libs.concurrent.Execution.Implicits._
import scala.xml._
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import akka.pattern._
import akka.util.Timeout
import java.io.File
import java.io.FileNotFoundException
import org.bson.types.ObjectId
import org.apache.commons.io.FileUtils


case class AddXMLData(xml: Node)
case class AddFileData(file: TemporaryFile, name: String)
case class DeleteUserData(name: String)
case class UserData(id: ObjectId, name: String)
case object GetUserData
case object StopUserService

// @TODO improve error handling (timeouts etc.)
class UserService(val userId: ObjectId) extends Actor {
  implicit val timeout = Timeout(10.seconds)
  
  val fileDir = new File("userdata/"+userId+"/")
  var files: Seq[UserData] = Nil
  
  // this is needed when the actor died, but the session data is still existing
  override def preStart: Unit = {
     if(fileDir.exists) {
       // fetch all existing files
       val exFiles = fileDir.listFiles.filter(_.getName.endsWith(".xml"))
       files++=exFiles map { f => 
         UserData(new ObjectId(f.getName().split("-").last.replace(".xml", "")), f.getName())
       }
     }
     else {
       fileDir.mkdir()
     }
  }
  
  def receive = {
    case GetUserData => sender ! files
    case AddXMLData(xml) => {
        val dataId = new ObjectId()
    	val fileName = "votable-"+dataId+".xml"
    	val file = new File("userdata/"+userId+"/"+fileName)
    	if(!file.exists) file.createNewFile()
    	// here we add the fileName
    	val userdata = UserData(dataId, fileName)
    	files++=Seq(userdata)
    	// here we save the content
    	scala.xml.XML.save("userdata/"+userId+"/"+fileName, xml, "UTF-8")
    	sender ! userdata
    }
    case AddFileData(file, name) => {
        val dataId = new ObjectId()
    	val fileName = name.replace(".xml", "")+"-"+dataId+".xml"
    	file.moveTo(new File("userdata/"+userId+"/"+fileName))
    	// here we add the fileName
    	val userdata = UserData(dataId, fileName)
    	files++=Seq(userdata)
    	sender ! userdata
    }
    case DeleteUserData(fileName) => {
        try {
        	val delFile = new File("userdata/"+userId+"/"+fileName)
        	// filter out element, which is to be removed
    		files=files.filter(data => data.name != fileName)
    		// remove file on HD
    		sender ! delFile.delete()
        } catch {
           case e: FileNotFoundException => sender ! false
        }
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
  private def checkIfExists(userId: String): ActorRef = {
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
   
  // simple action to initialise a userservice actor
  def init(userId: String): ActorRef = checkIfExists(userId)
   
  def getUserData(userId: String): Future[Seq[UserData]] = {
    val actorRef: ActorRef = checkIfExists(userId)
    (actorRef ? GetUserData).mapTo[Seq[UserData]]
  }
  
  def addXMLUserData(userId: String, xml: Node): Future[UserData] = {
    val actorRef: ActorRef = checkIfExists(userId) 
    (actorRef ? AddXMLData(xml)).mapTo[UserData]   
  }
  
  def addFileUserData(userId: String, file: TemporaryFile, name: String): Future[UserData] = {
    val actorRef: ActorRef = checkIfExists(userId)
    (actorRef ? AddFileData(file, name)).mapTo[UserData]
  }
  
  def deleteUserData(userId: String, name: String) = {
    val actorRef: ActorRef = checkIfExists(userId)
    (actorRef ? DeleteUserData(name))
  }
  
  
}