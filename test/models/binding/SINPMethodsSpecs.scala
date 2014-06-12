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


// all test parameters are taken from ICD v0.7 (5.6.2014)
object SINPMethodsSpecs extends org.specs2.mutable.Specification with Mockito {

  // @TODO include test cases for only mandatory (but also with optional parameters)

  "SINP Methods binding" should {
        
        "respond to getDataPointValue" in {
        	
            val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
        	
            val extraParams = ExtraParams_getDataPointValueSINP(
                Some(VOTableType), // output filetype
                None // interpolation method (TO BE TESTED)
            )
            
            val variable = Seq("Bx", "By") // variable seq
            
            val result = sinp.service.getDataPointValue(
        	    "impex://SINP/NumericalOutput/Earth/2003-11-20UT12", // resourceId
        		Some(variable), // variables (@FIXME NOT WORKING)
        		Some(new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_52points.vot")), // url_xyz
        		Some(extraParams) // extra params
            )
        		
            result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               val result = Await.result(promise, Duration(1, "minute")).xml
               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
            })
            
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
                Some(400.0), // sw velocity 
                Some(imf_b), // imf b 
                Some(30.0), // dst
                Some(150.0), // al
                Some(VOTableType) // output filetype
            )
        	
            val result = sinp.service.calculateDataPointValueFixedTime(
                "impex://SINP/NumericalOutput/Earth/OnFly", // resourceId
                TimeProvider.getISODate("2012-03-08T14:06:00"), // start time
                Some(extraParams), // extra params
                new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot")) // url_xyz
                
            result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               val result = Await.result(promise, Duration(1, "minute")).xml
               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
            })
        		
            result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
        	result must beRight // result must be successful
          
        }
        
        
        "respond to calculateDataPointValue" in {
            
           val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_calculateDataPointValue(
               Some(VOTableType) // output filetype
           )
          
           val result = sinp.service.calculateDataPointValue(
               "impex://SINP/SimulationModel/Earth/OnFly", // resourceId
                Some(extraParams), // extra params
                new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot") // url_xyz  
           )
           
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               val result = Await.result(promise, Duration(1, "minute")).xml
               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
           })
        		
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
           
        }
        

        "respond to calculateDataPointValueSpacecraft" in { 
          
           val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_calculateDataPointValueSpacecraft(
               Some(VOTableType) // output filetype
           )
          
           val result = sinp.service.calculateDataPointValueSpacecraft(
               "impex://SINP/SimulationModel/Earth/OnFly", // resourceId
               CLUSTER1, // spacecraft name
               TimeProvider.getISODate("2010-01-12T13:00:00"), // start time
               TimeProvider.getISODate("2010-01-13T03:45:00"), // stop time
               TimeProvider.getDuration("PT600S"), // sampling
               Some(extraParams) // extra params
           )
           
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               val result = Await.result(promise, Duration(1, "minute")).xml
               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
           })
        		
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
           
        }
        
    
	  	"respond to calculateFieldLine" in {
	  	  
	  	  val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	  
	  	  val extraParams = ExtraParams_calculateFieldLine(
	  	      None, // line length (TO BE TESTED)
	  	      None, // step size (TO BE TESTED)
	  	      Some(VOTableType) // output filetype
	  	  )
	  	  
	  	  val result = sinp.service.calculateFieldLine(
	  	      "impex://SINP/SimulationModel/Earth/OnFly", // resourceId
	  	      TimeProvider.getISODate("2010-01-12T13:00:00"), // start time
	  	      Some(extraParams), // extra params
	  	      new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot") // url_xyz
	  	  )
	  	  
          result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               val result = Await.result(promise, Duration(1, "minute")).xml
               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
          })
	  	  
	  	  result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  	  result must beRight // result must be sucessful
	  	 
	  	}
	  	
	  	
	  	"respond to calculateCube" in {
	  	  
	  	  val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	  
	  	  val imf_b = ListOfDouble(
              Some(5.1), // x
              Some(-8.9), // y
              Some(4.5) // z
          ) 
	  	  
	  	  val extraParams = ExtraParams_calculateCube(
	  	      Some(5.0), // sw density
	  	      Some(800.0), // sw velocity
	  	      Some(imf_b), // imf b
	  	      Some(-23.0), // dst
	  	      Some(-1117.0), // al
	  	      Some(VOTableType) // output filetype
	  	  )
	  	  
	  	  val cubeSize = Cube_size_array(
	  	      Some(BigInt(-40)), // x_low
	  	      Some(BigInt(10)), // x_high
	  	      Some(BigInt(-15)), // y_low
	  	      Some(BigInt(15)), // y_high
	  	      Some(BigInt(-10)), // z_low
	  	      Some(BigInt(10)) // z_high
	  	  )
	  	  
	  	  val result = sinp.service.calculateCube(
	  	      "impex://SINP/NumericalOutput/Earth/OnFly", // resourceId
	  	      TimeProvider.getISODate("2005-09-11T02:00:00"), // start time 
	  	      Some(extraParams), // extra params
	  	      Some(0.7), // sampling 
	  	      Some(cubeSize) // cube size
	  	  )
	  	      
          result.fold(f => println(f), u => {
              println("Result URL: "+u)
              //val promise = WS.url(u.toString).get()
              //val result = Await.result(promise, Duration(1, "minute")).xml
              //scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
          })
	  	  
	  	  result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  	  result must beRight // result must be sucessful
	  	  
	  	}
	  	
	  	
	  	"respond to calulateCubeMercury" in {
	  	  
	  	  val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	  
	  	  val imf_b = ListOfDouble(
              Some(5.1), // x
              Some(-8.9), // y
              Some(4.5) // z
          ) 
	  	  
	  	  val extraParams = ExtraParams_calculateCubeMercury(
	  	      Some(196.0), // bd
	  	      Some(4.0), // flux
	  	      Some(1.5), // rss
	  	      Some(1.5), // r2
	  	      Some(0.0), // dz
	  	      Some(imf_b), // imf b
	  	      Some(VOTableType) // output filetype
	  	  )
	  	  
	  	  val result = sinp.service.calculateCubeMercury(
	  	      "impex://SINP/SimulationModel/Mercury/OnFly", // resourceId
	  	      Some(extraParams) // extra params
	  	  )
	  	  
          result.fold(f => println(f), u => {
              println("Result URL: "+u)
              //val promise = WS.url(u.toString).get()
              //val result = Await.result(promise, Duration(1, "minute")).xml
              //scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
          })
	  	  
	  	  result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  	  result must beRight // result must be sucessful
	  	  
	  	}
        
	  	
	  	"respond to calculateDataPointValueMercury" in {
	  	  
	  	  val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	  
	  	  val imf_b = ListOfDouble(
	  	      Some(5.1), // x
              Some(-8.9), // y
              Some(4.5) // z
          ) 
          
          val extraParams = ExtraParams_calculateDataPointValueMercury(
              Some(VOTableType), // output filetype
              Some(-196.0), // bd
              Some(131.0), // flux
              Some(1.35), // rss
              Some(1.32), // r2
              Some(0.0), // dz
              Some(imf_b) // imf b
          )
	  	  
	  	  val result = sinp.service.calculateDataPointValueMercury(
	  	      "impex://SINP/SimulationModel/Mercury/OnFly", // resourceId
	  	      Some(extraParams), // extra params
	  	      new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/XYZ_calcDPVMercury.vot") // url_xyz
	  	   )
	  	   
          result.fold(f => println(f), u => {
              println("Result URL: "+u)
              val promise = WS.url(u.toString).get()
              val result = Await.result(promise, Duration(1, "minute")).xml
              scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
          })
	  	   
	  	  result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  	  result must beRight // result must be sucessful
	  	  
	  	}
	  	
    
	  	"respond to calculateCubeSaturn" in {
	  	   
	  	   val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	   
	  	   val imf_b = ListOfDouble(
	  	      Some(0.0), // x
              Some(0.0), // y
              Some(0.0) // z
           ) 
           
           val extraParams = ExtraParams_calculateCubeSaturn(
	  	       Some(3.0), // bdc
	  	       Some(-7.0), // bt
	  	       Some(6.5), // rd2
	  	       Some(15.0), // rd1
	  	       Some(18.0), // r2
	  	       Some(22.0), // rss
	  	       Some(imf_b), // imf b
	  	       Some(VOTableType) // output filetype
	  	   )
	  	   
	  	  val cubeSize = Cube_size_array(
	  	      Some(BigInt(-6)), // x_low
	  	      Some(BigInt(7)), // x_high
	  	      Some(BigInt(-3)), // y_low
	  	      Some(BigInt(3)), // y_high
	  	      Some(BigInt(-3)), // z_low
	  	      Some(BigInt(3)) // z_high
	  	  )
	  	   
	  	   val result = sinp.service.calculateCubeSaturn(
	  	       "impex://SINP/SimulationModel/Saturn/OnFly", // resourceId
	  	       TimeProvider.getISODate("2008-09-10T12:00:00"),  // start time
	  	       Some(extraParams), // extra params
	  	       Some(1.0), // sampling 
	  	       Some(cubeSize) // cube size
	  	   )
	  	   
           result.fold(f => println(f), u => {
              println("Result URL: "+u)
              //val promise = WS.url(u.toString).get()
              //val result = Await.result(promise, Duration(1, "minute")).xml
              //scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
           })
	  	   
	  	   result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  	   result must beRight // result must be sucessful
           
	  	}
	  	
    
	  	"respond to calculateDataPointValueSaturn" in {
	  	  
	  	   val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	   
	  	   val imf_b = ListOfDouble(
	  	      Some(0.0), // x
              Some(0.0), // y
              Some(0.0) // z
           ) 
	  	   
	  	   val extraParams = ExtraParams_calculateDataPointValueSaturn(
	  	       Some(VOTableType), // output filetype
	  	       Some(3.0), // bdc
	  	       Some(-7.0), // bt
	  	       Some(6.5), // rd2
	  	       Some(15.0), // rd1
	  	       Some(18.0), // r2
	  	       Some(22.0), // rss
	  	       Some(imf_b) // imf b
	  	   )
	  	   
	  	   val result = sinp.service.calculateDataPointValueSaturn(
	  	       "impex://SINP/SimulationModel/Saturn/OnFly", // resourceId
	  	       Some(extraParams), // extra params
	  	       new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot") // url_xyz
	  	   )
	  	
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               val result = Await.result(promise, Duration(1, "minute")).xml
               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
           })
	  	   
	  	   result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  	   result must beRight // result must be sucessful
	  	
	  	}
	  	
  }
  
}