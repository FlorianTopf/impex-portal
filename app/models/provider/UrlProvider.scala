package models.provider

import java.net.URI

object UrlProvider {
  def getUrls(protocol: String, dns: String, paths: Seq[String]): Seq[URI] = {
    paths map { path =>
      new URI(protocol+"://"+dns+path)
    }
  }
  
  //http://docs.oracle.com/javase/6/docs/api/java/net/URI.html
  def getURI(scheme: String, authority: String, path: String = ""): URI = {
    path match {
      case "" => new URI(scheme+"://"+authority)
      case _  => new URI(scheme+"://"+authority+"/"+path)
    }
  }
  
}