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
import scalaxb._
import java.net.URI


object SINPMethodsSpecs extends org.specs2.mutable.Specification with Mockito {
  
  // @TODO include test cases for only mandatory (but also with optional parameters)

  "SINP Methods binding" should {
        
        "respond to getDataPointValue" in {
          
        	val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
        	val result = sinp.service.getDataPointValue(
        	    "impex://SINP/NumericalOutput/Earth/2003-11-20UT12", // resourceId
        		None, // variable (TO BE TESTED EXTRA)
        		Some(new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_52points.vot")), // url_xyz
        		None // extra params (TO BE TESTED EXTRA)
            )
        		
            result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
        	result must beRight // result must be successful
        }
  }
  
}