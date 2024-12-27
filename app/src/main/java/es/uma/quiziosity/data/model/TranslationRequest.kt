package es.uma.quiziosity.data.model

// Request body data class for DeepL's translation API
data class TranslationRequest(
    val text: List<String> // Text to be translated
)
