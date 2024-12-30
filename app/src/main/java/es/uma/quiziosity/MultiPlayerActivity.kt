// MultiPlayerActivity.kt
package es.uma.quiziosity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MultiPlayerActivity : GameActivity() {

    private var currentPlayer = 1
    private var player1Score = 0
    private var player2Score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Any multi-player specific initialization can go here
    }

    override fun showNextQuestion() {
        resetButtons()
        cancelTimer()

        isAnswerChecked = false

        val nextIndex = questions.indexOf(currentQuestion) + 1
        if (nextIndex < questions.size) {
            currentQuestion = questions[nextIndex]
            displayQuestion(currentQuestion)
        } else {
            endGame()
        }

        // Switch player turn
        currentPlayer = if (currentPlayer == 1) 2 else 1
    }

    override fun checkAnswer(selectedAnswer: String, correctAnswer: String) {
        if (isAnswerChecked) return
        isAnswerChecked = true

        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        val buttons = listOf(binding.answer1, binding.answer2, binding.answer3, binding.answer4)

        buttons.forEach { button ->
            val answer = button.text.toString()
            if (answer == correctAnswer) {
                button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
                if (selectedAnswer == correctAnswer) {
                    try {
                        correctAnswerMediaPlayer.reset()
                        correctAnswerMediaPlayer.setDataSource(applicationContext, Uri.parse("android.resource://${packageName}/raw/correct_answer_sound"))
                        correctAnswerMediaPlayer.prepare()
                        correctAnswerMediaPlayer.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val timeLeft = binding.timerProgressBar.progress
                    val scoreIncrement = 10 + (timeLeft / 10)
                    if (currentPlayer == 1) {
                        player1Score += scoreIncrement
                    } else {
                        player2Score += scoreIncrement
                    }
                    startTimer()
                }
            } else {
                button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                if (selectedAnswer == answer) {
                    try {
                        wrongAnswerMediaPlayer.reset()
                        wrongAnswerMediaPlayer.setDataSource(applicationContext, Uri.parse("android.resource://${packageName}/raw/wrong_answer_sound"))
                        wrongAnswerMediaPlayer.prepare()
                        wrongAnswerMediaPlayer.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            button.isClickable = false
        }

        Handler(Looper.getMainLooper()).postDelayed({
            showNextQuestion()
        }, 1500)
    }

    override fun endGame() {
        binding.answer1.visibility = View.GONE
        binding.answer2.visibility = View.GONE
        binding.answer3.visibility = View.GONE
        binding.answer4.visibility = View.GONE
        binding.timerText.visibility = View.GONE
        binding.timerProgressBar.visibility = View.GONE

        showEndGameDialog()
    }

    private fun showEndGameDialog() {
        val sharedPreferences = QuiziosityApp.getSharedPreferences()
        val playerName1 = sharedPreferences.getString("player_name_1", getString(R.string.guest))
        val playerName2 = sharedPreferences.getString("player_name_2", getString(R.string.guest))

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.game_over))
        builder.setMessage(getString(R.string.multiplayer_scores, playerName1, player1Score.toString(), playerName2, player2Score.toString()))
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