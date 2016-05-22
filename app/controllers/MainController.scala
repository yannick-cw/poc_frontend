package controllers

import javax.inject._

import akka.actor.ActorSystem
import app.Config
import models.{HttpRequestModel, ValidatorModel}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class EvaluationRequest(inputText: String, inputAlgorithms: List[String])
case class EvaluationResponse(evaluation: Map[String, Double])

@Singleton
class MainController @Inject() extends Controller {
    val validatorModel = ValidatorModel

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

        )(EvaluationRequest.apply)(EvaluationRequest.unapply) verifying (Config.Form.Error.FORM_INPUT_ERROR, fields =>  validatorModel.getCleanedRequest(fields) match {
            case validRequestData@Some(evaluationRequest) => {
                validRequest = evaluationRequest
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
                Ok(s"request sent with ${this.validRequest}")
            }
        )
    }

    import ExecutionContext.Implicits.global

    def futureTest = Action {
        lazy val futureList:List[Future[String]] = List(generateFuture(true), generateFuture(false))

        lazy val future1 = generateFuture(true)
        lazy val future2 = generateFuture(false)
        lazy val future3 = generateFuture(true)

        Future.sequence(Seq(future1,future2)).map{
            case results => println(results)
        }
        Ok
    }


    def generateFuture(sleep: Boolean) : Future[String] = Future {
        Thread.sleep(5000)
        println(s"Thread with sleep value $sleep is ready")
        s"return from sleep value was $sleep"
    }

}