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

class MultiPlayerActivity : GameActivity() {

    private var currentPlayer = 1
    private var player1Score = 0
    private var player2Score = 0
    private lateinit var playerName1: String
    private lateinit var playerName2: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve player names from SharedPreferences
        val sharedPreferences = QuiziosityApp.getSharedPreferences()
        playerName1 = sharedPreferences.getString("player_name_1", getString(R.string.guest))!!
        playerName2 = sharedPreferences.getString("player_name_2", getString(R.string.guest))!!

        // Set the initial player name in the current_player TextView
        binding.currentPlayer.text = playerName1
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
        updateCurrentPlayer()
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

                    updateCurrentPlayer()

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

    private fun updateCurrentPlayer() {
        if (currentPlayer == 1) {
            binding.currentPlayer.text = playerName1
            player1Score.toString().also { binding.currentScore.text = it }
        } else {
            binding.currentPlayer.text = playerName2
            player2Score.toString().also { binding.currentScore.text = it }
        }
    }

    override fun endGame() {
        super.endGame()
        showEndGameDialog()
    }

    private fun showEndGameDialog() {
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