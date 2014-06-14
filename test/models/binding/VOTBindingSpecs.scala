package models.binding


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
import java.io._


object VOTBindingSpecs extends org.specs2.mutable.Specification with Mockito {
  

    "VOTable Binding" should {
        
        "marshall random VOTable 1.2 files" in {
          
           val testFiles = new File("mocks/").listFiles.filter(_.getName.endsWith(".xml"))
           
           testFiles map { f => { 
             val xml = scalaxb.fromXML[VOTABLE](scala.xml.XML.loadFile(f))
             xml must beAnInstanceOf[VOTABLE]
             scalaxb.toXML[VOTABLE](xml, "VOTABLE", 
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")) must beAnInstanceOf[NodeSeq]
             Json.toJson(xml) must beAnInstanceOf[JsValue]
           }}
            
           testFiles must beAnInstanceOf[Array[File]]
        }

    }
    
}