package es.uma.quiziosity.data.api

import es.uma.quiziosity.data.model.TranslationRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TranslationApiSingleton {
    private const val BASE_URL = "https://api-free.deepl.com/"

    private val translationApi: TranslationApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApi::class.java)
    }

    suspend fun translateText(authKey: String, targetLang: String, text: List<String>) =
        translationApi.translateText(authKey, targetLang, TranslationRequest(text))
}
