package es.uma.quiziosity.data.repository

import es.uma.quiziosity.BuildConfig
import es.uma.quiziosity.data.api.TriviaApiSingleton
import es.uma.quiziosity.data.api.TranslationApiSingleton
import es.uma.quiziosity.data.model.Question

object TriviaRepository {

    private const val DEEPL_AUTH_KEY = BuildConfig.DEEPL_API_KEY

    // Get trivia questions and translate them
    suspend fun getTranslatedTriviaQuestions(category: String = "any", targetLang: String): List<Question> {
        // Fetch trivia questions
        val triviaQuestions = TriviaApiSingleton.getTriviaQuestions(category)

        // Extract the questions (or any other parts of the question you want to translate)
        val questionsToTranslate = triviaQuestions.map { it.question }

        // Translate the questions using DeepL API
        val translatedQuestionsResponse = TranslationApiSingleton.translateText(
            DEEPL_AUTH_KEY,
            targetLang,
            questionsToTranslate
        )

        // Map the translated text back to the trivia questions
        return triviaQuestions.zip(translatedQuestionsResponse.translations).map { (original, translation) ->
            original.copy(question = translation.text)
        }
    }
}
