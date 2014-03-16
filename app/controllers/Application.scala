package controllers

import models.actor._
import models.binding._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits._
import scala.xml._
import scala.collection.mutable.ListBuffer
import views.html._
import models.enums._

object Application extends Controller {
  import models.actor.ConfigService._
  
  def index = Action {
    Ok(views.html.portal())
  }

  def config(fmt: String = "xml") = Action.async {
    val future = ConfigService.request(GetConfig(fmt))
    
    fmt.toLowerCase match {
      case "xml" => future.mapTo[NodeSeq].map(config => Ok(config))
      case "json" => future.mapTo[JsValue].map(config => Ok(config))
    }
  }
  
  // @TODO return in json/xml
  def simulations = Action.async {
     val future = RegistryService.getRepositoryType(Simulation).mapTo[Spase]
     future.map { spase => 
       Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
     }
  }
  
  // @TODO return in json/xml
  def observations = Action.async {
     val future = RegistryService.getRepositoryType(Observation).mapTo[Spase]
     future.map { spase => 
       Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
     } 
  }
  
  // @TODO return in json
  def repository = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val id: Option[String] = for {
      id <- req.get("id")
    } yield id
    
    val future = RegistryService.getRepository(id)
    future.map { repository => 
      repository match {
        case Left(spase) => Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  // route for testing
  def test = Action.async {
    val future = RegistryService.getTreeXML(Some("FMI"))

    future map { response =>
      val tree = response map { r => scalaxb.fromXML[Spase](r) }
      //val simulationModel = response.xml \\ "SimulatioModel" \ "ResourceID"

      val simulationModels: ListBuffer[SimulationModelType] = ListBuffer()
      val simulationRuns: ListBuffer[SimulationRun] = ListBuffer()

      for (element <- tree(0).ResourceEntity) element.key.get match {
        case "SimulationModel" => simulationModels += element.as[SimulationModelType]
        //@TODO still the same problem with some XML elements
        case "SimulationRun" => simulationRuns +=
          scalaxb.fromXML[SimulationRun](element.value.asInstanceOf[NodeSeq])
        case _ => //println("something else")
      }

      //println(simulationRuns(1))

      //Ok("OK: "+test.text+"<br/>"+
      Ok("OK: First model: " + simulationModels.head.ResourceID + "; " +
        simulationModels.length + " Models found ; " +
        tree.head.ResourceEntity.length + " ResourceEntity elements found" + "; " +
        //"RepositoryID="+repository.ResourceID+"; "+
        simulationRuns.length + " SimulationRuns found ----->>>>>> " + simulationRuns)
    }

  }

}