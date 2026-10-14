package com.erickguaillas.devtrack.ui.today

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.erickguaillas.devtrack.R
import com.erickguaillas.devtrack.data.DevRepository
import com.erickguaillas.devtrack.databinding.FragmentTodayBinding
import com.erickguaillas.devtrack.databinding.DialogFormBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodayFragment : Fragment(R.layout.fragment_today) {

    private var _b: FragmentTodayBinding? = null
    private val b get() = _b!!
    private lateinit var repo: DevRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentTodayBinding.bind(view)
        repo = DevRepository(requireContext())

        val fmt = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es"))
        b.tvDate.text = fmt.format(Date()).replaceFirstChar { it.uppercase() }

        val adapter = TaskAdapter(
            onToggle = { task -> lifecycleScope.launch { repo.toggleTask(task) } },
            onDelete = { task -> lifecycleScope.launch { repo.deleteTask(task.id) } }
        )
        b.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        b.rvTasks.adapter = adapter

        repo.tasks.observe(viewLifecycleOwner) { list ->
            adapter.submit(list)
            val done = list.count { it.done }
            b.tvProgress.text = if (list.isEmpty()) "Aún no tienes tareas. ¡Agrega la primera! ➕"
            else "Hoy: $done de ${list.size} completadas" + if (done == list.size) " — ¡Día perfecto! 🏆" else ""
            if (list.isNotEmpty()) {
                b.progressToday.max = list.size
                b.progressToday.progress = done
            }
        }

        b.fabAddTask.setOnClickListener { showAddTaskDialog() }
        b.btnOpenJournal.setOnClickListener {
            findNavController().navigate(R.id.nav_bitacora)
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = DialogFormBinding.inflate(layoutInflater)
        dialogView.tvDialogTitle.text = "Nueva tarea de hoy"
        dialogView.etInput.hint = "Ej: Postular a 5 empleos"
        AlertDialog.Builder(requireContext())
            .setView(dialogView.root)
            .setPositiveButton("Agregar") { _, _ ->
                val text = dialogView.etInput.text.toString().trim()
                if (text.isNotEmpty()) lifecycleScope.launch { repo.addTask(text) }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
