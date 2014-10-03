package models.actor


import models.binding._
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import scala.concurrent._
import scala.concurrent.duration._
import play.api.test.Helpers._
import play.api.test._
import play.api.libs.concurrent._
import play.api.libs.json._
import akka.pattern.ask
import akka.testkit._
import akka.actor._
import org.bson.types.ObjectId
import play.api.libs.Files.TemporaryFile
import java.io.File
import org.apache.commons.io.FileUtils


object UserServiceSpecs extends Specification with Mockito {
	val userId = new ObjectId().toString()
	val fileName = "fmi_hyb_orbite_60sec.xml"
	// loading the file (will be removed)
	val srcFile = new File("mocks/"+fileName)
	val testFile = new File("test/"+fileName)
	val testFileXML = scala.xml.XML.load("mocks/"+fileName)
	FileUtils.copyFile(srcFile, testFile)
  
	"UserService" should {
	  
	  "be initialised and contain data" in {
		  val app = new FakeApplication
          running(app) {
			  implicit val actorSystem = Akka.system(app)
			  val actor = UserService.init(userId)
			  val files = Await.result(UserService.getUserData(userId), DurationInt(10) second)
			  
			  actor must beAnInstanceOf[ActorRef]
			  files must beAnInstanceOf[Seq[UserData]]
			  files must beEmpty
		  }
	  }
	    
		  
	  "add user data as file" in {
	      val app = new FakeApplication
          running(app) {
			  implicit val actorSystem = Akka.system(app)
			  val file = TemporaryFile(testFile)
			  val actor = UserService.init(userId)
			  val result = Await.result(UserService.addFileUserData(userId, file, fileName).mapTo[UserData], DurationInt(10) second)
			  val files = Await.result(UserService.getUserData(userId), DurationInt(10) second)
			  
			  actor must beAnInstanceOf[ActorRef]
			  files must beAnInstanceOf[Seq[UserData]]
			  files.length must beEqualTo(1)
			  result must beAnInstanceOf[UserData]
			  result must beEqualTo(files.head)
			  result.id must beAnInstanceOf[ObjectId]
			  result.name must beEqualTo(fileName.replace(".xml", "")+"-"+files.head.id+".xml")
 		  }
	  }
	    
	    
	  "add user data as XML" in {
	      val app = new FakeApplication
          running(app) {
			  implicit val actorSystem = Akka.system(app)
			  val actor = UserService.init(userId)
			  val result = Await.result(UserService.addXMLUserData(userId, testFileXML).mapTo[UserData], DurationInt(10) second)
			  val files = Await.result(UserService.getUserData(userId), DurationInt(10) second)
			  
			  actor must beAnInstanceOf[ActorRef]
			  files must beAnInstanceOf[Seq[UserData]]
			  files.length must beEqualTo(2)
			  result must beAnInstanceOf[UserData]
			  result must beEqualTo(files(1))
			  result.id must beAnInstanceOf[ObjectId]
			  result.name must beEqualTo("votable-"+files.reverse.head.id+".xml")
 		  }
	  }
	    
	    
	  "delete user data" in {
	      val app = new FakeApplication
          running(app) {
			  implicit val actorSystem = Akka.system(app)
			  val actor = UserService.init(userId)
			  val filesInit = Await.result(UserService.getUserData(userId), DurationInt(10) second)
			  val result = Await.result(UserService.deleteUserData(userId, filesInit.head.name).mapTo[Boolean], DurationInt(10) second)
			  val filesEnd = Await.result(UserService.getUserData(userId), DurationInt(10) second)
			  
			  actor must beAnInstanceOf[ActorRef]
			  filesInit must beAnInstanceOf[Seq[UserData]]
			  filesEnd must beAnInstanceOf[Seq[UserData]]
			  filesEnd.length must beEqualTo(1)
			  result must beTrue
 		  }
	  }	  
	  

	  "be stopped and init cleanup" in {
	      val app = new FakeApplication
          running(app) {
			  implicit val actorSystem = Akka.system(app)
			  val actor = UserService.init(userId)
			  (actor ? StopUserService)
			  // just because specs would cry without matcher
			  actor must beAnInstanceOf[ActorRef]  
 		  }
	  }
	  
	  
	  "have deleted user directory" in {
		  val userDir = new File("userdata/"+userId)
		  userDir.exists() must beFalse
	  }
	  
	}
}