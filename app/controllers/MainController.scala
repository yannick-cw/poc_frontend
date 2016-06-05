package controllers

import javax.inject._

import app.Config
import models.{FormValidation, HttpRequestModel, ValidatorModel}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

case class EvaluationRequest(inputText: String, inputAlgorithms: List[String])

case class EvaluationResponse(evaluation: Map[String, Double])

@Singleton
class MainController @Inject() extends Controller with FormValidation {
    implicit val defaultValues = Config.Defaults
    lazy val validatorModel = ValidatorModel

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


    def handleUserInput = Action.async{ implicit request => {
        userInputForm.bindFromRequest.fold(
            formWithErrors => {
                //400
                Future.successful(BadRequest(views.html.index(
                    controllers.routes.MainController.index.toString,
                    formWithErrors,
                    algorithmsForTemplate
                )))
            },

            userData => {
                import ExecutionContext.Implicits.global

                /*val response = HttpRequestModel.request(userData)

                response.map { list =>
                    Ok(views.html.index(
                        controllers.routes.MainController.index.toString,
                        userInputForm,
                        algorithmsForTemplate,
                        list.mkString(" ")
                    ))
                }*/

                Future.successful(Ok(views.html.index(
                    controllers.routes.MainController.index.toString,
                    userInputForm,
                    algorithmsForTemplate,
                    EvaluationResponse(Map("rep" -> 0.3, "dem" -> 0.7))
                )))
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