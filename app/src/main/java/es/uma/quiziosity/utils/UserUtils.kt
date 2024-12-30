// Utils.kt
package es.uma.quiziosity.utils

import es.uma.quiziosity.QuiziosityApp

object UserUtils {
    fun isUserLoggedIn(): Boolean {
        return QuiziosityApp.getSharedPreferences().getString("username", null) != null
    }
    fun getUsername(): String? {
        return QuiziosityApp.getSharedPreferences().getString("username", null)
    }
    fun getBestScore(): String? {
        return QuiziosityApp.getSharedPreferences().getString("best_score", "0")
    }
}