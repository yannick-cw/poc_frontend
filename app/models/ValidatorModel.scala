package models

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


    def validateRequestedAlgorithms(algorithm: String): Boolean = {
        LearningAlgorithms(algorithm).isDefined
    }


    def getValidAlgorithmsFromRequest(requestUriPart: String) : List[String] = {
        requestUriPart.split("/").filter(validateRequestedAlgorithms(_)).toList
    }
}