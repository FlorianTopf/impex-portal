package models.provider

object UrlProvider {
  def getUrls(dns: String, paths: Seq[String]): Seq[String] = {
    paths map { path =>
      "http://"+dns+path
    }
  }
  
}