package models.actor

import models.binding._
import models.provider._
import akka.actor._
import akka.pattern.ask
import scala.xml._
import scala.concurrent._
import scala.concurrent.duration._

class ObsDataProvider(val dataTree: Trees, val accessMethods: Methods) 
extends Actor with DataProvider[DataRoot] {
  import models.actor.ConfigService._
  import models.actor.DataProvider._
  
  // @TODO unified error messages
  def receive = {
    // @TODO return unified tree in XML
    case GetTrees(Some("xml")) => sender ! getTreeXML
    case GetTrees(None) => sender ! getTreeObjects
    case GetMethods => sender ! getMethodsXML
    case GetRepository => sender ! getRepository
    // @TODO update also methods
    case UpdateTrees => {
      dataTree.content = updateTrees
    }
    //case _ => sender ! Json.obj("error" -> "message not found")
    case _ => sender ! <error>message not found in data provider</error>
  }

  protected def getMetaData: Database = {
    try {
      Await.result(ConfigService.request(GetDatabaseByName(self.path.name)).mapTo[Database], 5.seconds)
    } catch {
      // if config service fails just provide what is there!
      case e: TimeoutException => Database(self.path.name, None, Nil, Nil, Nil, Nil, "", 
          Observation, UrlProvider.getURI("impex", self.path.name))
    }
  }

  protected def getTreeObjects: Seq[DataRoot] = {
    dataTree.content map { tree => scalaxb.fromXML[DataRoot](tree) }
  }

  // @TODO improve this (scalaxb stuff) => transform to repository
  protected def getRepository: Seq[Repository] = {
    getTreeObjects flatMap { tree => {
        println(tree.dataCenter)
    	tree.dataCenter map { dataCenter => 
          val contact = Contact(dataCenter.name, Seq(ArchiveSpecialist))
    	  val resourceHeader = ResourceHeader(dataCenter.id.toString, Nil, TimeProvider.getISONow, 
    	      None, dataCenter.name, None, Seq(contact))
    	  val accessURL = AccessURL(None , getMetaData.info)
    	  Repository(getMetaData.id.toString, resourceHeader, accessURL)
    	}
      }
    }
  }
  
  protected def getObservatory = ???
  
  protected def getInstrument = ???
  
  protected def getNumericalData = ???

}

