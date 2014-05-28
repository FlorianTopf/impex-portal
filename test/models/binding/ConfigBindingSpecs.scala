package models.binding


import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test._
import scala.xml.NodeSeq


object ConfigBindingSpecs extends org.specs2.mutable.Specification with Mockito {
  
  
    "Config Binding" should {
       
      "marshall the config file" in {
      
        val config = scalaxb.fromXML[Impexconfiguration](scala.xml.XML.loadFile("conf/configuration.xml"))
        
        config must beAnInstanceOf[Impexconfiguration]
        scalaxb.toXML[Impexconfiguration](config, "impexconfiguration", 
            scalaxb.toScope(None -> "http://www.impex.org/2012/configuration.xsd")) must beAnInstanceOf[NodeSeq]
        
        
      }
      
      
    }
  

}