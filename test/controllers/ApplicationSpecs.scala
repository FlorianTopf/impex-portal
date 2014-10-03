package controllers

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class ApplicationSpecs extends Specification {

    "Application" should {

        "send 404 on a bad request" in {
            running(FakeApplication()) {
                route(FakeRequest(GET, "/boum")) must beNone
            }
        }
        
        "render the portal page" in {
        	running(FakeApplication()) {
        		val portal = route(FakeRequest(GET, "/")).get
        
        		status(portal) must equalTo(OK)
        		contentType(portal) must beSome.which(_ == "text/html")
        	}
        }
        
        "render the api-view page" in {
        	running(FakeApplication()) {
        		val apiView = route(FakeRequest(GET, "/api-view")).get
        
        		status(apiView) must equalTo(OK)
        		contentType(apiView) must beSome.which(_ == "text/html")
        	}
          
        }
        
        
    }
}