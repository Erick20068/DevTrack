package com.erickguaillas.devtrack.ui.repaso

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.erickguaillas.devtrack.R
import com.erickguaillas.devtrack.data.Content
import com.erickguaillas.devtrack.data.Deck
import com.erickguaillas.devtrack.data.Quiz
import com.erickguaillas.devtrack.databinding.FragmentRepasoBinding

class RepasoFragment : Fragment(R.layout.fragment_repaso) {

    private var _b: FragmentRepasoBinding? = null
    private val b get() = _b!!

    private var quizMode = false

    // Flashcards state
    private var deck: Deck = Content.decks.first()
    private var order: List<Int> = deck.cards.indices.toList()
    private var index = 0
    private var showingFront = true

    // Quiz state
    private var quiz: Quiz = Content.quizzes.first()
    private var qIndex = 0
    private var score = 0
    private var answered = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentRepasoBinding.bind(view)

        b.btnModeCards.setOnClickListener { setMode(false) }
        b.btnModeQuiz.setOnClickListener { setMode(true) }

        b.spinnerDeck.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, position: Int, id: Long) {
                if (quizMode) {
                    quiz = Content.quizzes[position]
                    startQuiz()
                } else {
                    deck = Content.decks[position]
                    shuffleOrder()
                    showCard()
                }
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        b.btnFlip.setOnClickListener { flipCard() }
        b.btnNext.setOnClickListener { if (index < order.size - 1) { index++; showingFront = true; showCard() } }
        b.btnPrev.setOnClickListener { if (index > 0) { index--; showingFront = true; showCard() } }
        b.btnShuffle.setOnClickListener { shuffleOrder(); Toast.makeText(context, "Mezcladas 🔀", Toast.LENGTH_SHORT).show() }

        val optButtons = listOf(b.btnOpt1, b.btnOpt2, b.btnOpt3, b.btnOpt4)
        optButtons.forEachIndexed { i, btn -> btn.setOnClickListener { answer(i) } }
        b.btnNextQ.setOnClickListener { nextQuestion() }

        setMode(false)
    }

    // ─── Modo ───
    private fun setMode(quiz: Boolean) {
        quizMode = quiz
        b.containerCards.visibility = if (quiz) View.GONE else View.VISIBLE
        b.containerQuiz.visibility = if (quiz) View.VISIBLE else View.GONE

        val labels = if (quiz) Content.quizzes.map { it.label } else Content.decks.map { it.label }
        b.spinnerDeck.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, labels)
        b.spinnerDeck.setSelection(0)
        if (quiz) { this.quiz = Content.quizzes.first(); startQuiz() }
        else { deck = Content.decks.first(); shuffleOrder(); showCard() }
    }

    // ─── Flashcards ───
    private fun shuffleOrder() {
        order = deck.cards.indices.shuffled()
        index = 0
        showingFront = true
    }

    private fun showCard() {
        val card = deck.cards[order[index]]
        b.tvCard.text = if (showingFront) card.front else card.back
        b.tvCounter.text = "${deck.emoji} ${index + 1}/${order.size} · toca la tarjeta para voltear"
        b.cardFlash.setOnClickListener { flipCard() }
    }

    private fun flipCard() {
        val flip1 = ObjectAnimator.ofFloat(b.cardFlash, "rotationY", 0f, 90f).setDuration(140)
        flip1.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                showingFront = !showingFront
                showCard()
                b.cardFlash.rotationY = -90f
                ObjectAnimator.ofFloat(b.cardFlash, "rotationY", -90f, 0f).setDuration(140).start()
            }
        })
        flip1.start()
    }

    // ─── Quiz ───
    private fun startQuiz() {
        qIndex = 0
        score = 0
        renderQuestion()
    }

    private fun renderQuestion() {
        answered = false
        val q = quiz.questions[qIndex]
        b.tvQCounter.text = "${quiz.emoji} Pregunta ${qIndex + 1}/${quiz.questions.size} · Puntaje: $score"
        b.tvQuestion.text = q.question
        b.tvExplanation.visibility = View.GONE
        b.btnNextQ.visibility = View.GONE

        val buttons = listOf(b.btnOpt1, b.btnOpt2, b.btnOpt3, b.btnOpt4)
        buttons.forEachIndexed { i, btn ->
            btn.text = q.options[i]
            btn.alpha = 1f
            btn.isEnabled = true
        }
    }

    private fun answer(chosen: Int) {
        if (answered) return
        answered = true
        val q = quiz.questions[qIndex]
        val buttons = listOf(b.btnOpt1, b.btnOpt2, b.btnOpt3, b.btnOpt4)

        if (chosen == q.correctIndex) score++
        buttons[q.correctIndex].alpha = 1f
        buttons.forEachIndexed { i, btn ->
            btn.alpha = when {
                i == q.correctIndex -> 1f
                i == chosen -> 0.55f
                else -> 0.35f
            }
            btn.isEnabled = false
        }
        b.tvExplanation.text = (if (chosen == q.correctIndex) "✅ ¡Correcto! " else "❌ Incorrecto. ") + q.explanation
        b.tvExplanation.visibility = View.VISIBLE
        b.btnNextQ.text = if (qIndex == quiz.questions.size - 1) "Ver resultado 🏁" else "Siguiente ➜"
        b.btnNextQ.visibility = View.VISIBLE
    }

    private fun nextQuestion() {
        if (qIndex < quiz.questions.size - 1) {
            qIndex++
            renderQuestion()
        } else {
            val total = quiz.questions.size
            b.tvQCounter.text = "🏁 Resultado final"
            b.tvQuestion.text = "$score / $total correctas"
            b.tvExplanation.text = when {
                score == total -> "PERFECTO 🏆 — dominado, pásale a la siguiente baraja."
                score >= total * 0.7 -> "Muy bien 💪 — repasa las falladas y sube."
                else -> "Toca repasar 📚 — vuelve a las flashcards y reintenta."
            }
            b.tvExplanation.visibility = View.VISIBLE
            listOf(b.btnOpt1, b.btnOpt2, b.btnOpt3, b.btnOpt4).forEach { it.visibility = View.GONE }
            b.btnNextQ.text = "Reiniciar quiz 🔄"
            b.btnNextQ.visibility = View.VISIBLE
            b.btnNextQ.setOnClickListener {
                listOf(b.btnOpt1, b.btnOpt2, b.btnOpt3, b.btnOpt4).forEach { it.visibility = View.VISIBLE }
                b.btnNextQ.setOnClickListener { nextQuestion() }
                startQuiz()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
