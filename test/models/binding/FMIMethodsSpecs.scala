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
import scala.math._
import java.security.SecureRandom
import play.api.libs.json._


// all test parameters are taken from ICD v0.9 (4.6.2014)
object FMIMethodsSpecs extends org.specs2.mutable.Specification with Mockito {
  
  "FMI Methods binding" should {
    
        "respond to getDataPointValue" in {
           
           val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getDataPointValueFMI(
               Some(LinearValue), // interpolation method
               Some(VOTableType) // output filetype
           )
           
           val variable = Seq("Bx", "Btot") // variable seq
           
           val result = fmi.service.getDataPointValue(
               "impex://FMI/NumericalOutput/HYB/mars/spiral_angle_runset_20130607_mars_20deg/Mag", // resourceId
               Some(variable), // variable
               new URI("http://impex-fp7.fmi.fi/ws_tests/input/getDataPointValue_input.vot"), // url_xyz
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
        
        
        "respond to getDataPointValueSpacecraft" in {
           
           val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getDataPointValueFMI(
               Some(LinearValue), // interpolation method
               Some(VOTableType) // output filetype
           )
          
           val variable = Seq("Density") // variable seq
           
           val result = fmi.service.getDataPointValueSpacecraft(
               // resourceId
               "impex://FMI/NumericalOutput/GUMICS/earth/synth_stationary/solarmin/EARTH___n_T_Vx_Bx_By_Bz__7_100_600_3p_03_15m/tilt15p/H+_mstate", 
               Some(variable), // variable 
               CLUSTER3Value, // spacecraft name
               TimeProvider.getISODate("2010-08-02T00:00:00"), // start time
               TimeProvider.getISODate("2010-08-02T01:00:00"), // stop time
               TimeProvider.getDuration("PT60S"), // sampling
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
        
        
        "respond to getSurface" in {
          
           val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getSurfaceFMI(
               None, // resolution (@TODO TO BE TESTED)
               Some(VOTableType), // output filetype
               Some(LinearValue) // interpolation method
           )
           
           val variable = Seq("Density") // variable seq
           
           val planePoint = Seq(1.0f, 0f, 0f) // plane point
           
           val planeNormalVector = Seq(3.7e6f, 0f, 0f) // plane normal vector
           
           val result = fmi.service.getSurface(
               "impex://FMI/NumericalOutput/HYB/mars/Mars_testrun_lowres/O+_ave_hybstate", // resoureId
               Some(variable), // variable
               planePoint, // plane point
               planeNormalVector, // plane normal vector
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
        
        
        "respond to getVOTableURL" in {
          
           val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}   
           
           val count = 1000
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
           
           val result = fmi.service.getVOTableURL(Some("My test run"), Some("VOTable format demo"), fields)
           
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               val result = Await.result(promise, Duration(1, "minute")).xml
               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
            })
            
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
          
        }
        
        
        "respond to getMostRelevantRun" in {
          
           val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val swParameters = SW_parameter_list(
               Some(SW_parameter( 
                   5e6, // value
                   Some(2), // weight
                   None, // scale
                   None // function
               )), // sw density
               Some(SW_parameter(
                   4.5e5, // value
                   Some(1), // weight
                   None, // scale
                   None // function
               )), // sw Utot
               None, // sw temperature
               None, // sw Btot
               None, // sw Bx
               None, //  sw By
               None, // sw Bz
               None, // solar F10.7
               Some(SW_parameter(
                   0.5, // value
                   None, // weight
                   Some(1), // scale
                   Some("abs(SW_Bx/SW_Btot)") // function
               )) // sw function
           )
           
           val result = fmi.service.getMostRelevantRun(
               Earth, // object value (enumRegion)
               Some(BigInt(2)), // run count
               swParameters // sw parameters
           ) 

           result.fold(f => println(f), u => {
               //println("Result URL: "+u)
               // result is a json string so we try this:
               Json.parse(u) must beAnInstanceOf[JsValue]
            })
            
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], String]]
           result must beRight // result must be successful

        }
    
         
        "respond to getFieldLine" in {
          
           val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
           
           // this should be a list of floats
           //val stopConditionRegion = Seq("x_min", "x_max", "y_min", "y_max", "z_min", "z_max")
           
           val extraParams = ExtraParams_getFieldLineFMI(
               Some(Forward), // direction
               None, // step size (@TODO TO BE TESTED)
               Some(BigInt(100)), // max steps
               Some(0), // stop condition radius
               None, //Some(stopConditionRegion), // stop condition region (@TODO TO BE TESTED)
        	   Some(VOTableType) // output filetype
           )
          
           val result = fmi.service.getFieldLine(
               "impex://FMI/NumericalOutput/HYB/mars/spiral_angle_runset_20130607_mars_90deg/Mag", // resourceId
               None, // variable (not supported ATM) 
               new URI("http://impex-fp7.fmi.fi/ws_tests/input/getFieldLine_input.vot"), // url_xyz
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
        
        
        "respond to getParticleTrajectory" in {
          
           val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getParticleTrajectory(
               Some(Forward), // direction
               Some(1), // step size
               Some(BigInt(200)), // max steps
               Some(0), // stop condition radius
               None, // stop condition region (@TODO TO BE TESTED)
               None, // interpolation method (@TODO TO BE TESTED)
               Some(VOTableType) // output filetype
           )
          
           val result = fmi.service.getParticleTrajectory(
               "impex://FMI/NumericalOutput/HYB/mars/spiral_angle_runset_20130607_mars_90deg/Mag", // resourceId
               new URI("http://impex-fp7.fmi.fi/ws_tests/input/getParticleTrajectory_input.vot"), // url_xyz
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
        
        
        "respond to getDataPointSpectra" in {
          
           val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getDataPointSpectraFMI(
               None, // interpolation method (@TODO TO BE TESTED)
               Some(VOTableType), // output filetype
               None // energy channel (@TODO BE TESTED)
           )
           
           val result = fmi.service.getDataPointSpectra(
               "impex://FMI/NumericalOutput/HYB/venus/run01_venus_nominal_spectra_20140417/H+_spectra", // resourceId
               new URI("http://impex-fp7.fmi.fi/ws_tests/input/getDataPointSpectra_input.vot"), // url_xyz
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
        
        
        "respond to getDataPointSpectraSpacecraft" in {
          
           val fmi = new Methods_FMISoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getDataPointSpectraFMI(
               None, // interpolation method (@TODO TO BE TESTED)
               Some(VOTableType), // output filetype
               None // energy channel (@TODO TO BE TESTED)
           )
           
           val result = fmi.service.getDataPointSpectraSpacecraft(
               "impex://FMI/NumericalOutput/HYB/venus/run01_venus_nominal_spectra_20140417/H+_spectra", // resourceId
               VEXValue, // spacecraft name
               TimeProvider.getISODate("2010-08-02T06:00:00"), // start time
               TimeProvider.getISODate("2010-08-02T09:00:00"), // stop time
               TimeProvider.getDuration("PT60S"), // sampling
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
    
    
	  	"respond to isAlive" in {
	  	  
	  	   val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	   
	  	   val result = sinp.service.isAlive()
	  	   
	  	   result.fold(f => println(f), b => b must beTrue)
	  	   result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], Boolean]]
	  	   result must beRight
	  	}
        
  }
  
}