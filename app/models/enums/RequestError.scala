package models.enums

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class RequestError(code: ERequestError.Value)

object ERequestError extends Enumeration {
    val UNKNOWN_MSG = Value(400, "unknown message") //Bad request
    val INACTIVE = Value(401, "no active session") //Unauthorized
    val UNKNOWN_ENTITY = Value(404, "unknown element") //Not found
    val NOT_IMPLEMENTED = Value(501, "not implemented") //Not implemented
    val UNKNOWN_PROVIDER = Value(502, "unknown provider") //Bad gateway

    implicit val eRequestErrorFormat = new Format[ERequestError.Value] {
        def writes(r: ERequestError.Value): JsValue = {
            JsString(r.toString)
        }

        def reads(json: JsValue): JsResult[ERequestError.Value] = {
            json match {
                case json: JsString => {
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
            Json.obj("code" -> r.code.id, "message" -> r.code)
        }
    }
}