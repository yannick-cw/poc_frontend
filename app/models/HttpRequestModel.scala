package models

import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import controllers.{EvaluationRequest, TwitterRequest}
import protocols.JsonSupport
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

object HttpRequestModel extends JsonSupport {
    case class ClassifyRequest(algorithm: String, text: String)
    case class ClassifyResult(algorithm: String = "", rep: Double = 0.0, dem: Double = 0.0)


    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    import ExecutionContext.Implicits.global

    //create future for each algorithm request

    def request(serviceRequest: EvaluationRequest) : Future[List[ClassifyResult]] = {
        val futures = for (request <- ValidatorModel.getAlgorithmsByName(serviceRequest.inputAlgorithms)) yield {
            val classifyRequest = ClassifyRequest(request._1, serviceRequest.inputText)
            Source
              .single(HttpRequest(POST, uri = "/classify",entity = HttpEntity(contentType = ContentTypes.`application/json` , classifyRequest.toJson.compactPrint)))
              .via(Http(system).outgoingConnection(ConfigFactory.load().getString("service.host"), ConfigFactory.load().getInt("service.port")))
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


    def requestTwitter(twitterRequest: TwitterRequest) = {
        val request = Source
          .single(HttpRequest(POST, uri = "/classifyUser", entity = HttpEntity(contentType = ContentTypes.`application/json` , twitterRequest.toJson.compactPrint)))
          .via(Http(system).outgoingConnection(ConfigFactory.load().getString("twitter.host"), ConfigFactory.load().getInt("twitter.port")))
          .runWith(Sink.head)

        request.flatMap {
            case response@HttpResponse(StatusCodes.OK, _, _, _) => Unmarshal(response).to[ClassifyResult]
            case response@HttpResponse(_, _, _, _) => Future.successful(ClassifyResult("ERROR", 0, 0))
        }
    }

}
