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


case object Kill
case class AddXMLData(xml: Node)
case class AddFileData(file: TemporaryFile)
case object GetData

class UserService(val id: ObjectId) extends Actor {
  implicit val timeout = Timeout(10.seconds)
  
  val fileDir = new File("userdata/"+id+"/")
  var files: Seq[String] = Nil
  
  // this is needed when the actor died, but the session still is here!
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
  
  // we need to delete all resources in the userdata dir
  //override def postStop: Unit = {
  //  FileUtils.deleteDirectory(fileDir)
  //}
  
  def receive = {
    case GetData => sender ! files
    // @TODO maybe we should return something (+ error handling)
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
    case Kill => { 
      context.stop(self)
      FileUtils.deleteDirectory(fileDir)
    }
  }
  
}


object UserService {
   implicit val timeout = Timeout(10.seconds)
  
   // will return actorRef later
  def checkIfExists(id: String): ActorRef = {
    val actorSel: ActorSelection = Akka.system.actorSelection("user/"+id) 
    val asker: AskableActorSelection = new AskableActorSelection(actorSel)
    val identityFuture: Future[ActorIdentity] = asker.ask(Identify(None)).mapTo[ActorIdentity]
    val identity: ActorIdentity = Await.result(identityFuture, 10.seconds)
    val actorRef: Option[ActorRef] = identity.ref
    
    actorRef match {
      case Some(a) => a
      case None => { 
        val actorRef: ActorRef = Akka.system.actorOf(Props(new UserService(new ObjectId(id))), name = id)
        Akka.system.scheduler.scheduleOnce(5.minutes, actorRef, Kill)
        actorRef
      }
    }
      
  }
   
  def getUserData(userId: String): Future[Seq[String]] = {
    val actorRef: ActorRef = checkIfExists(userId)
    (actorRef ? GetData).mapTo[Seq[String]]
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