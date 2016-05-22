package models

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import models.ValidatorModel.LearningAlgorithm
import helpers.Helper._

case class ServiceEvaluationRequest(inputText: String, involvedAlgorithms: List[LearningAlgorithm])

class HttpRequestModel(serviceRequest: ServiceEvaluationRequest) {
    val algorithmsConnections = ValidatorModel.getValidAlgorithms.values.toList <=> serviceRequest.involvedAlgorithms

   // def sendRequestToServer(algorithmConnection: Uri): Future[HttpResponse] = {
        //TODO generate JSON
     //   Http().singleRequest(HttpRequest(POST, uri = "/", entity = ""))
    //}
}

object HttpRequestModel {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
}