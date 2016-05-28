package models

import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import controllers.EvaluationRequest
import protocols.JsonSupport

import scala.concurrent.{ExecutionContext, Future}

class HttpRequestModel(serviceRequest: EvaluationRequest) extends JsonSupport {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    import ExecutionContext.Implicits.global

    //create future for each algorithm request

    def request = {

        val futures = for (request <- ValidatorModel.getAlgorithmsByName(serviceRequest.inputAlgorithms)) yield {
            Source
              .single(HttpRequest(POST, entity = serviceRequest.inputText))
              .via(Http(system).outgoingConnection("localhost", 8080))
              .runWith(Sink.head)
        }

        val result = Future sequence (futures.toList)

        //TODO howto handle result from future sequence?
        result.map { responseList => {
            responseList.map { response => {
                println(response.toString())
                response.entity.asInstanceOf[HttpEntity.Strict].data.decodeString("UTF-8")
            }
            }
        }
        }
    }
}
