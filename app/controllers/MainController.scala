package controllers

import javax.inject._

import app.Config
import models.HttpRequestModel.ClassifyResult
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

                val response = HttpRequestModel.request(userData)
                //testing without backend connection ->
                //val response = Future.successful(List(ClassifyResult("naive_bayes", 0.7, 0.3)))

                response.map { resultList =>                    val algorithmNames = validatorModel.getAlgorithmsByName(resultList.map(_.algorithm)).map(_._2.name)


                    println(resultList)
                  //TODO check Algorithm case class (needed?)
                    println(algorithmNames)
                    val result = algorithmNames.zip(resultList).toList

                    println(result)

                    Ok(views.html.index(
                        controllers.routes.MainController.index.toString,
                        userInputForm,
                        algorithmsForTemplate,
                        result
                    ))
                }
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