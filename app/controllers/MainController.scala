package controllers

import javax.inject._

import akka.actor.ActorSystem
import akka.actor.Status.{Failure, Success}
import app.Config
import models.{HttpRequestModel, ValidatorModel}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.ws.WS
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

case class EvaluationRequest(inputText: String, inputAlgorithms: List[String])

case class EvaluationResponse(evaluation: Map[String, Double])

@Singleton
class MainController @Inject() extends Controller {
    implicit val defaultValues = Config.Defaults
    lazy val validatorModel = ValidatorModel

    /* Narf...FIX ME
    * request validated and converted to a valid request on verifying method of userInputForm (l. 33) =>
    * store valid request in verifying method for later usages */
    implicit var validRequest: EvaluationRequest = null

    val userInputForm = Form(
        mapping(
            "inputText" -> text(
                minLength = Config.Form.ValidationValues.INPUT_TEXT_MINIMUM_LENGTH,
                maxLength = Config.Form.ValidationValues.INPUT_TEXT_MAXIMUM_LENGTH
            ),

            "inputAlgorithms" -> list(nonEmptyText)

        )(EvaluationRequest.apply)(EvaluationRequest.unapply) verifying(Config.Form.Error.FORM_INPUT_ERROR, fields => validatorModel.getCleanedRequest(fields) match {
            case validRequestData@Some(evaluationRequest) => {
                validRequest = evaluationRequest
                true
            }

            case _ => false
        })
    )


    private val algorithmsForTemplate: Map[String, String] = {
        validatorModel.getValidAlgorithms.map(algorithm => {
            algorithm._1 -> algorithm._2.name
        })
    }


    def index() = Action {
        Ok(views.html.index(
            controllers.routes.MainController.index.toString,
            userInputForm,
            algorithmsForTemplate
        ))
    }


    def handleUserInput = Action { implicit request => {
        userInputForm.bindFromRequest.fold(
            formWithErrors => {
                //400
                BadRequest(views.html.index(
                    controllers.routes.MainController.index.toString,
                    formWithErrors,
                    algorithmsForTemplate
                ))
            },

            userData => {
                import ExecutionContext.Implicits.global

                val httpRequestModel = new HttpRequestModel(validRequest)
                println("start future")
                val response = httpRequestModel.request

                val result = Await.result(response, 10 seconds)

                Ok(views.html.index(
                    controllers.routes.MainController.index.toString,
                    userInputForm,
                    algorithmsForTemplate,
                    result.toString()
                ))
            }
        )
    }}


    def showDocumentation = Action {
        Ok(views.html.documentation(
            controllers.routes.MainController.showDocumentation.toString
        ))
    }


    def showAbout = Action {
        Ok(views.html.about(
            controllers.routes.MainController.showAbout.toString
        ))
    }
}