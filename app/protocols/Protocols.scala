package protocols

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import controllers.{EvaluationRequest, TwitterRequest}
import models.HttpRequestModel.{ClassifyRequest, ClassifyResult}
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val classifyFormat = jsonFormat2(ClassifyRequest)
    implicit val itemFormat = jsonFormat2(EvaluationRequest)
    implicit val classifyResultForm = jsonFormat3(ClassifyResult)
    implicit val classifyTwitterResultForm = jsonFormat(TwitterRequest, "username")
}
