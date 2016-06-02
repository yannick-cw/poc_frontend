package models

import app.Config
import controllers.EvaluationRequest
import play.api.data.Form
import play.api.data.Forms._

/**
  * Created by 437580 on 02/06/16.
  */
trait FormValidation {

  val userInputForm = Form(
    mapping(
      "inputText" -> text(
        minLength = Config.Form.ValidationValues.INPUT_TEXT_MINIMUM_LENGTH,
        maxLength = Config.Form.ValidationValues.INPUT_TEXT_MAXIMUM_LENGTH
      ),

      "inputAlgorithms" -> list(nonEmptyText)

    )(EvaluationRequest.apply)(EvaluationRequest.unapply)
  )

}
