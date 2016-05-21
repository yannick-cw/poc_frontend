package models

import app.Config
import controllers.EvaluationRequest
import akka.http.scaladsl.model._


object ValidatorModel {

    case class InputData(inputText: String, usedAlgorithms: List[LearningAlgorithm])

    abstract class LearningAlgorithm(val name: String, val ip: Uri)

    object LearningAlgorithmTypes {
        //TODO request ip's from algorithm-services
        case object NaiveBayes extends LearningAlgorithm("Naive Bayes", "localhost")
    }


    object LearningAlgorithms {
        val validAgorithms:scala.collection.immutable.Map[String, LearningAlgorithm] = Map(
            "naive_bayes" -> LearningAlgorithmTypes.NaiveBayes
        )


        def apply(algorithmName: String): Option[LearningAlgorithm] = {
            if(validAgorithms.keySet.contains(algorithmName)) return Some(validAgorithms(algorithmName))
            None
        }
    }


    def getValidAlgorithms :scala.collection.immutable.Map[String, LearningAlgorithm] = {
        LearningAlgorithms.validAgorithms
    }


    def getValidAlgorithmsFromRequest(requestUriPart: List[String]) : List[String] = {
        requestUriPart.filter(LearningAlgorithms(_).isDefined).toList
    }


    def isValidInput (inputText: String) : Boolean = {
        (Config.Form.ValidationValues.INPUT_TEXT_MINIMUM_LENGTH until Config.Form.ValidationValues.INPUT_TEXT_MAXIMUM_LENGTH) contains inputText.size
    }


    def getCleanedRequest(evaluationRequest: EvaluationRequest) : Option[EvaluationRequest] = {
        val cleanInputText = evaluationRequest.inputText.trim
        val validAlgorithms = getValidAlgorithmsFromRequest(evaluationRequest.inputAlgorithms)

        (isValidInput(cleanInputText) && validAlgorithms.nonEmpty) match {
            case true => Some(EvaluationRequest(cleanInputText, validAlgorithms))
            case _ => None
        }
    }
}
