package models.binding

import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test._
import scalaxb._
import java.net.URI
import models.provider.TimeProvider
import models.binding._
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout
import scala.xml.NodeSeq


// all test parameters are taken from ICD v0.5 (25.5.2014)
object LATMOSMethodsSpecs extends org.specs2.mutable.Specification with Mockito {
  
  "LATMOS Methods binding" should {
    
       "respond to getDataPointValue" in {
           
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
          
           val extraParams = ExtraParams_getDataPointValueLATMOS(
               Some(120.0), // imf clockangle
               Some(VOTableType) // output filetype
           )
           
           val variable = Seq("Bx", "By", "Btot") // variable seq
           
           val result = latmos.service.getDataPointValue(
               "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_13_02_13/Mag/3D", // resourceId
               Some(variable), // variable
               new URI("http://impex.latmos.ipsl.fr/Vmvrv5e.xml"), // url_xyz
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
           
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
          
           val extraParams = ExtraParams_getDataPointValueLATMOS(
               Some(120.0), // imf clockangle
               Some(VOTableType) // output filetype
           )
           
           val variable = Seq("Ux", "Uy") // variable seq
           
           val result = latmos.service.getDataPointValueSpacecraft(
               "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_13_02_13/The/3D", // resourceId
               Some(variable), // variable
               MEXValue, // spacecraft name
               TimeProvider.getISODate("2007-07-12T00:00:00"), // start time
               TimeProvider.getISODate("2007-07-13T00:00:00"), // stop time
               TimeProvider.getDuration("PT60S"), // sampling
               Some(extraParams)) // extra params
          
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
          
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getSurfaceLATMOS(
               None, // resolution (@TODO TO BE TESTED)
               Some(0), // imf clockangle
               Some(VOTableType) // output filetype
           )
           
           val variable = Seq("Bx", "By", "Bz") // variable seq

           val planePoint = Seq(0.0f, 0.0f, 0.0f) // plane point seq

           val planeNormalVector = Seq(0.0f, 0.0f, 1.0f) // plane normal vector seq
           
           val result = latmos.service.getSurface(
               "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_13_02_13/Mag/3D", // resourceId
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
        
    
        "respond to getFileURL" in {
          
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}

           val result = latmos.service.getFileURL(
               "spase://IMPEX/Granule/LATMOS/Hybrid/Mars_14_01_13/Mag/MEX/0.0", // resourceId
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
        	   None, // stepsize (@TODO TO BE TESTED)
               Some(VOTableType) // output filetype
           )
           
           val variable = Seq("Bx", "By", "Bz", "Btot")
          
           val result = latmos.service.getFieldLine(
               "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_13_02_13/Mag/3D", // resourceId
               Some(variable), // variable
               new URI("http://impex.latmos.ipsl.fr/Vmvrv5e.xml"), // url_xyz
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
                     
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getDataPointSpectraLATMOS(
               None, // imf clockangle (@TODO TO BE TESTED)
               Some(VOTableType), // output filetype
               None // energy channel (@TODO TO BE TESTED)
           )
           
           val result = latmos.service.getDataPointSpectra(
               "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_14_03_14/IonSpectra", // resourceId
               new URI("http://impex.latmos.ipsl.fr/Vmrmf2.xml"), // url_xyz
               Some(extraParams) // extra params
           )
           
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               val result = Await.result(promise, Duration(2, "minute")).xml
               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
           })
           
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
           
        }
        

        "respond to getDataPointSpectraSpacecraft" in {
          
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val extraParams = ExtraParams_getDataPointSpectraLATMOS(
               None, // imf clockangle (@TODO TO BE TESTED)
               Some(VOTableType), // output filetype
               None // energy channel (@TODO TO BE TESTED)
           )
           
           val result = latmos.service.getDataPointSpectraSpacecraft(
               "spase://IMPEX/NumericalOutput/LATMOS/Hybrid/Mars_14_03_14/IonSpectra", // resourceId
               MEXValue,  // spacecraft name
               TimeProvider.getISODate("2010-01-01T18:00:00"), // start time
               TimeProvider.getISODate("2010-01-01T19:00:00"), // stop time
               TimeProvider.getDuration("PT60S"), // sampling
               Some(extraParams) // extra params
           )
           
           result.fold(f => println(f), u => {
               println("Result URL: "+u)
               val promise = WS.url(u.toString).get()
               val result = Await.result(promise, Duration(2, "minute")).xml
               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
           })
           
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
           result must beRight // result must be successful
           
        }
        
        
        "respond to isAlive" in {
          
           val latmos = new Methods_LATMOSSoapBindings with Soap11Clients with DispatchHttpClients {}
          
           val result = latmos.service.isAlive()
	  	   
	  	   result.fold(f => println(f), b => b must beTrue)
	  	   result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], Boolean]]
	  	   result must beRight
	  	   
	  	}
        
  }
  
}