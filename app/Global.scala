import models.actor._
import models.binding._
import models.provider._
import play.libs.Akka
import play.api._
import play.api.libs.ws._
import scala.concurrent._
import scala.concurrent.duration._
import scala.xml._
import akka.actor._
import akka.util.Timeout
import java.net.URI
import java.io._

object Global extends GlobalSettings {
  override def onStart(app: play.api.Application) {
    implicit val timeout = Timeout(10.seconds)
    
    import models.actor.ConfigService._

    println("application has started")

    val actor: ActorRef = Akka.system.actorOf(Props(new ConfigService), name = "config")
    val config = Await.result(
      ConfigService.request(GetDatabases).mapTo[Seq[Database]],
      10.seconds)

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
        
        database.typeValue match {
          case Simulation => RegistryService.registerChild(
              Props(new SimDataProvider(Trees(trees), Methods(methods))),
              database.name)
          case Observation => RegistryService.registerChild(
              Props(new ObsDataProvider(Trees(trees), Methods(methods))),
              database.name)
        }
    }

  }
  
  private def fetchAndSaveFiles(URLs: Seq[URI], folder: String, db: Database): Seq[NodeSeq] = { 
    URLs map {
      URL => 
        val fileName = PathProvider.getPath(folder, db.name, URL.toString)
        val fileDir = new File(folder+"/"+db.name)
        if(!fileDir.exists) fileDir.mkdir()
        val file = new File(fileName)
        if(!file.exists) file.createNewFile()
        val promise = WS.url(URL.toString).get()
        //try {
        //  val result = Await.result(promise, 1.minute).xml
        //  scala.xml.XML.save(fileName, result, "UTF-8")
        //  result
        //} catch {
        //  case e: TimeoutException => {
        //    println("timeout: fallback on local file at "+db.name)
            scala.xml.XML.load(fileName)
        //  }
        //}
    }
  }

}