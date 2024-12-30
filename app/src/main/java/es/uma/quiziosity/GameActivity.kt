package es.uma.quiziosity

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import es.uma.quiziosity.data.model.Question
import es.uma.quiziosity.data.repository.TriviaRepository
import es.uma.quiziosity.databinding.ActivityGameBinding
import es.uma.quiziosity.utils.UserUtils
import kotlinx.coroutines.launch

class GameActivity : BaseActivity() {

    private var isAnswerChecked: Boolean = false
    private lateinit var binding: ActivityGameBinding
    private val hideHandler = Handler(Looper.myLooper()!!)

    private lateinit var questions: List<Question>
    private lateinit var currentQuestion: Question
    private lateinit var countDownTimer: CountDownTimer

    private var consecutiveCorrectAnswers: Int = 0
    private var score: Int = 0

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var correctAnswerMediaPlayer: MediaPlayer
    private lateinit var wrongAnswerMediaPlayer: MediaPlayer

    private val hidePart2Runnable = Runnable {
        binding.root.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
    }
    private val showPart2Runnable = Runnable {
        supportActionBar?.show()
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get normalized volume
        val normalizedVolume = getNormalizedVolume()

        // Initialize MediaPlayers
        mediaPlayer = MediaPlayer.create(this, R.raw.tic_tac_sound).apply {
            isLooping = true
            setVolume(normalizedVolume, normalizedVolume)
        }
        correctAnswerMediaPlayer = MediaPlayer.create(this, R.raw.correct_answer_sound).apply {
            setVolume(normalizedVolume, normalizedVolume)
        }
        wrongAnswerMediaPlayer = MediaPlayer.create(this, R.raw.wrong_answer_sound).apply {
            setVolume(normalizedVolume, normalizedVolume)
        }

        resetButtons()

        lifecycleScope.launch {
            val categories = sharedPreferences.getStringSet("categories", setOf())!!
            val language = sharedPreferences.getString("language", "en")!!
            questions = TriviaRepository.getTriviaQuestions(categories.toString(), language)
            if (questions.isNotEmpty()) {
                currentQuestion = questions[0]
                displayQuestion(currentQuestion)
            }
        }

        val answerButtons = listOf(binding.answer1, binding.answer2, binding.answer3, binding.answer4)
        answerButtons.forEach { button ->
            button.setOnClickListener {
                animateButton(it)
                checkAnswer(button.text.toString(), currentQuestion.correctAnswer)
                countDownTimer.cancel()
            }
        }
    }

    private fun displayQuestion(question: Question) {
        val answers = mutableListOf(question.correctAnswer).apply {
            addAll(question.incorrectAnswers)
            shuffle()
        }

        binding.questionText.text = question.question.text
        binding.answer1.text = answers[0]
        binding.answer2.text = answers[1]
        binding.answer3.text = answers[2]
        binding.answer4.text = answers[3]

        animateViewAppearance(binding.questionText)
        animateViewAppearance(binding.answer1)
        animateViewAppearance(binding.answer2)
        animateViewAppearance(binding.answer3)
        animateViewAppearance(binding.answer4)

        resetProgressBar()
        startTimer()
    }

    private fun startTimer(bonusTime: Long = 0L) {
        cancelTimer()

        val totalTime = 10000L + bonusTime
        countDownTimer = object : CountDownTimer(totalTime, 10) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerText.text = getString(R.string.time_remaining, (millisUntilFinished / 1000).toString())
                binding.timerProgressBar.progress = (millisUntilFinished * 1000 / totalTime).toInt()

                // Play and loop the countdown sound
                if (!mediaPlayer.isPlaying) {
                    try {
                        mediaPlayer.reset() // Reset the MediaPlayer
                        mediaPlayer.setDataSource(applicationContext, Uri.parse("android.resource://${packageName}/raw/tic_tac_sound"))
                        mediaPlayer.prepare()
                        mediaPlayer.isLooping = true
                        mediaPlayer.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFinish() {
                checkAnswer("", currentQuestion.correctAnswer)
                binding.timerProgressBar.progress = 0
                consecutiveCorrectAnswers = 0

                try {
                    wrongAnswerMediaPlayer.reset()
                    wrongAnswerMediaPlayer.setDataSource(applicationContext, Uri.parse("android.resource://${packageName}/raw/wrong_answer_sound"))
                    wrongAnswerMediaPlayer.prepare()
                    wrongAnswerMediaPlayer.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                triggerVisualEffect()

                Handler(Looper.getMainLooper()).postDelayed({
                    showNextQuestion()
                }, 1500)
            }
        }.start()
    }



    private fun triggerVisualEffect() {
        val animator = ObjectAnimator.ofFloat(binding.root, "alpha", 1f, 0f, 1f)
        animator.duration = 500
        animator.start()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(100)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()

        // Release MediaPlayers
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        if (::correctAnswerMediaPlayer.isInitialized) {
            correctAnswerMediaPlayer.stop()
            correctAnswerMediaPlayer.release()
        }
        if (::wrongAnswerMediaPlayer.isInitialized) {
            wrongAnswerMediaPlayer.stop()
            wrongAnswerMediaPlayer.release()
        }
    }


    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        supportActionBar?.hide()
        isFullscreen = false
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        binding.root.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        isFullscreen = true
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        private const val AUTO_HIDE = true
        private const val AUTO_HIDE_DELAY_MILLIS = 3000
        private const val UI_ANIMATION_DELAY = 300
    }

    private fun animateButton(button: View) {
        button.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(150)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun checkAnswer(selectedAnswer: String, correctAnswer: String) {
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

                    consecutiveCorrectAnswers++
                    val timeLeft = binding.timerProgressBar.progress
                    score += 10 + (timeLeft / 10)
                    startTimer(consecutiveCorrectAnswers * 2000L)
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
                    consecutiveCorrectAnswers = 0
                }
            }
            button.isClickable = false
        }

        Handler(Looper.getMainLooper()).postDelayed({
            showNextQuestion()
        }, 1500)
    }



    private fun resetButtons() {
        val buttons = listOf(binding.answer1, binding.answer2, binding.answer3, binding.answer4)
        buttons.forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
            button.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            button.isClickable = true
        }
    }

    private fun showNextQuestion() {
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
    }

    private fun resetProgressBar() {
        binding.timerProgressBar.progress = 1000
    }

    private fun cancelTimer() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
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

    private fun endGame() {
        binding.answer1.visibility = View.GONE
        binding.answer2.visibility = View.GONE
        binding.answer3.visibility = View.GONE
        binding.answer4.visibility = View.GONE
        binding.timerText.visibility = View.GONE
        binding.timerProgressBar.visibility = View.GONE

        if(score > (UserUtils.getBestScore()?.toInt() ?: 0)) {
            UserUtils.getUsername()?.let { username ->
                lifecycleScope.launch {
                    QuiziosityApp.getDatabase().userDao().updateScore(username, score)
                    QuiziosityApp.getSharedPreferences().edit().putString("best_score", score.toString()).apply()
                }
            }
        }
        showEndGameDialog()
    }

    private fun getNormalizedVolume(): Float {
        val volume = sharedPreferences.getInt("volume", 50)
        return volume / 100f // Convert volume to a float between 0.0 and 1.0
    }
}

private fun animateViewAppearance(view: View) {
    view.alpha = 0f
    view.visibility = View.VISIBLE
    view.animate()
        .alpha(1f)
        .setDuration(500)
        .setListener(null)
}