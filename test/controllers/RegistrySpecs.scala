package controllers

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models.binding._
import java.util.Random

// @TODO extend default timeout (how?)
class RegistrySpecs extends Specification {
  
	// test info
  	val rand = new Random(java.lang.System.currentTimeMillis)
	val config = scalaxb.fromXML[Impexconfiguration](scala.xml.XML.loadFile("conf/configuration.xml"))
	val databases = config.impexconfigurationoption.filter(_.key.get == "database").map(_.as[Database])
	val simulations = databases.filter(p => p.typeValue == Simulation)
	val observations = databases.filter(p => p.typeValue == Observation)

	// NOTE: wrong fmt parameter here just returns XML
	//		 all values are possible for the r parameter if given (returns true)
    "Registry" should {

        "send 404 on a bad request" in {
            running(FakeApplication()) {
                route(FakeRequest(GET, "/boum")) must beNone
            }
        }
        
        /* "respond on base route with XML" in {
        	running(FakeApplication()) {
        	  val result = route(FakeRequest(GET, "/registry"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/xml")
        	}
        }
       
         "respond on base route with XML on request" in {
        	running(FakeApplication()) {
        	  val result = route(FakeRequest(GET, "/registry?fmt=xml"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/xml")
        	}
        }
        
        "respond on base route with JSON on request" in {
        	running(FakeApplication()) {
        	  val result = route(FakeRequest(GET, "/registry?fmt=json"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/json")
        	}
        } */
        
        "respond on base route GET+id with XML" in {
        	running(FakeApplication()) {
        	  // @FIXME only for simproviders atm
        	  val randomProvider = simulations(rand.nextInt(simulations.length))
        	  val result = route(FakeRequest(GET, "/registry?id="+randomProvider.id.toString))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/xml")
        	}
        }
        
         "respond on base route GET+id+fmt with JSON" in {
        	running(FakeApplication()) {
        	  // @FIXME only for simproviders atm
        	  val randomProvider = simulations(rand.nextInt(simulations.length))
        	  val result = route(FakeRequest(GET, "/registry?id="+randomProvider.id.toString+"&fmt=json"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/json")
        	}
        }
         
        "respond on base route+unkown id with error" in {
        	running(FakeApplication()) {
        	  val result = route(FakeRequest(GET, "/registry?id=boum"))
        	  result must beSome
        	  status(result.get) must equalTo(BAD_REQUEST)
        	  contentType(result.get) must beSome("application/json")
        	  contentAsString(result.get) must contain("502")
        	  contentAsString(result.get) must contain("unknown provider")
        	} 
        } 
        
        "respond on repository route GET+id with XML" in {
        	running(FakeApplication()) {
        	  val randomProvider = databases(rand.nextInt(databases.length))
        	  val result = route(FakeRequest(GET, "/registry/repository?id="+randomProvider.id.toString))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/xml")
        	}
        }
        
        "respond on repository route+unkown id with error" in {
        	running(FakeApplication()) {
        	  val result = route(FakeRequest(GET, "/registry/repository?id=boum"))
        	  result must beSome
        	  status(result.get) must equalTo(BAD_REQUEST)
        	  contentType(result.get) must beSome("application/json")
        	  contentAsString(result.get) must contain("502")
        	  contentAsString(result.get) must contain("unknown provider")
        	} 
        } 
        
        "respond on simulationmodel route GET+id with XML" in {
        	running(FakeApplication()) {
        	  val randomProvider = simulations(rand.nextInt(simulations.length))
        	  val result = route(FakeRequest(GET, "/registry/simulationmodel?id="+randomProvider.id.toString))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/xml")
        	}
        }
        
        "respond on simulationmodel route GET+id with JSON" in {
        	running(FakeApplication()) {
        	  val randomProvider = simulations(rand.nextInt(simulations.length))
        	  val result = route(FakeRequest(GET, "/registry/simulationmodel?id="+randomProvider.id.toString+"&fmt=json"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/json")
        	}
        }
        
        "respond on simulationmodel route GET+wrong id with error" in {
        	running(FakeApplication()) {
        	  val randomProvider = observations(rand.nextInt(observations.length))
        	  val result = route(FakeRequest(GET, "/registry/simulationmodel?id="+randomProvider.id.toString))
        	  result must beSome
        	  status(result.get) must equalTo(BAD_REQUEST)
        	  contentType(result.get) must beSome("application/json")
        	  contentAsString(result.get) must contain("404")
        	  contentAsString(result.get) must contain("unknown element")
        	}
        }
        
        "respond on simulationmodel route GET+id+r with XML" in {
        	running(FakeApplication()) {
        	  val randomProvider = simulations(rand.nextInt(simulations.length))
        	  val result = route(FakeRequest(GET, "/registry/simulationmodel?id="+randomProvider.id.toString+"&r=true"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/xml")
        	}
        } 
        
        "respond on simulationmodel route GET+id+r with JSON" in {
        	running(FakeApplication()) {
        	  val randomProvider = simulations(rand.nextInt(simulations.length))
        	  val result = route(FakeRequest(GET, "/registry/simulationmodel?id="+randomProvider.id.toString+"&r=true&fmt=json"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/json")
        	}
        } 
        
        "respond on observatory route GET+id with XML" in {
        	running(FakeApplication()) {
        	  val randomProvider = observations(rand.nextInt(observations.length))
        	  val result = route(FakeRequest(GET, "/registry/observatory?id="+randomProvider.id.toString))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/xml")
        	}
        }
        
        "respond on observatory route GET+id with JSON" in {
        	running(FakeApplication()) {
        	  val randomProvider = observations(rand.nextInt(observations.length))
        	  val result = route(FakeRequest(GET, "/registry/observatory?id="+randomProvider.id.toString+"&fmt=json"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/json")
        	}
        }
        
        "respond on observatory route GET+wrong id with error" in {
        	running(FakeApplication()) {
        	  val randomProvider = simulations(rand.nextInt(simulations.length))
        	  val result = route(FakeRequest(GET, "/registry/observatory?id="+randomProvider.id.toString))
        	  result must beSome
        	  status(result.get) must equalTo(BAD_REQUEST)
        	  contentType(result.get) must beSome("application/json")
        	  contentAsString(result.get) must contain("404")
        	  contentAsString(result.get) must contain("unknown element")
        	}
        }
        
        "respond on observatory route GET+id+r with XML" in {
        	running(FakeApplication()) {
        	  val randomProvider = observations(rand.nextInt(observations.length))
        	  val result = route(FakeRequest(GET, "/registry/observatory?id="+randomProvider.id.toString+"&r=true"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/xml")
        	}
        } 
        
        "respond on observatory route GET+id+r with JSON" in {
        	running(FakeApplication()) {
          	  val randomProvider = observations(rand.nextInt(observations.length))
        	  val result = route(FakeRequest(GET, "/registry/observatory?id="+randomProvider.id.toString+"&r=true&fmt=json"))
        	  result must beSome
        	  status(result.get) must equalTo(OK)
        	  contentType(result.get) must beSome("application/json")
        	}
        }
        
        // @TODO add more test cases for the registry
    }
}