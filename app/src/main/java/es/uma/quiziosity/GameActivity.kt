package es.uma.quiziosity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import es.uma.quiziosity.data.model.Question
import es.uma.quiziosity.data.repository.TriviaRepository
import es.uma.quiziosity.databinding.ActivityGameBinding
import es.uma.quiziosity.utils.LocaleHelper
import kotlinx.coroutines.launch

class GameActivity : BaseActivity() {

    private lateinit var binding: ActivityGameBinding
    private val hideHandler = Handler(Looper.myLooper()!!)

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        binding.root.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
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
            else -> {
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch questions in a coroutine
        lifecycleScope.launch {
            val categories = sharedPreferences.getStringSet("categories", setOf())!!
            val language = sharedPreferences.getString("language", "en")!!
            val questions = TriviaRepository.getTriviaQuestions(categories.toString(), language)
            if (questions.isNotEmpty()) {
                val question = questions[0]
                displayQuestion(question)
            }
        }

        // Apply animation to buttons
        binding.answer1.setOnClickListener {
            animateButton(it)
            // Handle answer 1 click
        }
        binding.answer2.setOnClickListener {
            animateButton(it)
            // Handle answer 2 click
        }
        binding.answer3.setOnClickListener {
            animateButton(it)
            // Handle answer 3 click
        }
        binding.answer4.setOnClickListener {
            animateButton(it)
            // Handle answer 4 click
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
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(100)
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
        val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.2f, 1f)
        scaleX.duration = 300
        scaleY.duration = 300
        scaleX.start()
        scaleY.start()
    }
}