package es.uma.quiziosity.data.model

data class Question(
    val id: String,                // Unique identifier for the question
    val question: QuestionText,    // The trivia question text as an object
    val correctAnswer: String,     // The correct answer
    val incorrectAnswers: List<String>,  // A list of incorrect answers
    val category: String,          // The category of the trivia question
    val difficulty: String,        // The difficulty level (e.g., "easy", "medium", "hard")
    val type: String,              // The type of question (e.g., "multiple" for multiple-choice)
    val tags: List<String>,        // Tags associated with the question
    val regions: List<String>,     // Regions associated with the question
    val isNiche: Boolean           // Whether the question is niche
)