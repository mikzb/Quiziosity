package es.uma.quiziosity.data.repository

import com.deepl.api.DeepLException
import com.deepl.api.Translator
import es.uma.quiziosity.BuildConfig
import es.uma.quiziosity.data.api.TriviaApiSingleton
import es.uma.quiziosity.data.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TriviaRepository {

    private const val DEEPL_AUTH_KEY = BuildConfig.DEEPL_API_KEY

    // Get trivia questions and translate them
    suspend fun getTriviaQuestions(
        categories: String = "any",
        targetLang: String? = null
    ): List<Question> {
        // Fetch trivia questions
        val triviaQuestions = TriviaApiSingleton.getTriviaQuestions(categories)

        // If no target language is specified, return the original questions
        if (targetLang == null || targetLang == "en") {
            return triviaQuestions
        }

        // Extract the texts to translate
        val textsToTranslate = triviaQuestions.flatMap { question ->
            listOf(question.question.text, question.correctAnswer) + question.incorrectAnswers
        }

        // Define the translator
        val translator = Translator(DEEPL_AUTH_KEY)

        // Define source language
        val sourceLang = "en"

        return try {
            // Translate the texts using DeepL API
            val translatedTextsResponse = withContext(Dispatchers.IO) {
                translator.translateText(textsToTranslate, sourceLang, targetLang)
            }

            // Map the translated texts back to the original questions
            var index = 0
            triviaQuestions.map { question ->
                val translatedQuestionText = translatedTextsResponse[index++].text
                val translatedCorrectAnswer = translatedTextsResponse[index++].text
                val translatedIncorrectAnswers = question.incorrectAnswers.map { translatedTextsResponse[index++].text }

                question.copy(
                    question = question.question.copy(text = translatedQuestionText),
                    correctAnswer = translatedCorrectAnswer,
                    incorrectAnswers = translatedIncorrectAnswers
                )
            }
        } catch (e: DeepLException) {
            // Handle translation errors
            e.printStackTrace()
            // Return the original questions if translation fails
            triviaQuestions
        }
    }
}