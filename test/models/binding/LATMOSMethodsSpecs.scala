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
           
           val variable = Seq("Bx", "By", "Btot") // variable seq
           
           val result = latmos.service.getDataPointValue(
               "impex://LATMOS/Hybrid/Mars_13_02_13/Mag/3D", // resourceId
               Some(variable), // variables
               new URI("http://impex.latmos.ipsl.fr/Vmvrv5e.xml"), // url_xyz
               Some(extraParams) // extra params
           )
          
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               // @FIXME not working because the file is empty
               //val result = Await.result(promise, Duration(1, "minute")).xml
               //scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
           
        }
        
    
        "respond to getDataPointValue_Spacecraft" in {
           
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
          
           val extraParams = ExtraParams_getDataPointValueLATMOS(
               Some(120.0), // imf clockangle
               Some(VOTableType) // output filetype
           )
           
           val variable = Seq("Ux", "Uy") // variable seq
           
           val result = latmos.service.getDataPointValue_Spacecraft(
               "impex://LATMOS/Hybrid/Mars_13_02_13/The/3D", // resourceId
               Some(variable), // variable
               MarsExpressValue, // spacecraft name
               TimeProvider.getISODate("2007-07-12T00:00:00"), // start time
               TimeProvider.getISODate("2007-07-13T00:00:00"), // stop time
               TimeProvider.getDuration(60), // sampling
               Some(extraParams)) // extra params
          
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               // @FIXME not working because the file is empty
               //val result = Await.result(promise, Duration(1, "minute")).xml
               //scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
           
        }
        
        
        "respond to getSurface" in {
          
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getSurfaceLATMOS(
               None, //resolution (TO BE TESTED)
               Some(0), // imf clockangle
               Some(VOTableType) // output filetype
           )
           
           val variable = Seq("Bx", "By", "Bz") // variable seq
           // @FIXME must this be a float seq?
           val planePoint = Seq(0.0f, 0.0f, 0.0f) // plane point seq
           // @FIXME must this be a float seq?
           val planeNormalVector = Seq(0.0f, 0.0f, 1.0f) // plane normal vector seq
           
           val result = latmos.service.getSurface(
               "impex://LATMOS/Hybrid/Mars_13_02_13/Mag/3D", // resourceId
               Some(variable), // variable
               planePoint, // plane point
               planeNormalVector, // plane normal vector
               Some(extraParams) // extra params
           )
          
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               // @FIXME not working because the file is empty
               //val result = Await.result(promise, Duration(1, "minute")).xml
               //scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
           })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
           
        }
        
    
        "respond to getFileURL" in {
          
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}

           val result = latmos.service.getFileURL(
               "impex://LATMOS/Hybrid/Mars_14_01_13/Mag/MEX/0.0", // resourceId
               TimeProvider.getISODate("2007-07-12T00:00:00"), // start time
               TimeProvider.getISODate("2007-07-13T00:00:00") // stop time
           )
          
           result.fold(f => println(f), xml => {
                // method immediately returns a VOTable file
                xml must beAnInstanceOf[VOTABLE]
           })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.VOTABLE]]
           result must beRight // result must be successful

        }
        
        
        "respond to getFieldLine" in {
           
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getFieldLineLATMOS(
        	   Some(Both), // direction
        	   None, // stepsize (TO BE TESTED)
               Some(VOTableType) // output filetype
           )
           
           val variable = Seq("Bx", "By", "Bz", "Btot")
          
           val result = latmos.service.getFieldLine(
               "impex://LATMOS/Hybrid/Mars_13_02_13/Mag/3D", // resourceId
               Some(variable), // variable
               new URI("http://impex.latmos.ipsl.fr/Vmvrv5e.xml"), // url_xyz
               Some(extraParams) // extra params
           )
           
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               // @FIXME not working because the file is empty
               //val result = Await.result(promise, Duration(1, "minute")).xml
               //scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
           })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
          
        }
        
        // NOT YET IMPLEMENTED
        /*"respond to getDataPointsSpectra" in {
          
          
        }*/
        
        // NOT YET IMPLEMENTED
        /*"respond to getDataPointsSpectra_Spacecraft" in {
          
          
        }*/
        
  }
  
}