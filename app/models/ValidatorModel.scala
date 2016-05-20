package models

import app.Config
import controllers.EvaluationRequest

object ValidatorModel {

    case class InputData(inputText: String, usedAlgorithms: List[LearningAlgorithm])

    abstract class LearningAlgorithm(val name: String)

    object LearningAlgorithmTypes {
        case object NaiveBayes extends LearningAlgorithm("Naive Bayes")
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
