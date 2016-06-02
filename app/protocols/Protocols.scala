package protocols

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import controllers.EvaluationRequest
import models.HttpRequestModel.{ClassifyRequest, ClassifyResult}
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val classifyFormat = jsonFormat2(ClassifyRequest.apply)
    implicit val itemFormat = jsonFormat2(EvaluationRequest.apply)
    implicit val classifyResultForm = jsonFormat3(ClassifyResult.apply)

}
