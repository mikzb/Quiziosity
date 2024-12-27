package es.uma.quiziosity.data.model

// Response data class for DeepL's translation API
data class TranslationResponse(
    val translations: List<TranslationResult>
)