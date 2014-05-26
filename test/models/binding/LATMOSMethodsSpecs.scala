package models.binding

import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test._
import scalaxb._
import java.net.URI
import models.provider.TimeProvider
import models.binding._
import play.api.libs.ws._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout
import scala.xml.NodeSeq


// all test parameters are taken from ICD v0.5 (25.5.2014)
object LATMOSMethodsSpecs extends org.specs2.mutable.Specification with Mockito {

  // @TODO include test cases for only mandatory (but also with optional parameters)
  
  "LATMOS Methods binding" should {
    
        "respond to getDataPointValue" in {
           
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
          
           val extraParams = ExtraParams_getDataPointValueLATMOS(
               Some(120.0), // imf clockangle
               Some(VOTableType) // output filetype
           )
           
           val variables = Seq("Bx", "By", "Btot") // variable seq
           
           val result = latmos.service.getDataPointValue(
               "impex://LATMOS/Hybrid/Mars_13_02_13/Mag/3D", // resourceId
               Some(variables), // variables
               new URI("http://impex.latmos.ipsl.fr/Vmvrv5e.xml"), // url_xyz
               Some(extraParams) // extra params
           )
          
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               // @FIXME not working because the file is not existing
               //val result = Await.result(promise, Duration(1, "minute")).xml
               //scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
           
        }
    
    
  }
  
}