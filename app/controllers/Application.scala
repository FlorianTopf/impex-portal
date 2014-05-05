package controllers

import models.actor._
import models.binding._
import models.actor.ConfigService._
import models.enums._
import views.html._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import play.libs.Akka
import scala.xml._
import scala.collection.mutable.ListBuffer
import akka.actor._
import akka.pattern.ask
import java.net.URI
import javax.xml.datatype._


object Application extends Controller {
  
  // route for portal client
  def index = Action { Ok(views.html.portal()) }
  
  // route for swagger ui
  def apiview = Action { Ok(views.html.apiview()) }
  
  // route for testing
  def test = PortalAction {
    import models.actor.DataProvider._
 /*   val future = RegistryService.getSimulationRun(Some("impex://FMI"), "false")
    future.map { _ match {
      case Left(tree) => {
        Ok(Json.toJson(tree))
      }
      case Right(error) => BadRequest(Json.toJson(error))
    }}
  } */
   val actorSel = Akka.system.actorSelection("user/registry/impex___CLWEB")
   //val future = (actorSel ? GetElement(EInstrument, None)).mapTo[Spase]
   //val future = (actorSel ? GetTree).mapTo[Spase]
   //val future = (actorSel ? UpdateData)
   //val future = ConfigService.request(GetDatabaseById(new URI("impex://AMDA")))
   //val test = "300s"
   //val d: Duration = DatatypeFactory.newInstance().newDuration("PT"+test.toUpperCase)
   //future.map { f =>  Ok(d.toString) }
//   val future = RegistryService.getNumericalData(None, false)
//   future.map { _ match {
//     case Left(spase) => Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
//     case Right(error) => BadRequest(Json.toJson(error))
//   }} 
   val fileName = "trees/impex___AMDA/getObsDataTree_RemoteParams.xml"
   val xml = scala.xml.XML.loadFile(fileName)
   Ok(<dataRoot>{ xml \\ "dataCenter" filterNot{element => (element \\ "@name" exists (_.text.contains("CLWEB@IRAP"))) ||
   			(element \\ "@isSimulation" exists (_.text == "true"))}}</dataRoot>)
     
  }
  
}