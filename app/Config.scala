package app

object Config {

    object Defaults  {
        val APPLICATION_TITLE = "POC - Political Opinion Classifier"
    }

    object Form  {

        object Info  {

            val FORM_LEGEND = "Welcome to POC - your political opinion classifier"
            val FORM_HELP = "Enter some text in the text field and choose an algorithm to be used for classifying of your political opinion"
            val FORM_INPUT_TEXT_PLACEHOLDER = "Please enter your some lines of text about your political opinion..."
        }

        object Error {

            val FORM_INPUT_TEXT_ERROR = "Please enter a valid input text"
            val SELECTED_ALGORITHMS_ERROR = "Please select at least one algorithm"
            val ERROR_DURING_REQUEST = "Error while requesting your classification"
        }

        object ValidationValues {
            val INPUT_TEXT_MINIMUM_LENGTH = 10
            val INPUT_TEXT_MAXIMUM_LENGTH = 10000
        }

    }

}
