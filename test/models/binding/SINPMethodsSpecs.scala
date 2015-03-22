package models.binding

import org.specs2.mock.Mockito
import play.api.test.Helpers._
import play.api.test._
import scalaxb._
import java.net.URI
import models.provider.TimeProvider
import models.binding._
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout
import scala.xml.NodeSeq


// all test parameters are taken from ICD v1.0 (15.12.2014)
object SINPMethodsSpecs extends org.specs2.mutable.Specification with Mockito {

  "SINP Methods binding" should {
        
        "respond to getDataPointValue" in {
        	val app = new FakeApplication
        	running(app) {
	        	val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	        	
	            val extraParams = ExtraParams_getDataPointValueSINP(
	                Some(VOTableType), // output filetype
	                None // interpolation method (only linear anyway)
	            )
	            
	            val variable = Seq("Bx", "By") // variable seq
	            
	            val result = sinp.service.getDataPointValue(
	        	    "spase://IMPEX/NumericalOutput/SINP/Earth/2003-11-20UT12", // resourceId
	        		Some(variable), // variables
	        		Some(new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_52points.vot")), // url_xyz
	        		Some(extraParams) // extra params
	            )
	        		
	            result.fold(f => println(f), u => {
	               println("Result URL: "+u)
	               val promise = WS.url(u.toString).get()
	               val result = Await.result(promise, Duration(1, "minute")).xml
	               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	            })
	            
	            result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	        	result must beRight // result must be successful
        	}
        }
        
        "respond to calculateDataPointValueFixedTime" in {
            val app = new FakeApplication
            running(app) {
	            val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	            
	            val imf_b = ListOfDouble(
	                Some(-1.0), // x
	                Some(4.0), // y
	                Some(1.0) // z
	            ) 
	          
	            val extraParams = ExtraParams_calculateDataPointValueFixedTime(
	                Some(4.0), // sw density
	                Some(400.0), // sw velocity 
	                Some(imf_b), // imf b 
	                Some(30.0), // dst
	                Some(150.0), // al
	                Some(VOTableType) // output filetype
	            )
	        	
	            val result = sinp.service.calculateDataPointValueFixedTime(
	                "spase://IMPEX/NumericalOutput/SINP/Earth/OnFly", // resourceId
	                TimeProvider.getISODate("2012-03-08T14:06:00"), // start time
	                Some(extraParams), // extra params
	                new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot")) // url_xyz
	                
	            result.fold(f => println(f), u => {
	               println("Result URL: "+u)
	               val promise = WS.url(u.toString).get()
	               val result = Await.result(promise, Duration(1, "minute")).xml
	               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	            })
	        		
	            result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	        	result must beRight // result must be successful
            }
        }
        
        
        "respond to calculateDataPointValue" in {
            val app = new FakeApplication
            running(app) {
	           val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	           
	           val extraParams = ExtraParams_calculateDataPointValue(
	               Some(VOTableType) // output filetype
	           )
	          
	           val result = sinp.service.calculateDataPointValue(
	               "spase://IMPEX/SimulationModel/SINP/Earth/OnFly", // resourceId
	                Some(extraParams), // extra params
	                new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot") // url_xyz  
	           )
	           
	           result.fold(f => println(f), u => {
	               println("Result URL: "+u)
	               val promise = WS.url(u.toString).get()
	               val result = Await.result(promise, Duration(1, "minute")).xml
	               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	           })
	        		
	           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	           result must beRight // result must be successful
            }
        }

        "respond to calculateDataPointValueSpacecraft" in { 
        	val app = new FakeApplication
        	running(app) {
	           val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	           
	           val extraParams = ExtraParams_calculateDataPointValueSpacecraft(
	               Some(VOTableType) // output filetype
	           )
	          
	           val result = sinp.service.calculateDataPointValueSpacecraft(
	               "spase://IMPEX/SimulationModel/SINP/Earth/OnFly", // resourceId
	               CLUSTER1, // spacecraft name
	               TimeProvider.getISODate("2010-01-12T13:00:00"), // start time
	               TimeProvider.getISODate("2010-01-13T03:45:00"), // stop time
	               TimeProvider.getDuration("PT600S"), // sampling
	               Some(extraParams) // extra params
	           )
	           
	           result.fold(f => println(f), u => {
	               println("Result URL: "+u)
	               val promise = WS.url(u.toString).get()
	               val result = Await.result(promise, Duration(1, "minute")).xml
	               scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	           })
	        		
	           result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	           result must beRight // result must be successful
            }
        }
        
	  	"respond to calculateFieldLine" in {
	  		val app = new FakeApplication
		  	running(app) {
			  	val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
			  	  
			  	val extraParams = ExtraParams_calculateFieldLine(
			        Some(50.0), // line length
			        Some(-0.5), // step size
			  	    Some(VOTableType) // output filetype
			  	)
			  	  
			  	val result = sinp.service.calculateFieldLine(
			        "spase://IMPEX/SimulationModel/SINP/Earth/OnFly", // resourceId
			        TimeProvider.getISODate("2010-01-12T13:00:00"), // start time
		    	    Some(extraParams), // extra params
			  	    new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot") // url_xyz
			    )
			  	  
		        result.fold(f => println(f), u => {
		            println("Result URL: "+u)
		            val promise = WS.url(u.toString).get()
		            val result = Await.result(promise, Duration(1, "minute")).xml
		            scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
		        })
			  	  
			  	result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
			  	result must beRight // result must be sucessful
	  		}
	  	}
	  	
	  	"respond to calculateCube" in {
	  		val app = new FakeApplication
			running(app) {
	  		   val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
			
			   val imf_b = ListOfDouble(
			       Some(5.1), // x
			       Some(-8.9), // y
			       Some(4.5) // z
			   )
			
			   val extraParams = ExtraParams_calculateCube(
				   Some(5.0), // sw density
				   Some(800.0), // sw velocity
				   Some(imf_b), // imf b
				   Some(-23.0), // dst
				   Some(-1117.0), // al
				   Some(VOTableType) // output filetype
			   )
			
			   val cubeSize = Cube_size_array(
				   Some(BigInt(-40)), // x_low
				   Some(BigInt(10)), // x_high
				   Some(BigInt(-15)), // y_low
				   Some(BigInt(15)), // y_high
				   Some(BigInt(-10)), // z_low
				   Some(BigInt(10)) // z_high
			   )
			
			   val result = sinp.service.calculateCube(
			       "spase://IMPEX/NumericalOutput/SINP/Earth/OnFly", // resourceId
			       TimeProvider.getISODate("2005-09-11T02:00:00"), // start time 
			       Some(extraParams), // extra params
			       Some(0.7), // sampling 
			       Some(cubeSize) // cube size
			   )
			
			   result.fold(f => println(f), u => {
				   println("Result URL: " + u)
				   //val promise = WS.url(u.toString).get()
				   //val result = Await.result(promise, Duration(1, "minute")).xml
				   //scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
			   })
			
			   result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
			   result must beRight // result must be sucessful
	  		}
	  	}
	  	
	  	"respond to calulateCubeMercury" in {
	  		val app = new FakeApplication
	  		running(app) {
	  			val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	  
	  			val imf_b = ListOfDouble(
	  				Some(5.1), // x
	  				Some(-8.9), // y
	  				Some(4.5) // z
	  			) 
		  	  
	  			val extraParams = ExtraParams_calculateCubeMercury(
	  				Some(196.0), // bd
	  				Some(131.0), // flux
	  				Some(1.5), // rss
	  				Some(1.5), // r2
	  				Some(0.0), // dz
	  				Some(imf_b), // imf b
	  				Some(VOTableType) // output filetype
	  			)
		  	  
	  			val result = sinp.service.calculateCubeMercury(
	  				"spase://IMPEX/SimulationModel/SINP/Mercury/OnFly", // resourceId
	  				Some(extraParams) // extra params
	  			)
		  	  
	  			result.fold(f => println(f), u => {
	  				println("Result URL: "+u)
	  				//val promise = WS.url(u.toString).get()
	  				//val result = Await.result(promise, Duration(1, "minute")).xml
	  				//scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	  			})
		  	  
	  			result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  			result must beRight // result must be sucessful
	  		}
	  	}
	  	  
	  	"respond to calculateDataPointValueMercury" in {
	  		val app = new FakeApplication
	  		running(app) {
	  			val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	  
	  			val imf_b = ListOfDouble(
	  				Some(5.1), // x
	  				Some(-8.9), // y
	  				Some(4.5) // z
	  			) 
          
	  			val extraParams = ExtraParams_calculateDataPointValueMercury(
	  				Some(VOTableType), // output filetype
	  				Some(-196.0), // bd
	  				Some(4.0), // flux
	  				Some(1.35), // rss
	  				Some(1.32), // r2
	  				Some(0.0), // dz
	  				Some(imf_b) // imf b
	  			)
	  	  
	  			val result = sinp.service.calculateDataPointValueMercury(
	  				"spase://IMPEX/SimulationModel/SINP/Mercury/OnFly", // resourceId
	  				Some(extraParams), // extra params
	  				new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/XYZ_calcDPVMercury.vot") // url_xyz
	  			)
	  	   
	  			result.fold(f => println(f), u => {
	  				println("Result URL: "+u)
	  				val promise = WS.url(u.toString).get()
	  				val result = Await.result(promise, Duration(1, "minute")).xml
	  				scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	  			})
	  	   
	  			result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  			result must beRight // result must be sucessful
	  		}
	  	}
	  	
	  	"respond to calculateCubeSaturn" in {
	  		val app = new FakeApplication
	  		running(app) {
	  			val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	   
	  			val imf_b = ListOfDouble(
	  			    Some(0.0), // x
	  			    Some(0.0), // y
	  			    Some(0.0) // z
                ) 
           
                val extraParams = ExtraParams_calculateCubeSaturn(
                    Some(3.0), // bdc
                    Some(-7.0), // bt
                    Some(6.5), // rd2
                    Some(15.0), // rd1
                    Some(18.0), // r2
                    Some(22.0), // rss
                    Some(imf_b), // imf b
                    Some(VOTableType) // output filetype
                )
	  	   
                val cubeSize = Cube_size_array(
                	Some(BigInt(-6)), // x_low
                	Some(BigInt(7)), // x_high
                	Some(BigInt(-3)), // y_low
                	Some(BigInt(3)), // y_high
                	Some(BigInt(-3)), // z_low
                	Some(BigInt(3)) // z_high
                )
	  	   
                val result = sinp.service.calculateCubeSaturn(
                    "spase://IMPEX/SimulationModel/SINP/Saturn/OnFly", // resourceId
                    TimeProvider.getISODate("2008-09-10T12:00:00"),  // start time
                    Some(extraParams), // extra params
                    Some(1.0), // sampling 
                    Some(cubeSize) // cube size
                )
	  	   
                result.fold(f => println(f), u => {
                	println("Result URL: "+u)
                	//val promise = WS.url(u.toString).get()
                	//val result = Await.result(promise, Duration(1, "minute")).xml
                	//scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
                })
	  	   
                result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
                result must beRight // result must be sucessful
	  		}
	  	}
	  	
	  	"respond to calculateDataPointValueSaturn" in {
	  		val app = new FakeApplication
	  		running(app) {
	  		  val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  		  
	  		  val imf_b = ListOfDouble(
	  		      Some(0.0), // x
	  		      Some(0.0), // y
	  		      Some(0.0) // z
	  		  ) 
	  	   
	  		  val extraParams = ExtraParams_calculateDataPointValueSaturn(
	  		      Some(VOTableType), // output filetype
	  		      Some(3.0), // bdc
	  		      Some(-7.0), // bt
	  		      Some(6.5), // rd2
	  		      Some(15.0), // rd1
	  		      Some(18.0), // r2
	  		      Some(22.0), // rss
	  		      Some(imf_b) // imf b
	  		  )
	  	   
	  		  val result = sinp.service.calculateDataPointValueSaturn(
	  		      "spase://IMPEX/SimulationModel/SINP/Saturn/OnFly", // resourceId
	  		      Some(extraParams), // extra params
	  		      new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot") // url_xyz
	  		  )
	  	
	  		  result.fold(f => println(f), u => {
	  			  println("Result URL: "+u)
	  			  val promise = WS.url(u.toString).get()
	  			  val result = Await.result(promise, Duration(1, "minute")).xml
	  			  scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	  		  })
	  	   
	  		  result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  		  result must beRight // result must be sucessful
	  	   }
	   }
	  	
	  	"respond to getSurface" in {
	  		val app = new FakeApplication
	  		running(app) {
	  			val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	  
	  			val planeNormalVector = Seq(0.0f, 0.0f, 1.0f) // plane normal vector seq
	  	  
	  			val planePoint = Seq(0.0f, 0.0f, 0.0f) // plane point seq
	  	  
	  			val extraParams = ExtraParams_getSurfaceSINP(
	  				Some(0.2), // resolution
	  				Some(VOTableType) // output filetype
	  			)
	  			
	  			val variable = Seq("Bx")
	  	  
	  			val result = sinp.service.getSurface(
	  				"spase://IMPEX/NumericalOutput/SINP/Earth/2003-11-20UT12",
	  				Some(variable), // variables
	  				planeNormalVector, // plane normal vector
	  				planePoint, // plane point
	  				Some(extraParams) // extra params
	  			)
	  	  
	  			result.fold(f => println(f), u => {
	  				println("Result URL: "+u)
	  				val promise = WS.url(u.toString).get()
	  				val result = Await.result(promise, Duration(1, "minute")).xml
	  				scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	  			})
	  	   
	  			result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  			result must beRight // result must be sucessful
	  		}
	  	}
	  	
	  	"respond to isAlive" in {
	  		val app = new FakeApplication
	  		running(app) {
	  		   val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	   
	  		   val result = sinp.service.isAlive()
	  		   
	  		   result.fold(f => println(f), b => b must beTrue)
	  		   result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], Boolean]]
	  		   result must beRight
	  	   }
	  	}
	  	
	  	"respond to calculateCubeJupiter" in {
	  		val app = new FakeApplication
	  		running(app) {
	  			val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	   
	  			val imf_b = ListOfDouble(
	  			    Some(0.0), // x
	  			    Some(0.0), // y
	  			    Some(0.0) // z
	  			) 
           
	  			val extraParams = ExtraParams_calculateCubeJupiter(
	  				Some(2.5), // bdc
	  				Some(-2.5), // bt
	  				Some(80.0), // rd2
	  				Some(19.0), // rd1
	  				Some(80.0), // r2
	  				Some(100.0), // rss
	  				Some(imf_b), // imf b
	  				Some(VOTableType) // output filetype
	  			)
	  	   
	  			val cubeSize = Cube_size_array(
	  				Some(BigInt(-450)), // x_low
	  				Some(BigInt(150)), // x_high
	  				Some(BigInt(-300)), // y_low
	  				Some(BigInt(300)), // y_high
	  				Some(BigInt(-300)), // z_low
	  				Some(BigInt(300)) // z_high
	  			)
	  	   
	  			val result = sinp.service.calculateCubeJupiter(
	  				"spase://IMPEX/SimulationModel/SINP/Jupiter/OnFly",
	  				TimeProvider.getISODate("2008-09-10T12:00:00"), 
	  				Some(extraParams), 
	  				Some(10.0), 
	  				Some(cubeSize)
	  			)
	  	   
	  			result.fold(f => println(f), u => {
	  				println("Result URL: "+u)
	  				//val promise = WS.url(u.toString).get()
	  				//val result = Await.result(promise, Duration(1, "minute")).xml
	  				//scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	  			})
	  	   
	  			result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  			result must beRight // result must be sucessful
	  		}
	  	}
	  	
	  	"respond to calculateDataPointValueJupiter" in {
	  		val app = new FakeApplication
            running(app) {
	  			val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	   
	  			val imf_b = ListOfDouble(
	  				Some(0.0), // x
	  				Some(0.0), // y
	  				Some(0.0) // z
	  			) 
	  	   
	  			val extraParams = ExtraParams_calculateDataPointValueJupiter(
	  				Some(VOTableType), // output filetype
	  				Some(2.5), // bdc
	  				Some(-2.5), // bt
	  				Some(10.0), // rd2
	  				Some(80.0), // rd1
	  				Some(80.0), // r2
	  				Some(100.0), // rss
	  				Some(imf_b) // imf_b
	  			)
	  	   
	  			val result = sinp.service.calculateDataPointValueJupiter(
	  				"spase://IMPEX/SimulationModel/SINP/Jupiter/OnFly", // resourceId
	  				Some(extraParams), // extra params
	  				new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot") // url_xyz
	  			)
	  	
	  			result.fold(f => println(f), u => {
	  				println("Result URL: "+u)
	  				val promise = WS.url(u.toString).get()
	  				val result = Await.result(promise, Duration(1, "minute")).xml
	  				scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	  			})
	  	   
	  			result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  			result must beRight // result must be sucessful
	  		}
	  	}
	  	
	  	"respond to calculateFieldLineSaturn" in {
	  	    val app = new FakeApplication
	  	    running(app) {
	  	        val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	        
	  	        val imf_b = ListOfDouble(
	  				Some(0.0), // x
	  				Some(0.0), // y
	  				Some(0.0) // z
	  			) 
	  			
	  			val extraParams = ExtraParams_calculateFieldLineSaturn(
	  			    Some(VOTableType), // output filetype
	  			    Some(3.0), // bdc
	  			    Some(-7.0), // bt
	  			    Some(6.5), // rd2
	  			    Some(15.0), // rd1
	  			    Some(18.0), // r2
	  			    Some(22.0), // rss
	  			    Some(imf_b), // imf_b
	  			    Some(100.0), // line_length
	  			    Some(-0.2) // step_size
	  			)
	  			
	  			val result = sinp.service.calculateFieldLineSaturn(
	  			    "spase://IMPEX/SimulationModel/SINP/Saturn/OnFly", 
	  			    TimeProvider.getISODate("2014-05-14T00:00:00"), 
	  			    Some(extraParams), 
	  			    new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/Cassini_2014-05-14--17.xml")
	  			)
	  			
	  			result.fold(f => println(f), u => {
	  				println("Result URL: "+u)
	  				val promise = WS.url(u.toString).get()
	  				val result = Await.result(promise, Duration(1, "minute")).xml
	  				scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	  			})
	  	   
	  			result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  			result must beRight // result must be sucessful
	  	    }
	  	}
	  	
	  	"respond to calculateFieldLineJupiter" in {
	  	    val app = new FakeApplication
	  	    running(app) {
	  	        val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	        
	  	        val imf_b = ListOfDouble(
	  				Some(0.0), // x
	  				Some(0.0), // y
	  				Some(0.0) // z
	  			) 
	  			
	  			val extraParams = ExtraParams_calculateFieldLineJupiter(
	  			    Some(VOTableType), // output filetype
	  			    Some(2.5), // bdc
	  			    Some(-2.5), // bt
	  			    Some(10.0), // rd2
	  			    Some(80.0), // rd1
	  			    Some(80.0), // r2
	  			    Some(100.0), // rss
	  			    Some(imf_b), // imf_b
	  			    Some(150.0), // line_length
	  			    Some(-0.5) // step_size
	  			)
	  			
	  			val result = sinp.service.calculateFieldLineJupiter(
	  			    "spase://IMPEX/SimulationModel/SINP/Jupiter/OnFly", 
	  			    TimeProvider.getISODate("2009-06-28T13:00:00"), 
	  			    Some(extraParams), 
	  			    new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot")
	  			)
	  			
	  			result.fold(f => println(f), u => {
	  				println("Result URL: "+u)
	  				val promise = WS.url(u.toString).get()
	  				val result = Await.result(promise, Duration(1, "minute")).xml
	  				scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	  			})
	  	   
	  			result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  			result must beRight // result must be sucessful
	  	    }
	  	}
	  		  	
	  	"respond to calculateFieldLineMercury" in {
	  	    val app = new FakeApplication
	  	    running(app) {
	  	        val sinp = new Methods_SINPSoapBindings with Soap11Clients with DispatchHttpClients {}
	  	        
	  	        val imf_b = ListOfDouble(
	  				Some(0.0), // x
	  				Some(0.0), // y
	  				Some(0.0) // z
	  			) 
	  			
	  			val extraParams = ExtraParams_calculateFieldLineMercury(
	  			    Some(VOTableType), // output filetype
	  			    Some(-196), // bd
	  			    Some(4.0), // flux
	  			    Some(1.35), // rss
	  			    Some(1.32), // r2
	  			    Some(0.0), // dz
	  			    Some(imf_b), // imf_b
	  			    Some(12.0), // line_length
	  			    Some(0.02) // step_size
	  			)
	  			
	  			val result = sinp.service.calculateFieldLineMercury(
	  			    "spase://IMPEX/SimulationModel/SINP/Mercury/OnFly", 
	  			    TimeProvider.getISODate("2009-06-28T00:00:00"), 
	  			    Some(extraParams), 
	  			    new URI("http://dec1.sinp.msu.ru/~lucymu/paraboloid/points_calc_120points.vot")
	  			)
	  			
	  			result.fold(f => println(f), u => {
	  				println("Result URL: "+u)
	  				val promise = WS.url(u.toString).get()
	  				val result = Await.result(promise, Duration(1, "minute")).xml
	  				scalaxb.fromXML[VOTABLE](result) must beAnInstanceOf[VOTABLE]
	  			})
	  	   
	  			result must beAnInstanceOf[Either[scalaxb.Soap11Fault[Any], java.net.URI]]
	  			result must beRight // result must be sucessful
	  	    }
	  	}
  }
}