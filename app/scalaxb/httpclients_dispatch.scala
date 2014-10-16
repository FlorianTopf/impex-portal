package scalaxb

trait DispatchHttpClients extends HttpClients {
  lazy val httpClient = new DispatchHttpClient {}

  trait DispatchHttpClient extends HttpClient {
    import dispatch._
    // @FIXME maybe not working
    import dispatch.Defaults
    val http = new Http()
    http.configure(_.setIdleConnectionTimeoutInMs(120000))
    
    def request(in: String, address: java.net.URI, headers: Map[String, String]): String = {
      val req = url(address.toString) << in <:< headers
      val s = http(req > as.String)
      s()
    }
  }
}
