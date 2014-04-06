import models.actor._
import models.binding._
import play.libs.Akka
import play.api._
import scala.concurrent._
import scala.concurrent.duration._
import akka.actor._
import akka.util.Timeout

object Global extends GlobalSettings {
  override def onStart(app: play.api.Application) {
	import models.actor.ConfigService._
	
    implicit val timeout = Timeout(10.seconds)

    val actor: ActorRef = Akka.system.actorOf(Props(new ConfigService), name = "config")
    val databases = Await.result(
        ConfigService.request(GetDatabases).mapTo[Seq[Database]], 10.seconds)

    Akka.system.actorOf(Props(new RegistryService(databases)), name = "registry")

  } 


}