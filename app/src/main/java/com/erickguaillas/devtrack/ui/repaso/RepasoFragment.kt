package com.erickguaillas.devtrack.ui.repaso

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.erickguaillas.devtrack.R
import com.erickguaillas.devtrack.data.Content
import com.erickguaillas.devtrack.data.Deck
import com.erickguaillas.devtrack.data.DevRepository
import com.erickguaillas.devtrack.data.Quiz
import com.erickguaillas.devtrack.databinding.FragmentRepasoBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class RepasoFragment : Fragment(R.layout.fragment_repaso) {

    private var _b: FragmentRepasoBinding? = null
    private val b get() = _b!!

    private lateinit var repo: DevRepository
    private var mode = 0 // 0 = flashcards, 1 = quiz, 2 = práctica

    private var deck: Deck = Content.decks.first()
    private var order: List<Int> = deck.cards.indices.toList()
    private var index = 0
    private var showingFront = true

    private var quiz: Quiz = Content.quizzes.first()
    private var qIndex = 0
    private var score = 0
    private var answered = false

    private var practicePack = Content.practicePacks.first()
    private lateinit var practiceAdapter: PracticeAdapter
    private var donePracticeIds: Set<String> = emptySet()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentRepasoBinding.bind(view)
        repo = DevRepository(requireContext())

        b.btnModeCards.setOnClickListener { setMode(0) }
        b.btnModeQuiz.setOnClickListener { setMode(1) }
        b.btnModePractice.setOnClickListener { setMode(2) }

        b.spinnerDeck.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
            when (mode) {
                0 -> { deck = Content.decks[position]; shuffleOrder(); showCard() }
                1 -> { quiz = Content.quizzes[position]; startQuiz() }
                2 -> { practicePack = Content.practicePacks[position]; bindPractice() }
            }
        }

        b.btnFlip.setOnClickListener { flipCard() }
        b.btnNext.setOnClickListener { if (index < order.size - 1) { index++; showingFront = true; showCard() } }
        b.btnPrev.setOnClickListener { if (index > 0) { index--; showingFront = true; showCard() } }
        b.btnShuffle.setOnClickListener { shuffleOrder(); Toast.makeText(context, "Mezcladas 🔀", Toast.LENGTH_SHORT).show() }

        val optButtons = listOf(b.btnOpt1, b.btnOpt2, b.btnOpt3, b.btnOpt4)
        optButtons.forEachIndexed { i, btn -> btn.setOnClickListener { answer(i) } }
        b.btnNextQ.setOnClickListener { nextQuestion() }

        practiceAdapter = PracticeAdapter { itemId ->
            lifecycleScope.launch { repo.toggleCheck("practice", itemId) }
        }
        b.rvPractice.layoutManager = LinearLayoutManager(requireContext())
        b.rvPractice.adapter = practiceAdapter

        repo.checksForDay("practice").observe(viewLifecycleOwner) { checks ->
            donePracticeIds = checks.filter { it.done }.map { it.itemId }.toSet()
            practiceAdapter.submit(practicePack.items, donePracticeIds)
            updatePracticeProgress()
        }

        setMode(0)
    }

    // ─── Modo (con estado activo visible) ───
    private fun setMode(newMode: Int) {
        mode = newMode

        b.containerCards.visibility = if (newMode == 0) View.VISIBLE else View.GONE
        b.containerQuiz.visibility = if (newMode == 1) View.VISIBLE else View.GONE
        b.containerPractice.visibility = if (newMode == 2) View.VISIBLE else View.GONE

        styleModeButtons(newMode)

        val labels = when (newMode) {
            0 -> Content.decks.map { it.label }
            1 -> Content.quizzes.map { it.label }
            else -> Content.practicePacks.map { it.label }
        }
        b.spinnerDeck.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels))
        b.spinnerDeck.setText(labels.first(), false)

        when (newMode) {
            0 -> { deck = Content.decks.first(); shuffleOrder(); showCard() }
            1 -> { quiz = Content.quizzes.first(); startQuiz() }
            else -> { practicePack = Content.practicePacks.first(); bindPractice() }
        }
    }

    private fun styleModeButtons(active: Int) {
        val primary = ContextCompat.getColor(requireContext(), R.color.primary)
        val card = ContextCompat.getColor(requireContext(), R.color.card)
        val textColor = ContextCompat.getColor(requireContext(), R.color.text)
        val onPrimary = ContextCompat.getColor(requireContext(), R.color.bg)

        listOf(b.btnModeCards, b.btnModeQuiz, b.btnModePractice).forEachIndexed { i, btn: MaterialButton ->
            if (i == active) {
                btn.backgroundTintList = ColorStateList.valueOf(primary)
                btn.setTextColor(onPrimary)
            } else {
                btn.backgroundTintList = ColorStateList.valueOf(card)
                btn.setTextColor(textColor)
            }
        }
    }

    private fun updatePracticeProgress() {
        val doneHere = practicePack.items.count { donePracticeIds.contains(it.id) }
        val doneAll = Content.practicePacks.sumOf { pack -> pack.items.count { donePracticeIds.contains(it.id) } }
        b.tvPracticeProgress.text = "✍️ ${practicePack.label}: $doneHere/${practicePack.items.size} · " +
                "Total practicado: $doneAll/${Content.totalPractice}"
        b.tvDeckInfo.text = "💡 Toca pista o solución · marca Practicado ✅ para guardar tu avance"
    }

    private fun bindPractice() {
        practiceAdapter.submit(practicePack.items, donePracticeIds)
        updatePracticeProgress()
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
        b.tvDeckInfo.text = "📚 ${deck.cards.size} tarjetas en esta baraja · ${Content.decks.size} barajas disponibles"
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
        b.tvDeckInfo.text = "🎯 ${quiz.questions.size} preguntas · te explico cada respuesta"

        val buttons = listOf(b.btnOpt1, b.btnOpt2, b.btnOpt3, b.btnOpt4)
        buttons.forEachIndexed { i, btn ->
            btn.visibility = View.VISIBLE
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
