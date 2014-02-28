import org.specs2.mutable._
import models.provider._
import play.api.test._
import play.api.test.Helpers._
import java.net.URI

object ProviderSpecs extends Specification {
  
  "PathProvider" should {
    "deconstruct path" in {
      val testPath = "/AMDA-NG/data/WSRESULT/getObsDataTree_LocalParams.xml"
      val folderName = "AMDA"
      
      val result = PathProvider.getPath("trees",testPath, folderName)
      val expectedResult = "trees/AMDA/getObsDataTree_LocalParams.xml"
      
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
    
  }
}