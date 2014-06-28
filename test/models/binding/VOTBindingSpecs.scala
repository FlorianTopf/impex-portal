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
import java.security.SecureRandom
import scala.math._
import models.provider.TimeProvider


object VOTBindingSpecs extends org.specs2.mutable.Specification with Mockito {
  

    "VOTable Binding" should {
        
        "marshall random VOTable 1.2 files" in {
          
           val testFiles = new File("mocks/").listFiles.filter(_.getName.endsWith(".xml"))
           
           testFiles map { f => { 
             val votable = scalaxb.fromXML[VOTABLE](scala.xml.XML.loadFile(f))
             val json = Json.toJson(votable)
             
             votable must beAnInstanceOf[VOTABLE]
             scalaxb.toXML[VOTABLE](votable, "VOTABLE", 
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")) must beAnInstanceOf[NodeSeq]
             
             json must beAnInstanceOf[JsValue]
             json.as[VOTABLE] must beAnInstanceOf[VOTABLE]
           }}
            
           testFiles must beAnInstanceOf[Array[File]]
        }

    }
    
    "create VOTable fields for getMostRelevantRun in JSON" in {
      
       val count = 5
       val radius = 6.371e6
       val random = SecureRandom.getInstance("SHA1PRNG", "SUN")
       random.nextBytes(Array[Byte](20))
       val times = for (i <- (0 to count).toList) yield { TimeProvider.getISONow }
       val x: List[Double] = for (i <- (0 to count).toList) yield (radius*cos(2*Pi*i/count))
       val y: List[Double] = for (i <- (0 to count).toList) yield (radius*sin(2*Pi*i/count))
       val z: List[Double] = for (i <- (0 to count).toList) yield (radius*tan(2*Pi*i/count))
       val accl: List[Int] = for (i <- (0 to count).toList) yield (random.nextInt(count))
       val fields = Seq(
           VOTable_field(times.map(_.toString), "Time"),
           VOTable_field(x.map(_.toString), "X"),
           VOTable_field(y.map(_.toString), "Y"),
           VOTable_field(z.map(_.toString), "Z"),
           VOTable_field(accl.map(_.toString), "Accl", None,
         		   Some("m/s^2"),
           		   Some(DoubleType) ,
           		   None,
           		   Some("phys.acceleration")
           )
       )
       
       val voTableURL = VOTableURL(Some("My test run"), Some("VOTable format demo"), fields)
       val json = Json.toJson(voTableURL)

       json must beAnInstanceOf[JsValue]
       json.as[VOTableURL] must beAnInstanceOf[VOTableURL]
    }
    
}