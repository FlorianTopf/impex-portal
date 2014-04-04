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
import com.wordnik.swagger.core._
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.ScalaJsonUtil

// @TODO improve all namespaces of returned XML files
object Registry extends Controller {
  
  // @TODO return full impex tree
  def registry = Action.async { implicit request => 
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val format = req.get("fmt").getOrElse("xml")
    val future = RegistryService.getTree(req.get("id"))
    future.map { (_, format) match {
       case (Left(spase), "json") => Ok(Json.toJson(spase))
       case (Left(spase), _) => 
         Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
       case (Right(error), _) => BadRequest(Json.toJson(error))
    }}
  }
  
  def simulations(fmt: String = "xml") = Action.async { 
     val future = RegistryService.getRepositoryType(Simulation).mapTo[Spase]
     future.map { spase => 
       fmt match {
         case "json" => Ok(Json.toJson(spase))
         case _ => 
           Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
       }
     }
  }
  
  def observations(fmt: String = "xml") = Action.async {
     val future = RegistryService.getRepositoryType(Observation).mapTo[Spase]
     future.map { spase => 
       fmt match {
         case "json" => Ok(Json.toJson(spase))
         case _ => 
           Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
       }
     } 
  }
  
  def repository = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val format = req.get("fmt").getOrElse("xml")
    val future = RegistryService.getRepository(req.get("id"))
    future.map { (_, format) match {
      case (Left(spase), "json") => Ok(Json.toJson(spase))
      case (Left(spase), _) => 
        Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
      case (Right(error), _) => BadRequest(Json.toJson(error))
    }}
  }
  
  def simulationmodel = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val recursive = req.get("r").getOrElse("false")
    val format = req.get("fmt").getOrElse("xml")
    val future = RegistryService.getSimulationModel(req.get("id"), recursive)
    future.map { (_, format) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  def simulationrun = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val recursive = req.get("r").getOrElse("false")
    val format = req.get("fmt").getOrElse("xml")
    val future = RegistryService.getSimulationRun(req.get("id"), recursive)
    future.map { (_, format) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }

  def numericaloutput = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val recursive = req.get("r").getOrElse("false")
    val format = req.get("fmt").getOrElse("xml")
    val future = RegistryService.getNumericalOutput(req.get("id"), recursive)
    future.map { (_, format) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  def granule = Action.async { implicit request =>
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val recursive = req.get("r").getOrElse("false")
    val format = req.get("fmt").getOrElse("xml")
    val future = RegistryService.getGranule(req.get("id"), recursive)
    future.map { (_, format) match {
        case (Left(spase), "json") => Ok(Json.toJson(spase))
        case (Left(spase), _) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case (Right(error), _) => BadRequest(Json.toJson(error))
      }
    }
  }
  
  // @TODO return in json and recursive
  def observatory = Action.async { implicit request => 
    val req: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
    val future = RegistryService.getObservatory(req.get("id"))
    future map { _ match {
        case Left(spase) => 
          Ok(scalaxb.toXML[Spase](spase, "Spase", scalaxb.toScope(None -> "http://impex-fp7.oeaw.ac.at")))
        case Right(error) => BadRequest(Json.toJson(error))
      }
    }  
  }

}