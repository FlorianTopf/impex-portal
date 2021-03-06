package models.enums

import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.net.URI
import com.fasterxml.jackson.core.JsonParseException


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
                case json: JsString => {
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

case class VOTableURLResponse(code: EServiceResponse.Value, message: String, request: JsValue)

object VOTableURLResponse {
    implicit val voTableURLResponseWrites = new Writes[VOTableURLResponse] {
        def writes(r: VOTableURLResponse): JsValue = {
            Json.obj("code" -> r.code.id, "message" -> r.message, "request" -> r.request)
        }
    }
}

case class ServiceResponseJson(code: EServiceResponse.Value, message: JsValue, request: Map[String, String])

object ServiceResponseJson {
    implicit val serviceResponseJson = new Writes[ServiceResponseJson] {
        def writes(r: ServiceResponseJson): JsValue = {
            Json.obj("code" -> r.code.id, "message" -> r.message, "request" -> r.request)
        }
    }
}
