package models.provider

import java.net.URI

object UrlProvider {
  def getUrls(protocol: String, dns: String, paths: Seq[String]): Seq[URI] = {
    paths.filter(!_.isEmpty) map { path =>
      new URI(protocol+"://"+dns+path)
    }
  }
  
  def decodeURI(string: String): URI = {
    new URI(string.replaceAll("(___)", "://").replaceAll("_", "/"))
  }
  
  def encodeURI(uri: URI): String = {
    uri.toString.replaceAll("(:|/)", "_")
  }
  
}