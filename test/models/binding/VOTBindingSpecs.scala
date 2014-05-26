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


object VOTBindingSpecs extends org.specs2.mutable.Specification with Mockito {
  

    "VOTable Binding" should {
        
        "marshall VOTable XML files" in {
          
            val voTableSINP = scalaxb.fromXML[VOTABLE](scala.xml.XML.loadFile("mocks/points_calc_52points.xml"))
            val voTableLATMOS = scalaxb.fromXML[VOTABLE](scala.xml.XML.loadFile("mocks/latmos_orbite_mex.xml"))
            val voTableFMI = scalaxb.fromXML[VOTABLE](scala.xml.XML.loadFile("mocks/Field_line.xml"))
            val voTableSINP2 = scalaxb.fromXML[VOTABLE](scala.xml.XML.loadFile("mocks/sinp_orbite_600sec.xml"))
            val voTableLATMOS2 = scalaxb.fromXML[VOTABLE](scala.xml.XML.loadFile("mocks/latmos_orbite_60sec.xml"))
            val voTableFMI2 = scalaxb.fromXML[VOTABLE](scala.xml.XML.loadFile("mocks/fmi_hyb_orbite_60sec.xml")) 
            val voTableSINP3 = scalaxb.fromXML[VOTABLE](scala.xml.XML.loadFile("mocks/getDPV_2003-11-20UT12_340.401.xml"))
            val voTableSINP4 = scalaxb.fromXML[VOTABLE](scala.xml.XML.loadFile("mocks/points_calc_120points.xml"))
            
            voTableSINP must beAnInstanceOf[VOTABLE]
            scalaxb.toXML[VOTABLE](voTableSINP, "VOTABLE", 
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")) must beAnInstanceOf[NodeSeq]
            voTableLATMOS must beAnInstanceOf[VOTABLE]
            scalaxb.toXML[VOTABLE](voTableLATMOS, "VOTABLE", 
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")) must beAnInstanceOf[NodeSeq]
            voTableFMI must beAnInstanceOf[VOTABLE]
            scalaxb.toXML[VOTABLE](voTableFMI, "VOTABLE", 
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")) must beAnInstanceOf[NodeSeq]
            voTableSINP2 must beAnInstanceOf[VOTABLE]
            scalaxb.toXML[VOTABLE](voTableSINP2, "VOTABLE", 
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")) must beAnInstanceOf[NodeSeq]
            voTableLATMOS2 must beAnInstanceOf[VOTABLE]
            scalaxb.toXML[VOTABLE](voTableLATMOS2, "VOTABLE", 
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")) must beAnInstanceOf[NodeSeq]
            voTableFMI2 must beAnInstanceOf[VOTABLE]
            scalaxb.toXML[VOTABLE](voTableFMI2, "VOTABLE", 
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")) must beAnInstanceOf[NodeSeq]
            voTableSINP3 must beAnInstanceOf[VOTABLE]
            scalaxb.toXML[VOTABLE](voTableSINP3, "VOTABLE", 
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")) must beAnInstanceOf[NodeSeq]
            voTableSINP4 must beAnInstanceOf[VOTABLE]
            scalaxb.toXML[VOTABLE](voTableSINP4, "VOTABLE", 
                scalaxb.toScope(None -> "http://www.ivoa.net/xml/VOTable/v1.2")) must beAnInstanceOf[NodeSeq]
            
        }

    }
    
}