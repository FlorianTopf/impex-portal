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
    }
}