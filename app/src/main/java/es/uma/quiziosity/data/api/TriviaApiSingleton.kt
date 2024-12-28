package es.uma.quiziosity.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TriviaApiSingleton {
    private const val BASE_URL = "https://the-trivia-api.com/v2/"

    private val triviaApi: TriviaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TriviaApi::class.java)
    }
    suspend fun getTriviaQuestions(category: String = "any") = triviaApi.getTriviaQuestions(category)
    suspend fun getCategories() = triviaApi.getCategories()
}