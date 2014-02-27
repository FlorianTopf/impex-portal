import akka.actor._
import play.libs.Akka
import scala.concurrent._
import play.api._
import models.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import models.binding._
import play.api.libs.ws._
import models.provider._
import scala.xml._
import scala.language.postfixOps
import java.net.URI

object Global extends GlobalSettings {
  override def onStart(app: play.api.Application) {
    implicit val timeout = Timeout(10 seconds)
    
    import models.actor.ConfigService._

    println("application has started")

    val actor: ActorRef = Akka.system.actorOf(Props(new ConfigService), name = "config")
    val config = Await.result(
      ConfigService.request(GetDatabases).mapTo[Seq[Database]],
      10.seconds)

    //println(config)

    Akka.system.actorOf(
      Props(new RegistryService),
      name = "registry")

    config map {
      database =>
        val dns: String = database.databaseoption.head.value
        val protocol: String = database.protocol.head
        val treeURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, database.tree)
        val methodsURLs: Seq[URI] = UrlProvider.getUrls(protocol, dns, database.methods)

        println("treeURL="+treeURLs)
        println("methodsURL="+methodsURLs)
        println("fetching files from "+database.name)

        val trees: Seq[NodeSeq] = fetchAndSaveFiles(treeURLs, "trees", database)
        val methods: Seq[NodeSeq] = fetchAndSaveFiles(methodsURLs, "methods", database)

        RegistryService.registerChild(
          Props(new DataProvider(Trees(trees), Methods(methods), database.typeValue)),
          database.name)
    }

  }
  
  private def fetchAndSaveFiles(URLs: Seq[URI], folder: String, db: Database): Seq[NodeSeq] = { 
    URLs map {
      URL => 
        // @FIXME just for testing (without update)
        val promise = WS.url(URL.toString).get()
        try {
          val result = Await.result(promise, 1.minute).xml
          scala.xml.XML.save(
            PathProvider.getPath(folder, db.name, URL.toString), result, "UTF-8")
          result
        } catch {
          case e: TimeoutException => {
            println("timeout: fallback on local file at "+db.name)
            scala.xml.XML.load(PathProvider.getPath(folder, db.name, URL.toString))
          }
        }
    }
  }

}