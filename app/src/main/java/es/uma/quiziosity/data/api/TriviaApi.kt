package es.uma.quiziosity.data.api

import es.uma.quiziosity.data.model.Question
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Define the interface that Retrofit will use to create the API calls
interface TriviaApi {

    // Define a method to get trivia questions
    @GET("questions")
    suspend fun getTriviaQuestions(
        @Query("categories") category : String,
    ): List<Question>
}
