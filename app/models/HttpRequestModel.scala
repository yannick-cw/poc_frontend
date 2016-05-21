package models

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import controllers.EvaluationRequest
import models.ValidatorModel.LearningAlgorithm

import scala.concurrent.Future

case class ServiceEvaluationRequest(inputText: String, involvedAlgorithms: List[LearningAlgorithm])

class HttpRequestModel {
    lazy val algorothmsConnections = ValidatorModel.getValidAlgorithms.map {
        case (key, value) => {
            //sendRequestToServer(value.ip)
        }
    }

   // def sendRequestToServer(algorithmConnection: Uri): Future[HttpResponse] = {
        //TODO generate JSON
     //   Http().singleRequest(HttpRequest(POST, uri = "/", entity = ""))
    //}
}

object HttpRequestModel {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    def apply: HttpRequestModel = new HttpRequestModel()
}