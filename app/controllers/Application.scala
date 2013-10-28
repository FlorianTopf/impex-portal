package controllers

import play.api._
import play.api.mvc._
import models._
import models.XMLProtocol._
import play.api.libs.ws._
import play.api.libs.concurrent.Execution.Implicits._
import scala.collection.mutable.ListBuffer
import views.html._
import scala.xml._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def test = Action {

    val config = scala.xml.XML.loadFile("conf/configuration.xml")

    val configuration = scalaxb.fromXML[models.Impexconfiguration](config)
    
    //println(configuration)
    
    val databases: ListBuffer[models.Database] = ListBuffer()
    val tools: ListBuffer[models.Tool] = ListBuffer()
    
    configuration.impexconfigurationoption.foreach(c => {
      //println(c.key.get +"="+ c.value)  
      c.value match {
        case x: Database => databases+=x
        case x: Tool => tools+=x
      }
      
    })
    

    //Ok("OK: " + databases(1).databaseoption(0).value+databases(1).methods.get)
    Ok(views.html.config(databases.toList))
  }
  
  def tree = Action {
     val promise = WS.url("http://impex.latmos.ipsl.fr/tree.xml").get()
    
    Async {
            promise map { response => 
              
              val test = response.xml \\ "SimulationModel"
              
              Ok("OK: " + test)
            }
     }
  } 
  
}