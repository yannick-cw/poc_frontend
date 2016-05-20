package controllers

import javax.inject._

import akka.actor.ActorSystem
import app.Config
import models.ConnectorModel
import models.ValidatorModel
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import akka.http.scaladsl._

case class EvaluationRequest(inputText: String, inputAlgorithms: List[String])

@Singleton
class MainController @Inject() extends Controller {
    val validatorModel = ValidatorModel
    val requestModel = new ConnectorModel


    val userInputForm = Form(
        mapping(
            "inputText" -> text(
                minLength = Config.Form.ValidationValues.INPUT_TEXT_MINIMUM_LENGTH,
                maxLength = Config.Form.ValidationValues.INPUT_TEXT_MAXIMUM_LENGTH
            ),
            "inputAlgorithms" -> list(nonEmptyText)
        )(EvaluationRequest.apply)(EvaluationRequest.unapply) verifying (Config.Form.Error.FORM_INPUT_ERROR, fields => validatorModel.getCleanedRequest(fields) match {
            case validRequestData@Some(evaluationRequest) => {
                implicit val validRequest = validRequestData
                println(validRequest.toString)
                true
            }

            case _ => false
        })
    )


    def index = Action {
        Ok(views.html.index(userInputForm, validatorModel.getValidAlgorithms.map(algorithm => {
            algorithm._1 -> algorithm._2.name
        }))
        )
    }


    def simpleResonse = Ok("simple responde worked")


    def handleInputRequest(inputText: String, requestedAlgorithms: String) = Action {
        //TODO send request to microservice
        Ok("")
    }


    def handleUserInput = Action { implicit request =>
        userInputForm.bindFromRequest.fold(
            formWithErrors => {
                //400
                BadRequest
            },

            userData => {
                Ok
            }
        )
    }


    import akka.http.scaladsl.Http
    import akka.http.scaladsl.model._
    import akka.stream.ActorMaterializer

    import scala.concurrent.Future

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val responseFuture: Future[HttpResponse] =
        Http().singleRequest(HttpRequest(uri = "http://akka.io"))

}