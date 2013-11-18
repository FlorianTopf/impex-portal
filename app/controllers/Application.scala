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
import models.actor._
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import play.libs.Akka
import akka.pattern.ask
import akka.util.Timeout
import scala.language.postfixOps

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def config = Action {
    val future = ConfigService.request(GetDatabases).mapTo[Map[String, Database]]
    
    Async {
      future.map(databases => Ok(views.html.config(databases)))
    }
  }
  
  def tree = Action {
     val future = RegistryService.getTreeXML(Some("SINP"))
    
     Async {
      future map { response =>

        val tree = response map { r => scalaxb.fromXML[Spase](r) }
        //val simulationModel = response.xml \\ "SimulatioModel" \ "ResourceID"

        // @TODO how we can extract from the sequence with matching the data records name?
        // @TODO why must repository be casted to NodeSeq?
        //val repository = scalaxb.fromXML[Repository](tree.ResourceEntity(1).value.asInstanceOf[NodeSeq])

        val simulationModels: ListBuffer[SimulationModel] = ListBuffer()
        val simulationRuns: ListBuffer[SimulationRun] = ListBuffer()

        for (element <- tree(0).ResourceEntity) element.key.get match {
          case "SimulationModel" => simulationModels += element.as[SimulationModel]
          //@TODO still the same problem with some XML elements
          case "SimulationRun" => simulationRuns += 
            scalaxb.fromXML[SimulationRun](element.value.asInstanceOf[NodeSeq])
          case _ => println("something else")
        }

        //println(simulationRuns(1))

        //Ok("OK: "+test.text+"<br/>"+
        Ok("OK: First model: " + simulationModels.head.ResourceID + "; " +
          simulationModels.length + " Models found ; " +
          tree.head.ResourceEntity.length + " ResourceEntity elements found" + "; " +
          //"RepositoryID="+repository.ResourceID+"; "+
          simulationRuns.length + " SimulationRuns found ----->>>>>> "+ simulationRuns)

      }
     }
     
  } 
  
  def repo = Action {
      //implicit val timeout = Timeout(10 seconds)
 /*   val stripped1 = Await.result(
        RegistryService.requestTreeXML(Some("AMDA")).mapTo[Seq[NodeSeq]], 1.second) map { tree => tree \\ "dataCenter" }
    
    val stripped2 = Await.result(
        RegistryService.requestTreeXML(Some("ClWeb")).mapTo[Seq[NodeSeq]], 1.second) map { tree => tree \\ "dataCenter" }
    
    val merged = <dataRoot>{stripped1}{stripped2}</dataRoot>
    
    Ok("OK:" + merged) */
      val registry: ActorRef = Akka.system.actorFor("user/registry")
      val future = RegistryService.getRepositories
      //val future = RegistryService.getRepository("AMDA")
      
      Async {
        future.map{ repositories =>
          println(repositories)
          Ok(views.html.repository(repositories))
        }
      }  
    
  }
  
}