package models

import app.Config
import controllers.{EvaluationRequest, TwitterRequest}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

/**
  * Created by 437580 on 02/06/16.
  */
trait FormValidation {
  def nonEmptyList[T]: Constraint[List[T]] = Constraint[List[T]]("constraint.required") { o =>
    if (o.nonEmpty) Valid else Invalid(ValidationError("error.required"))
  }

  val userInputForm = Form(
    mapping(
      "inputText" -> text(
        minLength = Config.Form.ValidationValues.INPUT_TEXT_MINIMUM_LENGTH,
        maxLength = Config.Form.ValidationValues.INPUT_TEXT_MAXIMUM_LENGTH
      ),

      "inputAlgorithms" -> list(text).verifying(nonEmptyList)

    )(EvaluationRequest.apply)(EvaluationRequest.unapply)
  )

    val twitterRequestForm = Form(
        mapping(
            "username" -> nonEmptyText
        )(TwitterRequest.apply)(TwitterRequest.unapply)
    )

}
