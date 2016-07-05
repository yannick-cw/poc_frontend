package models

import app.Config
import controllers.EvaluationRequest
import akka.http.scaladsl.model._


object ValidatorModel {

    case class InputData(inputText: String, usedAlgorithms: List[LearningAlgorithm])

    abstract class LearningAlgorithm(val name: String)

    object LearningAlgorithmTypes {
        case object NaiveBayes extends LearningAlgorithm("NaiveBayes")
        case object NaiveBayesWithTfIdf extends LearningAlgorithm("NaiveBayes with TF*IDF")
        case object NaiveBayesNet extends LearningAlgorithm("BayesNet")
        case object NaiveBayesMultiNomial extends LearningAlgorithm("NaiveBayes Multinomial")
        case object KNN extends LearningAlgorithm("k-Nearest Neighbors")
    }


    object LearningAlgorithms {
        val validAgorithms:scala.collection.immutable.Map[String, LearningAlgorithm] = Map(
            "naive_bayes" -> LearningAlgorithmTypes.NaiveBayes,
//            "naive_bayes_tfidf" -> LearningAlgorithmTypes.NaiveBayesWithTfIdf,
            "bayes_net" -> LearningAlgorithmTypes.NaiveBayesNet,
            "bayes_multinomial" -> LearningAlgorithmTypes.NaiveBayesMultiNomial,
            "knn" -> LearningAlgorithmTypes.KNN
        )


        def apply(algorithmName: String): Option[LearningAlgorithm] = {
            if(validAgorithms.keySet.contains(algorithmName)) return Some(validAgorithms(algorithmName))
            None
        }
    }


    def getValidAlgorithms :scala.collection.immutable.Map[String, LearningAlgorithm] = {
        LearningAlgorithms.validAgorithms
    }

    def getAlgorithmsByName(algorithmNames: List[String]) : Map[String, LearningAlgorithm] = {
       getValidAlgorithms.filter(algorithmNames contains _._1)
    }

    def getValidAlgorithNamesFromRequest(requestUriPart: List[String]) : List[String] = {
        requestUriPart.filter(LearningAlgorithms(_).isDefined).toList
    }


    def isValidInput (inputText: String) : Boolean = {
        (Config.Form.ValidationValues.INPUT_TEXT_MINIMUM_LENGTH until Config.Form.ValidationValues.INPUT_TEXT_MAXIMUM_LENGTH) contains inputText.size
    }


    def getCleanedRequest(evaluationRequest: EvaluationRequest) : Option[EvaluationRequest] = {
        val cleanInputText = evaluationRequest.inputText.trim
        val validAlgorithms = getValidAlgorithNamesFromRequest(evaluationRequest.inputAlgorithms)

        (isValidInput(cleanInputText) && validAlgorithms.nonEmpty) match {
            case true => Some(EvaluationRequest(cleanInputText, validAlgorithms))
            case _ => None
        }
    }
}
