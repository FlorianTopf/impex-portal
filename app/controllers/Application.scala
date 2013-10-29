package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws._
import play.api.libs.concurrent.Execution.Implicits._
import scala.collection.mutable.ListBuffer
import views.html._
import scala.xml._
import scalaxb._
import models.binding._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def config = Action {

    val config = scala.xml.XML.loadFile("conf/configuration.xml")

    val configuration = scalaxb.fromXML[Impexconfiguration](config)
    
    //println(configuration)
    
    val databases: ListBuffer[Database] = ListBuffer()
    val tools: ListBuffer[Tool] = ListBuffer()
    
    configuration.impexconfigurationoption.foreach(c => {
      //println(c.key.get +"="+ c.value)  
      c.value match {
        case x: Database => databases+=x; println(x)
        case x: Tool => tools+=x
      }
    })

    //Ok("OK: " + databases(1).databaseoption(0).value+databases(1).methods.get)
    Ok(views.html.config(databases.toList))
  }
  
  def tree = Action {
     val promise = WS.url("http://impex.latmos.ipsl.fr/tree.xml").get()
     //val promise = WS.url("http://impex-fp7.fmi.fi/ws/Tree_FMI_HYB.xml").get()
     //val promise = WS.url("http://dec1.sinp.msu.ru/~lucymu/paraboloid/SINP_tree.xml").get()
    
    Async {
            promise map { response => 
              
              val tree = scalaxb.fromXML[Spase](response.xml)
              //val simulationModel = response.xml \\ "SimulatioModel" \ "ResourceID"
              
              // @TODO how we can extract from the sequence with matching the data records name?
              // @TODO why must repository be casted to NodeSeq?
              //val repository = scalaxb.fromXML[Repository](tree.ResourceEntity(1).value.asInstanceOf[NodeSeq])
              
              val simulationModels: ListBuffer[SimulationModel] = ListBuffer()
              val simulationRuns: ListBuffer[SimulationRun] = ListBuffer()
              
              for(element <- tree.ResourceEntity) element.key match {
                case Some("SimulationModel") => simulationModels+=element.value.asInstanceOf[SimulationModel]
                //@TODO why must simulation run be casted to NodeSeq? 
                case Some("SimulationRun")  => simulationRuns+=scalaxb.fromXML[SimulationRun](element.value.asInstanceOf[NodeSeq])
                case _ => println("something else")
              } 
              
              println(simulationRuns(1))
              
              //Ok("OK: "+test.text+"<br/>"+
              Ok("OK: First model: "+simulationModels(0).ResourceID+"; "+
                  tree.ResourceEntity.length + " ResourceEntity elements found"+"; "+
                  //"RepositoryID="+repository.ResourceID+"; "+
                  simulationRuns.length+" SimulationRuns found ----->>>>>> "+ tree                   
              )
              
            }
     }
     
  } 
  
  def test = Action {
    val treeXml =
      <Spase xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://impex-fp7.oeaw.ac.at" xsi:schemaLocation="http://www.impex.latmos.ipsl.fr http://impex-fp7.oeaw.ac.at/fileadmin/impex+spase-1.0.xsd">
        <Version>2.2.2</Version>
        <SimulationModel>
          <ResourceID>impex://LATMOS/Hybrid</ResourceID>
          <ResourceHeader>
            <ResourceName>Hybrid_LATMOS</ResourceName>
            <ReleaseDate>2013-06-12T00:00:00.000</ReleaseDate>
            <Description>
              Hybrid simulation model developped at LATMOS for plasma interaction with celestial neutral environments (planets or moons)
            </Description>
            <Contact>
              <PersonID>LATMOS</PersonID>
              <Role>DataProducer</Role>
            </Contact>
            <InformationURL>
              <URL>http://impex.latmos.ipsl.fr</URL>
            </InformationURL>
          </ResourceHeader>
          <SimulationType>Hybrid</SimulationType>
          <CodeLanguage>Fortran2003</CodeLanguage>
        </SimulationModel>
        <Repository>
          <ResourceID>impex://LATMOS</ResourceID>
          <ResourceHeader>
            <ResourceName>Latmos_Hybrid_Simulation_Database</ResourceName>
            <ReleaseDate>2013-06-12T00:00:00.000</ReleaseDate>
            <Description/>
            <Contact>
              <PersonID>LATMOS</PersonID>
              <Role>DataProducer</Role>
            </Contact>
          </ResourceHeader>
          <AccessURL>
            <URL>http://impex.latmos.ipsl.fr/Hybrid</URL>
          </AccessURL>
        </Repository>
      </Spase>
      
     val treeObj = scalaxb.fromXML[Spase](treeXml)
    
     Ok("OK:" + treeObj)
  }
  
}