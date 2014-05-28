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


// all test parameters are taken from ICD v0.6.1 (24.5.2014)
object FMIMethodsSpecs extends org.specs2.mutable.Specification with Mockito {
  
  // @TODO include test cases for only mandatory (but also with optional parameters)
  
  "FMI Methods binding" should {
    
        "respond to getDataPointValue" in {
           
           val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getDataPointValueFMI(
               None, // interpolation method (TO BE TESTED)
               Some(VOTableType) // output filetype
           )
           
           val variables = Seq("Bx", "Btot") // variable seq
           
           val result = fmi.service.getDataPointValue(
               "impex://FMI/HWA/HYB/mars/spiral_angle_runset_20130607_mars_20deg/Mag", // resourceId
               Some(variables), // variables
               new URI("http://impex-fp7.fmi.fi/ws_tests/input/getDataPointValue_input.vot"), // url_xyz
               Some(extraParams) // extra params
           )
           
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               //val promise = WS.url(u.toString).get()
               // @FIXME not working because it VOTABLE file is v1.1
               //val result = Await.result(promise, Duration(1, "minute")).xml
               //scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
           
        }
    
    
  }
  
}