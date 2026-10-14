package com.erickguaillas.devtrack.ui.entrena

import android.app.AlertDialog
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
import com.erickguaillas.devtrack.data.Habit
import com.erickguaillas.devtrack.data.Wellness
import com.erickguaillas.devtrack.data.network.VisionAnalyzer
import com.erickguaillas.devtrack.databinding.DialogFormBinding
import com.erickguaillas.devtrack.databinding.FragmentEntrenaBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class EntrenaFragment : Fragment(R.layout.fragment_entrena) {

    private var _b: FragmentEntrenaBinding? = null
    private val b get() = _b!!
    private lateinit var repo: DevRepository
    private var foodBitmap: Bitmap? = null

    private var exerciseCount = 0
    private var careCount = 0

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
        b.tvRoutineTitle.text = "${routine.emoji} ${routine.dayName} · ${routine.focus} (ejercicios editables)"

        // ─── Ejercicios (CRUD) ───
        val exAdapter = HabitAdapter(
            onToggle = { habit -> lifecycleScope.launch { repo.toggleCheck(repo.todayKey(), "habit_${habit.id}") } },
            onEdit = { habit -> showHabitDialog(habit, "EXERCISE") },
            onDelete = { habit -> confirmDelete(habit) }
        )
        b.rvExercises.layoutManager = LinearLayoutManager(requireContext())
        b.rvExercises.adapter = exAdapter

        // ─── Hábitos de cuidado (CRUD) ───
        val careAdapter = HabitAdapter(
            onToggle = { habit -> lifecycleScope.launch { repo.toggleCheck(repo.todayKey(), "habit_${habit.id}") } },
            onEdit = { habit -> showHabitDialog(habit, "CARE") },
            onDelete = { habit -> confirmDelete(habit) }
        )
        b.rvTips.layoutManager = LinearLayoutManager(requireContext())
        b.rvTips.adapter = careAdapter

        repo.habits("EXERCISE").observe(viewLifecycleOwner) { list ->
            exerciseCount = list.size
            exAdapter.submit(list, currentDone())
            updateProgress()
        }
        repo.habits("CARE").observe(viewLifecycleOwner) { list ->
            careCount = list.size
            careAdapter.submit(list, currentDone())
            updateProgress()
        }

        repo.checksForDay(repo.todayKey()).observe(viewLifecycleOwner) { checks ->
            val done = checks.filter { it.done }.map { it.itemId }.toSet()
            lastDone = done
            repo.habits("EXERCISE").value?.let { exAdapter.submit(it, done) }
            repo.habits("CARE").value?.let { careAdapter.submit(it, done) }
            updateProgress()
        }

        b.btnAddExercise.setOnClickListener { showHabitDialog(null, "EXERCISE") }
        b.btnAddCare.setOnClickListener { showHabitDialog(null, "CARE") }

        b.btnDoneWorkout.setOnClickListener {
            lifecycleScope.launch {
                repo.addJournal("Entrenamiento completado: ${routine.focus} ${routine.emoji}", "CUERPO")
            }
            Snackbar.make(view, "💪 Registrado en tu bitácora. ¡Máquina!", Snackbar.LENGTH_SHORT).show()
        }

        // ─── Calorías por foto ───
        b.btnTakePhoto.setOnClickListener { takePhoto.launch(null) }
        b.btnPickImage.setOnClickListener { pickImage.launch("image/*") }
        b.btnAnalyze.setOnClickListener { analyzeFood() }
    }

    private var lastDone: Set<String> = emptySet()
    private fun currentDone(): Set<String> = lastDone

    private fun updateProgress() {
        val exDone = lastDone.count { it.startsWith("habit_") && isExercise(it) }
        b.tvRoutineProgress.text = "$exDone de $exerciseCount ejercicios completados hoy"
        b.progressRoutine.max = exerciseCount.coerceAtLeast(1)
        b.progressRoutine.progress = exDone

        val careDone = lastDone.count { it.startsWith("habit_") && !isExercise(it) }
        b.tvTipsProgress.text = "$careDone de $careCount hábitos cumplidos hoy"
    }

    /** Distingue ejercicio de cuidado consultando la lista actual en memoria */
    private fun isExercise(checkId: String): Boolean {
        val id = checkId.removePrefix("habit_").toLongOrNull() ?: return false
        return repo.habits("EXERCISE").value?.any { it.id == id } == true
    }

    // ─── Diálogo crear/editar ───
    private fun showHabitDialog(habit: Habit?, type: String) {
        val dialogView = DialogFormBinding.inflate(layoutInflater)
        val isExercise = type == "EXERCISE"
        dialogView.tvDialogTitle.text = when {
            habit != null -> "✏️ Editar " + if (isExercise) "ejercicio" else "hábito"
            isExercise -> "➕ Nuevo ejercicio"
            else -> "➕ Nuevo hábito de cuidado"
        }
        dialogView.etInput.hint = if (isExercise) "Ej: 4x15 flexiones" else "Ej: Dormir 8 horas"
        dialogView.etInput.setText(habit?.text ?: "")

        AlertDialog.Builder(requireContext())
            .setView(dialogView.root)
            .setPositiveButton(if (habit != null) "Guardar" else "Agregar") { _, _ ->
                val text = dialogView.etInput.text.toString().trim()
                if (text.isEmpty()) return@setPositiveButton
                lifecycleScope.launch {
                    if (habit != null) repo.updateHabit(habit.id, text)
                    else repo.addHabit(text, type)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmDelete(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle("¿Borrar «${habit.text}»?")
            .setMessage("Se quitará de tu lista de entrenamiento.")
            .setPositiveButton("Borrar") { _, _ ->
                lifecycleScope.launch { repo.deleteHabit(habit.id) }
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
