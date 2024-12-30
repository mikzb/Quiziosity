// SinglePlayerActivity.kt
package es.uma.quiziosity

import android.os.Bundle

class SinglePlayerActivity : GameActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Any multi-player specific initialization can go here
    }

    override fun showNextQuestion() {
        super.showNextQuestion()
        // Any multi-player specific logic for showing the next question can go here
    }

    override fun endGame() {
        super.endGame()
        // Any multi-player specific logic for ending the game can go here
    }
}