// Generated by <a href="http://scalaxb.org/">scalaxb</a>.
package models.binding

import play.api.libs.json._


case class Database(name: String,
  description: Option[String] = None,
  databaseoption: Seq[scalaxb.DataRecord[String]] = Nil,
  methods: Seq[String] = Nil,
  tree: Seq[String] = Nil,
  protocol: Seq[String] = Nil,
  info: String,
  typeValue: models.binding.Databasetype,
  id: java.net.URI) extends ImpexconfigurationOption

trait DatabaseOption

case class Tool(name: String,
  description: Option[String] = None,
  tooloption: Seq[scalaxb.DataRecord[String]] = Nil,
  info: String) extends ImpexconfigurationOption

trait ToolOption

case class Impexconfiguration(impexconfigurationoption: scalaxb.DataRecord[models.binding.ImpexconfigurationOption]*)

trait ImpexconfigurationOption
trait Databasetype

object Databasetype {
  def fromString(value: String, scope: scala.xml.NamespaceBinding): Databasetype = value match {
    case "simulation" => Simulation
    case "observation" => Observation

  }
}

case object Simulation extends Databasetype { override def toString = "simulation" }
case object Observation extends Databasetype { override def toString = "observation" }

// additions for JSON conversion
object Impexconfiguration {
  implicit val impexconfigurationWrites: Writes[Impexconfiguration] = new Writes[Impexconfiguration] {
    def writes(c: Impexconfiguration): JsValue = {
      val tools = c.impexconfigurationoption.filter(d => d.key.get == "tool").map(d => d.as[Tool])
      val dbs = c.impexconfigurationoption.filter(d => d.key.get == "database").map(d => d.as[Database])
      Json.obj("impexconfiguration" -> Json.obj("databases" -> dbs, "tools" -> tools))
    }
  }
  
  implicit val stringWrites: Writes[scalaxb.DataRecord[String]] = 
    new Writes[scalaxb.DataRecord[String]] {
    	def writes(d: scalaxb.DataRecord[String]): JsValue = {
    	  JsString(d.as[String])
    	}
  }
  
  implicit val toolWrites: Writes[Tool] = new Writes[Tool] {
    def writes(t: Tool): JsValue = 
      Json.obj("name" -> t.name, "description" -> t.description, "dns" -> t.tooloption ,"info" -> t.info)
  }
  
  implicit val databaseWrites: Writes[Database] = new Writes[Database] {
    def writes(d: Database): JsValue =  
      Json.obj("id" -> d.id.toString, "type" -> d.typeValue.toString, "name" -> d.name, "description" -> d.description, "dns" -> d.databaseoption, 
          "methods" -> d.methods, "tree" -> d.tree, "protocol" -> d.protocol, "info" -> d.info)
  }

}

