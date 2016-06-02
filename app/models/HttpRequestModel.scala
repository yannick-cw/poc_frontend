package models

import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import controllers.EvaluationRequest
import protocols.JsonSupport
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

object HttpRequestModel extends JsonSupport {
    case class ClassifyRequest(algorithm: String, text: String)
    case class ClassifyResult(algorithm: String, rep: Double, dem: Double)


    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    import ExecutionContext.Implicits.global

    //create future for each algorithm request

    def request(serviceRequest: EvaluationRequest) : Future[List[ClassifyResult]] = {
        val futures = for (request <- ValidatorModel.getAlgorithmsByName(serviceRequest.inputAlgorithms)) yield {
            val classifyRequest = ClassifyRequest(request._2.name, request._1)
            Source
              .single(HttpRequest(POST, uri = "/classify",entity = HttpEntity(contentType = ContentTypes.`application/json` , classifyRequest.toJson.compactPrint)))
              .via(Http(system).outgoingConnection("localhost", 9675))
              .runWith(Sink.head)
        }

        val futureClassifyResult = futures.map{ future =>
          future.flatMap{
              case response@HttpResponse(StatusCodes.OK, _, _, _) => Unmarshal(response).to[ClassifyResult]
              case response@HttpResponse(_, _, _, _) => Future.successful(ClassifyResult("ERROR", 0, 0))
          }
        }

        Future.sequence(futureClassifyResult.toList)
    }
}
