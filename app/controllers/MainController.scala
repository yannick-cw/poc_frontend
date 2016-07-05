package controllers

import javax.inject._

import app.Config
import models.HttpRequestModel.ClassifyResult
import models.{FormValidation, HttpRequestModel, ValidatorModel}
import play.api.mvc._
import play.api.data.Form
import play.api.libs.json.Json
import protocols.JsonSupport
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

case class EvaluationRequest(inputText: String = "", inputAlgorithms: List[String] = List.empty)
case class TwitterRequest(username: String = "")

case class ViewData(
                     routePath: String = "",
                     inputForm: Form[EvaluationRequest],
                     inputAlgorithms: Map[String, String] = Map.empty,
                     response: List[ClassifyResult] = List.empty,
                     messages: List[String] = List.empty,
                     twitterForm: Form [TwitterRequest]
                   )

@Singleton
class MainController @Inject() extends Controller with FormValidation with JsonSupport {
    implicit val defaultValues = Config.Defaults
    lazy val validatorModel = ValidatorModel

    private val algorithmsForTemplate: Map[String, String] = {
        validatorModel.getValidAlgorithms.map(algorithm => {
            algorithm._1 -> algorithm._2.name
        })
    }

    def index() = Action {
        Ok(views.html.index(
            ViewData(
                controllers.routes.MainController.index.toString,
                userInputForm,
                algorithmsForTemplate,
                twitterForm = twitterRequestForm
            )))
    }


    def handleUserInput = Action.async { implicit request => {
        userInputForm.bindFromRequest.fold(
            formWithErrors => {
                //400
                println("error")
                Future.successful(BadRequest(views.html.index(
                    ViewData(
                        controllers.routes.MainController.index.toString,
                        formWithErrors,
                        algorithmsForTemplate,
                        twitterForm = twitterRequestForm
                    ))))
            },

            userData => {
                println(userData)
                import ExecutionContext.Implicits.global

                //testing without backend connection ->
                //val response = Future.successful(List(ClassifyResult("naive_bayes", 0.7, 0.3)))
                val filledFormular = userInputForm.fill(userData)

                try {
                    val response = HttpRequestModel.request(userData)
                    response.map { result =>
                        val res = List(
                            ClassifyResult("naive_bayes", 0.7, 0.3),
                            ClassifyResult("naive_bayes_tfidf", 0.4, 0.6)
                        )
                        Ok(Json.toJson(res.map { r =>
                            (r.algorithm, Map("dem" -> r.dem, "rep" -> r.rep))
                        }.toMap ))

                        /*
                        Ok(Json.toJson(result.map { r =>
                            (r.algorithm, Map("dem" -> r.dem, "rep" -> r.rep))
                        }.toMap ))
                        */
                    }

                } catch {
                    case _: Exception =>
                        Future.successful(
                            Ok(views.html.index(
                                ViewData(
                                    controllers.routes.MainController.index.toString,
                                    filledFormular,
                                    algorithmsForTemplate,
                                    twitterForm = twitterRequestForm
                                )
                            ))
                        )
                }
            }
        )
    }
    }


    def handleTwitterRequest = Action.async { implicit request => {
        twitterRequestForm.bindFromRequest.fold(
            formWithErrors => {
                //400
                Future.successful(BadRequest(views.html.index(
                    ViewData(
                        controllers.routes.MainController.index.toString,
                        userInputForm,
                        algorithmsForTemplate,
                        twitterForm = formWithErrors
                    ))))
            },

            userData => {
                import ExecutionContext.Implicits.global

                //testing without backend connection ->
                //val response = Future.successful(List(ClassifyResult("naive_bayes", 0.7, 0.3)))
                val filledFormular = twitterRequestForm.fill(userData)

                try {

                    val res = List(
                        ClassifyResult("naive_bayes", 0.7, 0.3),
                        ClassifyResult("naive_bayes_tfidf", 0.4, 0.6)
                    )

                    Future.successful(Ok(Json.toJson(res.map { r =>
                        (r.algorithm, Map("dem" -> r.dem, "rep" -> r.rep))
                    }.toMap )))

                    /*
                    val response = HttpRequestModel.requestTwitter(userData)
                    response.map { result =>
                        val res = List(
                            ClassifyResult("naive_bayes", 0.7, 0.3),
                            ClassifyResult("naive_bayes_tfidf", 0.4, 0.6)
                        )
                        Ok(Json.toJson(res.map { r =>
                            (r.algorithm, Map("dem" -> r.dem, "rep" -> r.rep))
                        }.toMap ))

                        Ok(Json.toJson(result.map { r =>
                            (r.algorithm, Map("dem" -> r.dem, "rep" -> r.rep))
                        }.toMap ))
                    }
                    */

                } catch {
                    case _: Exception =>
                        Future.successful(
                            Ok(views.html.index(
                                ViewData(
                                    controllers.routes.MainController.index.toString,
                                    userInputForm,
                                    algorithmsForTemplate,
                                    twitterForm = filledFormular
                                )
                            ))
                        )
                }
            }
        )
    }
    }

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