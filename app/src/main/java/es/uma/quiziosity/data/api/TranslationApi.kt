package es.uma.quiziosity.data.api

import es.uma.quiziosity.data.model.TranslationRequest
import es.uma.quiziosity.data.model.TranslationResponse
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Body

// Define the interface that Retrofit will use to create the API calls
interface TranslationApi {

    // Method to translate text using DeepL API
    @POST("v2/translate")
    suspend fun translateText(
        @Query("auth_key") authKey: String,
        @Query("target_lang") targetLang: String,
        @Body request: TranslationRequest
    ): TranslationResponse
}