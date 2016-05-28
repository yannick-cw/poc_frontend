package models

import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import controllers.EvaluationRequest
import protocols.JsonSupport

import scala.collection.Iterable
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class HttpRequestModel(serviceRequest: EvaluationRequest) extends JsonSupport {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    import ExecutionContext.Implicits.global

    //create future for each algorithm request
    val futures = for (request <- ValidatorModel.getAlgorithmsByName(serviceRequest.inputAlgorithms)) yield {
            Source
              .single(HttpRequest(POST, entity = serviceRequest.inputText))
              .via(Http(system).outgoingConnection("localhost", 8080))
              .runWith(Sink.head)
    }

    val result = Future sequence (futures.toList)

    //TODO howto handle result from future sequence?
    result onComplete {
        case Success(results) => for (result <- results) println(result)
    }
}