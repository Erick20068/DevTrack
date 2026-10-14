package com.erickguaillas.devtrack.ui.entrena

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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

        // Rutina del día
        val routine = Wellness.todayRoutine()
        b.tvRoutineTitle.text = "${routine.emoji} ${routine.dayName} · ${routine.focus}"
        b.tvExercises.text = routine.exercises.joinToString("\n") { "•  $it" }
        b.tvTips.text = Wellness.bodyCareTips.joinToString("\n\n")

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
