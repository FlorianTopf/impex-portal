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
import akka.actor._
import akka.pattern.ask
import play.libs.Akka


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
  
  // @TODO return in json
  def simulations = Action.async {
     val future = RegistryService.getRepositoryType(Simulation).mapTo[Spase]
     future.map { spase => 
       Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
     }
  }
  
  // @TODO return in json
  def observations = Action.async {
     val future = RegistryService.getRepositoryType(Observation).mapTo[Spase]
     future.map { spase => 
       Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
     } 
  }
  
  // @TODO return in json
  def repository = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val recursive = req.get("r").getOrElse("false")
    recursive match { 
      case "false" => {   
        val future = RegistryService.getRepository(req.get("id"))
        future.map { _ match {
          case Left(spase) => Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
          case Right(error) => BadRequest(Json.toJson(error))
        }}
      }
      case "true" => { 
        val future = RegistryService.getTreeXML(req.get("id"))
        future.map { _ match {
           case Left(tree) => Ok(tree)
           case Right(error) => BadRequest(Json.toJson(error))
        }}
      }
    }
  }
  
  // @TODO return in json and recursive
  def simulationmodel = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getSimulationModel(req.get("id"), req.get("repository"))
    future.map { _ match {
        case Left(spase) => Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  // @TODO return in json and recursive
  def simulationrun = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getSimulationRun(req.get("id"), req.get("simulationmodel"))
    future.map { _ match {
        case Left(spase) => Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }
  }

  // @TODO return in json and recursive
  def numericaloutput = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getNumericalOutput(req.get("id"), req.get("simulationrun"))
    future.map { _ match {
        case Left(spase) => Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  // @TODO return in json and recursive
  def granule = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getGranule(req.get("id"), req.get("numericaloutput"))
    future.map { _ match {
        case Left(spase) => Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  // @TODO return in json and recursive
  def observatory = Action.async { implicit request => 
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getObservatory(req.get("id") , None)
    future map { _ match {
        case Left(spase) => Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }  
  }
  
  // @TODO return in json 
  def methods = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getMethodsXML(req.get("id"))
    future.map { _ match {
      case Left(tree) => Ok(tree.reduce(_++_))
      case Right(error) => BadRequest(Json.toJson(error))
    }}
  }
  
  // route for testing
  def test = Action.async {
    val future = RegistryService.getMethodsXML(Some("impex://FMI"))
    future.map { _ match {
      case Left(tree) => {
        Ok(tree.reduce(_++_))
      }
      case Right(error) => BadRequest(Json.toJson(error))
    }}
  }

}