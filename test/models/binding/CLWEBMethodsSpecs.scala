package models.binding

import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test._
import scalaxb._
import java.net.URI
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Await
import scala.concurrent.duration._


// @TODO no ICD existing
object CLWEBMethodsSpecs extends org.specs2.mutable.Specification with Mockito {
  
  "CLWEB Methods binding" should {
    
       "respond to getTimeTableList" in {
           
           val clweb = new Methods_CLWEBSoapBindings with Soap11Clients with DispatchHttpClients {}
                      
           val result = clweb.service.getTimeTablesList(Some("impex"), None)

           result.fold(f => println(f), u => {
             
               println("Result URL: "+u.TimeTablesList)
               u.success.get must beTrue
               u.TimeTablesList.get must beAnInstanceOf[URI]
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.GetTimeTablesListResponse]]
           result must beRight // result must be successful
           
        }
       
       
       "respond to getTimeTable" in {
           
           val clweb = new Methods_CLWEBSoapBindings with Soap11Clients with DispatchHttpClients {}
                      
           val result = clweb.service.getTimeTable(Some("impex"), None, "test1.xml")

           result.fold(f => println(f), u => {
               println("Result URL: "+u.ttFileURL)
               u.success.get must beTrue
               u.ttFileURL must beAnInstanceOf[URI]
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.GetTimeTableResponse]]
           result must beRight // result must be successful
           
        }
       
       
       "respond to getParameter" in {
           
           val clweb = new Methods_CLWEBSoapBindings with Soap11Clients with DispatchHttpClients {}
                      
           val result = clweb.service.getParameter(
               "2005-01-15T00:00", // starttime
               "2005-01-16T00:00", // stoptime
               "AC_EPHEMERIS_ORBIT_H0_SWE(20,21,22)", // parameter
               None, // sampling
               None, // userId
               None, // password
               Some(VOTableFormat), // output format
               None, // time format
               None) // gzip

           result.fold(f => println(f), u => {
               println("Result URLs: "+u.dataFileURLs)
               u.success.get must beTrue
               u.dataFileURLs must beAnInstanceOf[Seq[URI]]  
               u.dataFileURLs.map((u) => {
                 val promise = WS.url(u.toString).get()
                 val result = Await.result(promise, Duration(2, "minute")).xml
                 scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
               })
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.GetParameterResponse]]
           result must beRight // result must be successful
           
        }
       
       // not really needed in the API
       "respond to getObsDataTree" in {
           
           val clweb = new Methods_CLWEBSoapBindings with Soap11Clients with DispatchHttpClients {}
                      
           val result = clweb.service.getObsDataTree()

           result.fold(f => println(f), u => {
               println("Result URL: "+u.Tree)
               u.success.get must beTrue
               u.Tree must beAnInstanceOf[URI]
            })
          
           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], models.binding.GetObsDataTreeResponseCLWEB]]
           result must beRight // result must be successful
           
        }
       

  }
  
}