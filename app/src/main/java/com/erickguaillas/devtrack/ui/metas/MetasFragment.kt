package com.erickguaillas.devtrack.ui.metas

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.erickguaillas.devtrack.R
import com.erickguaillas.devtrack.data.DevRepository
import com.erickguaillas.devtrack.data.Finance
import com.erickguaillas.devtrack.databinding.DialogFormBinding
import com.erickguaillas.devtrack.databinding.FragmentMetasBinding
import kotlinx.coroutines.launch

class MetasFragment : Fragment(R.layout.fragment_metas) {

    private var _b: FragmentMetasBinding? = null
    private val b get() = _b!!
    private lateinit var repo: DevRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentMetasBinding.bind(view)
        repo = DevRepository(requireContext())

        val adapter = GoalAdapter(
            onBump = { goal, delta -> lifecycleScope.launch { repo.bumpGoal(goal, delta) } },
            onDelete = { goal -> lifecycleScope.launch { repo.deleteGoal(goal.id) } }
        )
        b.rvGoals.layoutManager = LinearLayoutManager(requireContext())
        b.rvGoals.adapter = adapter

        repo.goals.observe(viewLifecycleOwner) { list ->
            adapter.submit(list)
            b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // 💰 Consejo financiero del día
        val lesson = Finance.lessonOfTheDay()
        b.tvFinanceTitle.text = "💰 Hoy: ${lesson.title}"
        b.tvFinanceBody.text = lesson.body

        b.fabAddGoal.setOnClickListener { showAddDialog() }
    }

    private fun showAddDialog() {
        val dialogView = DialogFormBinding.inflate(layoutInflater)
        dialogView.tvDialogTitle.text = "Nueva meta 🎯"
        dialogView.etInput.hint = "Ej: Primer empleo como dev"
        AlertDialog.Builder(requireContext())
            .setView(dialogView.root)
            .setPositiveButton("Agregar") { _, _ ->
                val text = dialogView.etInput.text.toString().trim()
                if (text.isNotEmpty()) lifecycleScope.launch { repo.addGoal(text) }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
