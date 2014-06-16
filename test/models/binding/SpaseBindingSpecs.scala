package models.binding

import models.binding._
import models.provider._
import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test._
import play.api.libs.concurrent._
import play.api.libs.json._
import akka.pattern.ask
import akka.testkit._
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.xml.NodeSeq
import java.util.Random


// Specification also exists in package models.binding
object SpaseBindingSpecs extends org.specs2.mutable.Specification with Mockito {
  
    // test info
  	val rand = new Random(java.lang.System.currentTimeMillis)
    val config = scalaxb.fromXML[Impexconfiguration](scala.xml.XML.loadFile("conf/configuration.xml"))
	val databases = config.impexconfigurationoption.filter(_.key.get == "database").map(
	    _.as[Database]).filter(d => d.typeValue == Simulation)
  
    "SpaseBinding" should {
        
        "marshall all available XML files" in {
          databases map { database => 
            val id: String = UrlProvider.encodeURI(database.id)
            // @FIXME taking only one random tree from one database
            val fileName: String = PathProvider.getPath("trees", 
              id, database.tree(rand.nextInt(database.tree.length))) 
            val spase = scalaxb.fromXML[Spase](scala.xml.XML.loadFile(fileName))
                
            spase must beAnInstanceOf[Spase]
            scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")) must beAnInstanceOf[NodeSeq]
            Json.toJson(spase) must beAnInstanceOf[JsValue]  
            
          }

        }

        
    }
}