package models.provider

import org.specs2.mutable._
import models.provider._
import play.api.test._
import play.api.test.Helpers._
import java.net.URI

object ProviderSpecs extends Specification {
  
  "PathProvider" should {
    "deconstruct simple path" in {
      val testPath = "/AMDA-NG/data/WSRESULT/getObsDataTree_LocalParams.xml"
      val folderName = "AMDA"
      
      val result = PathProvider.getPath("trees", folderName, testPath)
      val expectedResult = "trees/AMDA/getObsDataTree_LocalParams.xml"
      
      result must be equalTo expectedResult
    }
    
    "deconstruct GET path" in {
      val testPath = "AMDA_METHODS_WSDL.php?wsdl"
      val folderName = "AMDA"
      
      val result = PathProvider.getPath("methods", folderName, testPath)
      val expectedResult = "methods/AMDA/AMDA_methods.wsdl"
      
      result must be equalTo expectedResult
    }
  }
  
  "UrlProvider" should {
    "provide URL" in {
      val testPath = Seq("/AMDA-NG/data/WSRESULT/getObsDataTree_LocalParams.xml")
      val dnsName  = "cdpp1.cesr.fr"
      val protocol = "http"
        
      val result = UrlProvider.getUrls(protocol, dnsName, testPath)
      val expectedResult = Seq(new URI("http://cdpp1.cesr.fr/AMDA-NG/data/WSRESULT/getObsDataTree_LocalParams.xml"))
      
      result must be equalTo expectedResult 
    }
    
    "encode URI" in {
      val uri = new URI("impex://FMI")
      val expected = "impex___FMI"
      
      UrlProvider.encodeURI(uri) must be equalTo expected
    }
    
    "decode URI" in {
      val string = "impex___SINP_PMM"
      val expected = new URI("impex://SINP/PMM")
        
      UrlProvider.decodeURI(string) must be equalTo expected
    }
  }
}