// SinglePlayerActivity.kt
package es.uma.quiziosity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import es.uma.quiziosity.utils.UserUtils
import kotlinx.coroutines.launch

class SinglePlayerActivity : GameActivity() {

    override fun endGame() {
        super.endGame()
        // Any multi-player specific logic for ending the game can go here
        if(score > (UserUtils.getBestScore()?.toInt() ?: 0)) {
            UserUtils.getUsername()?.let { username ->
                lifecycleScope.launch {
                    QuiziosityApp.getUserRepository().updateScore(username, score)
                    QuiziosityApp.getSharedPreferences().edit().putString("best_score", score.toString()).apply()
                }
            }
        }
        showEndGameDialog()
    }
    private fun showEndGameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.game_over))
        builder.setMessage(getString(R.string.your_score, score.toString()))
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }
}