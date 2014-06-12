package models.enums

import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.net.URI

case class ServiceResponse(code: EServiceResponse.Value, message: String)

object EServiceResponse extends Enumeration {
    val OK = Value(200, "request successful")
    val BAD_REQUEST = Value(400, "request failed")

    // does not work on enums :/
    //    implicit val serviceResponseFormat: Format[ServiceResponse.Value] = Json.format[ServiceResponse.Value]
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
            Json.obj("code" -> r.code.id, "message" -> r.message)
        }
    }
    

}