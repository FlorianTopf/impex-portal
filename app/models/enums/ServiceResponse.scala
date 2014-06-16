package models.enums

import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.net.URI

case class ServiceResponse(code: EServiceResponse.Value, message: String, request: Map[String, String])

object EServiceResponse extends Enumeration {
    val OK = Value(200)
    val BAD_REQUEST = Value(400)
    val NOT_IMPLEMENTED = Value(501) 

    implicit val eServiceResponseFormat = new Format[EServiceResponse.Value] {
        def writes(r: EServiceResponse.Value): JsValue = {
            JsString(r.toString)
        }

        def reads(json: JsValue): JsResult[EServiceResponse.Value] = {
            json match {
                case jsString: JsString => {
                    try {
                        JsSuccess(EServiceResponse.withName((json).as[String]))
                    } catch {
                        case e: Exception => JsError("Invalid ServiceResponse")
                    }
                }
                case other => JsError("Malformed ServiceResponse")
            }
        }
    }
}

object ServiceResponse {
    implicit val serviceResponseWrites = new Writes[ServiceResponse] {
        def writes(r: ServiceResponse): JsValue = {
            Json.obj("code" -> r.code.id, "message" -> r.message, "request" -> r.request)
        }
    }
    

}