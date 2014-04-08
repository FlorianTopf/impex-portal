package controllers

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class ConfigSpecs extends Specification {

    "Configuration" should {

        "send 404 on a bad request" in {
            running(FakeApplication()) {
                route(FakeRequest(GET, "/boum")) must beNone
            }
        }
        
        "respond with XML by default" in {
        	running(FakeApplication()) {
        	  val result = route(FakeRequest(GET, "/config"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/xml")
        	}
        }
       
        "respond with XML on request" in {
        	running(FakeApplication()) {
        	  val result = route(FakeRequest(GET, "/config?fmt=xml"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/xml")
        	}
        }
        
        "respond with JSON on request" in {
        	running(FakeApplication()) {
        	  val result = route(FakeRequest(GET, "/config?fmt=json"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/json")
        	}
        }
        
        "respond with error on unkown format" in {
        	running(FakeApplication()) {
        	  val result = route(FakeRequest(GET, "/config?fmt=boum"))
        	  result must beSome
        	  status(result.get) must equalTo(BAD_REQUEST)
        	  contentType(result.get) must beSome("application/json")
        	  contentAsString(result.get) must contain("403")
        	  contentAsString(result.get) must contain("unknown message")
        	} 
        } 
    }
}