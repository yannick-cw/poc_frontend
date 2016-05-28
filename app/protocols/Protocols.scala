package protocols

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import controllers.EvaluationRequest
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val itemFormat = jsonFormat2(EvaluationRequest)
}
