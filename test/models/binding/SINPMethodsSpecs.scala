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
import models.provider.TimeProvider
import models.binding._

// all test parameters are taken from ICD v0.6.1 (24.5.2014)
object SINPMethodsSpecs extends org.specs2.mutable.Specification with Mockito {
  
  // @TODO include test cases for only mandatory (but also with optional parameters)

  "SINP Methods binding" should {
        
        "respond to getDataPointValue" in {
        	
            val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
        	
            val result = sinp.service.getDataPointValue(
        	    "impex://SINP/NumericalOutput/Earth/2003-11-20UT12", // resourceId
        		None, // variable (TO BE TESTED SEPARATELY)
        		// @TODO url_xyz should be mandatory? (check WSDL)
        		Some(new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_52points.vot")), // url_xyz
        		None // extra params (TO BE TESTED SEPARATELY)
            )
        		
            result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
        	result must beRight // result must be successful
        }
        
        "respond to calculateDataPointValueFixedTime" in {
            
          val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
            val imf_b = ListOfDouble(
                Some(-1.0), // x
                Some(4.0), // y
                Some(1.0) // z
            ) 
          
            val extraParams = ExtraParams_calculateDataPointValueFixedTime(
                Some(4.0), // sw density
                // @TODO why is this a big int? (check WSDL)
                Some(BigInt(400)), // sw velocity 
                Some(imf_b), // imf b 
                Some(30.0), // dst
                Some(150.0), // al
                None // output filetype
            )
        	
            val result = sinp.service.calculateDataPointValueFixedTime(
                "impex://SINP/NumericalOutput/Earth/OnFly", // resourceId
                TimeProvider.getISODate("2012-03-08 14:06:00"), 
                Some(extraParams), // extra params (TO BE TESTED SEPARATELY)
                new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot"))
        		
            result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
        	result must beRight // result must be successful
          
        }
        
        "respond to calculateDataPointValue" in {
            
           val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
          
           val result = sinp.service.calculateDataPointValue(
               "impex://SINP/SimulationModel/Earth/OnFly", // resourceId
                None, // extra params (TO BE TESTED EXTRA)
                new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot") // url_xyz  
           )
        		
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
        } 
        
        /*"respond to calculateDataPointValue_Spacecraft" in { 
          
           val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
          
           val result = sinp.service.calculateDataPointValueSpacecraft(
               "impex://SINP/SimulationModel/Earth/OnFly", 
               ClusterA, 
               TimeProvider.getISODate("2010-01-12 13:00:00"),  
               TimeProvider.getISODate("2010-01-13 03:45:00"),
               // @TODO "real" duration might not be the case here (check WSDL and ask)
               TimeProvider.getDuration(600), 
               None
           )
        		
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
        }*/
        
        
  }
  
}