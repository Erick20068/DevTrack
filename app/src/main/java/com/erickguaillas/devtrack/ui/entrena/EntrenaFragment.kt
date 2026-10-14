package com.erickguaillas.devtrack.ui.entrena

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.erickguaillas.devtrack.BuildConfig
import com.erickguaillas.devtrack.R
import com.erickguaillas.devtrack.data.DevRepository
import com.erickguaillas.devtrack.data.Wellness
import com.erickguaillas.devtrack.data.network.VisionAnalyzer
import com.erickguaillas.devtrack.databinding.FragmentEntrenaBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class EntrenaFragment : Fragment(R.layout.fragment_entrena) {

    private var _b: FragmentEntrenaBinding? = null
    private val b get() = _b!!
    private lateinit var repo: DevRepository
    private var foodBitmap: Bitmap? = null

    private val routineItems by lazy { Wellness.routineItems() }
    private val tipItems by lazy { Wellness.tipItems() }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bmp?.let { setFood(it) }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bmp = requireContext().contentResolver.openInputStream(it)?.use { s -> BitmapFactory.decodeStream(s) }
            bmp?.let(::setFood)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentEntrenaBinding.bind(view)
        repo = DevRepository(requireContext())

        val routine = Wellness.todayRoutine()
        b.tvRoutineTitle.text = "${routine.emoji} ${routine.dayName} · ${routine.focus}"

        // Checklist de ejercicios
        val exAdapter = CheckAdapter { id -> lifecycleScope.launch { repo.toggleCheck(repo.todayKey(), id) } }
        b.rvExercises.layoutManager = LinearLayoutManager(requireContext())
        b.rvExercises.adapter = exAdapter

        // Checklist de cuidado del cuerpo
        val tipAdapter = CheckAdapter { id -> lifecycleScope.launch { repo.toggleCheck(repo.todayKey(), id) } }
        b.rvTips.layoutManager = LinearLayoutManager(requireContext())
        b.rvTips.adapter = tipAdapter

        repo.checksForDay(repo.todayKey()).observe(viewLifecycleOwner) { checks ->
            val done = checks.filter { it.done }.map { it.itemId }.toSet()

            exAdapter.submit(routineItems, done)
            val exDone = routineItems.count { done.contains(it.first) }
            b.tvRoutineProgress.text = "$exDone de ${routineItems.size} ejercicios completados hoy"
            b.progressRoutine.max = routineItems.size
            b.progressRoutine.progress = exDone

            tipAdapter.submit(tipItems, done)
            val tipDone = tipItems.count { done.contains(it.first) }
            b.tvTipsProgress.text = "$tipDone de ${tipItems.size} hábitos cumplidos hoy"
        }

        b.btnDoneWorkout.setOnClickListener {
            lifecycleScope.launch {
                repo.addJournal("Entrenamiento completado: ${routine.focus} ${routine.emoji}", "CUERPO")
            }
            Snackbar.make(view, "💪 Registrado en tu bitácora. ¡Máquina!", Snackbar.LENGTH_SHORT).show()
        }

        // Calorías por foto
        b.btnTakePhoto.setOnClickListener { takePhoto.launch(null) }
        b.btnPickImage.setOnClickListener { pickImage.launch("image/*") }
        b.btnAnalyze.setOnClickListener { analyzeFood() }
    }

    private fun setFood(bmp: Bitmap) {
        foodBitmap = bmp
        b.ivFood.setImageBitmap(bmp)
        b.ivFood.visibility = View.VISIBLE
        b.btnAnalyze.visibility = View.VISIBLE
        b.tvCalorieResult.text = "📸 Foto lista. Pulsa \"Analizar con IA\"."
    }

    private fun analyzeFood() {
        val bmp = foodBitmap ?: return
        b.tvCalorieResult.text = "🤖 Analizando tu comida..."
        b.btnAnalyze.isEnabled = false
        lifecycleScope.launch {
            val result = VisionAnalyzer.analyze(bmp, BuildConfig.GROQ_API_KEY)
            b.tvCalorieResult.text = result.text
            b.btnAnalyze.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
