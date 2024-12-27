package es.uma.quiziosity.data.repository

import com.deepl.api.Translator
import es.uma.quiziosity.BuildConfig
import es.uma.quiziosity.data.api.TriviaApiSingleton
import es.uma.quiziosity.data.model.Question

object TriviaRepository {

    private const val DEEPL_AUTH_KEY = BuildConfig.DEEPL_API_KEY

    // Get trivia questions and translate them
    suspend fun getTriviaQuestions(
        category: String = "any",
        targetLang: String? = null
    ): List<Question> {
        // Fetch trivia questions
        val triviaQuestions = TriviaApiSingleton.getTriviaQuestions(category)

        // If no target language is specified, return the original questions
        if (targetLang == null || targetLang == "en") {
            return triviaQuestions
        }

        // Extract the questions (or any other parts of the question you want to translate)
        val questionsToTranslate = triviaQuestions.map { it.question }

        // Define the translator
        val translator = Translator(DEEPL_AUTH_KEY)

        // Define source language
        val sourceLang = "en"

        // Translate the questions using DeepL API
        val translatedQuestionsResponse =
            translator.translateText(questionsToTranslate, sourceLang, targetLang)

        // Map the translated questions to the original questions
        return triviaQuestions.mapIndexed { index, question ->
            question.copy(question = translatedQuestionsResponse[index].text)
        }
    }
}
