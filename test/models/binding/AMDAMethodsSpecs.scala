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


// all test parameters are taken from ICD v1.5 (5.9.2014)
object AMDAMethodsSpecs extends org.specs2.mutable.Specification with Mockito {
  
  "AMDA Methods binding" should {
    
       "respond to getTimeTableList" in {
           
           val amda = new Methods_AMDASoapBindings with Soap11Clients with DispatchHttpClients {}
                      
           val result = amda.service.getTimeTablesList(Some("impex"), None)

           result.fold(f => println(f), u => {
             
               println("Result URL: "+u.TimeTablesList)
               u.success.get must beTrue
               u.TimeTablesList.get must beAnInstanceOf[java.net.URI]
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.GetTimeTablesListResponse]]
           result must beRight // result must be successful
           
        }
       
       
       "respond to getTimeTable" in {
           
           val amda = new Methods_AMDASoapBindings with Soap11Clients with DispatchHttpClients {}
                      
           val result = amda.service.getTimeTable(Some("impex"), None, "sharedtt_0")

           result.fold(f => println(f), u => {
               println("Result URL: "+u.ttFileURL)
               u.success.get must beTrue
               u.ttFileURL must beAnInstanceOf[java.net.URI]
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.GetTimeTableResponse]]
           result must beRight // result must be successful
           
        }
       
       
       "respond to getParameterList" in {
           
           val amda = new Methods_AMDASoapBindings with Soap11Clients with DispatchHttpClients {}
                      
           val result = amda.service.getParameterList("impex", None)

           result.fold(f => println(f), u => {
               println("Result URL: "+u.ParameterList.LocalDataBaseParameters+' '+u.ParameterList.RemoteDataBaseParameters)
               u.success.get must beTrue
               u.ParameterList.LocalDataBaseParameters.get must beAnInstanceOf[java.net.URI]
               u.ParameterList.RemoteDataBaseParameters.get must beAnInstanceOf[java.net.URI]
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.GetParameterListResponse]]
           result must beRight // result must be successful
           
        }
       
       
       "respond to getParameter" in {
           
           val amda = new Methods_AMDASoapBindings with Soap11Clients with DispatchHttpClients {}
                      
           val result = amda.service.getParameter(
               "1996-01-15T00:00", // starttime
               "1996-01-16T00:00", // stoptime
               "b_it", // parameter
               None, // sampling
               None, // userId
               None, // password
               Some(VOTableFormat), // output format
               None, // time format
               None) // gzip

           result.fold(f => println(f), u => {
               println("Result URLs: "+u.dataFileURLs)
               u.success.get must beTrue
               u.dataFileURLs must beAnInstanceOf[Seq[java.net.URI]]  
               u.dataFileURLs.map((u) => {
                 val promise = WS.url(u.toString).get()
                 val result = Await.result(promise, Duration(2, "minute")).xml
                 scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
               })
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.GetParameterResponse]]
           result must beRight // result must be successful
           
        }
       
       
       "respond to getObsDataTree" in {
           
           val amda = new Methods_AMDASoapBindings with Soap11Clients with DispatchHttpClients {}
                      
           val result = amda.service.getObsDataTree()

           result.fold(f => println(f), u => {
               println("Result URL: "+u.WorkSpace.LocalDataBaseParameters+" "+u.WorkSpace.RemoteDataBaseParameters)
               u.success.get must beTrue
               u.WorkSpace.LocalDataBaseParameters.get must beAnInstanceOf[java.net.URI]
               u.WorkSpace.RemoteDataBaseParameters.get must beAnInstanceOf[java.net.URI]  
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.GetObsDataTreeResponseAMDA]]
           result must beRight // result must be successful
           
        }
       
       
        "respond to getOrbites" in {
          
           val amda = new Methods_AMDASoapBindings with Soap11Clients with DispatchHttpClients {}
           
           val result = amda.service.getOrbites(
               "2007-03-15T00:00", // starttime
               "2007-03-16T00:00",  // stoptime
               VEXType, // spacecraft
               VSO, // coordinate system
               Some(Rv), // units 
               Some(60), // sampling
               Some("impex"), // userId
               None, // password
               Some(VOTableFormat), // output format
               None, // time format
               None) // gzip
             
           result.fold(f => println(f), u => {
               println("Result URsL: "+u.dataFileURLs)  
               u.success.get must beTrue
               u.dataFileURLs must beAnInstanceOf[Seq[java.net.URI]]
               u.dataFileURLs.map((u) => {
                 val promise = WS.url(u.toString).get()
                 val result = Await.result(promise, Duration(2, "minute")).xml
                 scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
               })
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.GetOrbitesResponse]]
           result must beRight // result must be successful

        }
  }
  
}