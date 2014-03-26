package models.enums

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class RequestError(value: ERequestError.Value)

object ERequestError extends Enumeration {
    val MISSING_VALUE = Value(400, "MISSING_VALUE")
    val MALFORMED_VALUE = Value(402, "MALFORMED_VALUE")
    val UNKNOWN_MSG = Value(403, "unknown message")
    val UNKNOWN_ENTITY = Value(404, "unknown element")
    val UNKNOWN_PROVIDER = Value(501, "unkown provider")
    val INACTIVE = Value(505, "no active session")

    // does not work on enums :/
    //    implicit val requestErrorFormat: Format[RequestError.Value] = Json.format[RequestError.Value]
    implicit val eRequestErrorFormat = new Format[ERequestError.Value] {
        def writes(r: ERequestError.Value): JsValue = {
            JsString(r.toString)
        }

        def reads(json: JsValue): JsResult[ERequestError.Value] = {
            json match {
                case jsString: JsString => {
                    try {
                        JsSuccess(ERequestError.withName((json).as[String]))
                    } catch {
                        case e: Exception => JsError("Invalid RequestError")
                    }
                }
                case other => JsError("Malformed RequestError")
            }
        }
    }
}

object RequestError {
    implicit val requestErrorWrites = new Writes[RequestError] {
        def writes(r: RequestError): JsValue = {
            Json.obj("error" -> r.value.id, "msg" -> r.value)
        }
    }
}