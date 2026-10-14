package com.erickguaillas.devtrack.ui.bitacora

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.erickguaillas.devtrack.R
import com.erickguaillas.devtrack.data.DevRepository
import com.erickguaillas.devtrack.data.JournalEntry
import com.erickguaillas.devtrack.databinding.DialogEntryBinding
import com.erickguaillas.devtrack.databinding.FragmentBitacoraBinding
import kotlinx.coroutines.launch

class BitacoraFragment : Fragment(R.layout.fragment_bitacora) {

    private var _b: FragmentBitacoraBinding? = null
    private val b get() = _b!!
    private lateinit var repo: DevRepository
    private lateinit var adapter: JournalAdapter
    private var allEntries: List<JournalEntry> = emptyList()
    private var filter: String? = null // null = todos

    private val categories = listOf("TRABAJO", "INGLES", "CODIGO", "APPS", "CUERPO")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentBitacoraBinding.bind(view)
        repo = DevRepository(requireContext())

        adapter = JournalAdapter(onDelete = { entry -> confirmDelete(entry) })
        b.rvJournal.layoutManager = LinearLayoutManager(requireContext())
        b.rvJournal.adapter = adapter

        b.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            filter = when (checkedIds.firstOrNull()) {
                R.id.chipTrabajo -> "TRABAJO"
                R.id.chipIngles -> "INGLES"
                R.id.chipCodigo -> "CODIGO"
                R.id.chipApps -> "APPS"
                R.id.chipCuerpo -> "CUERPO"
                else -> null
            }
            applyFilter()
        }

        repo.journal.observe(viewLifecycleOwner) { list ->
            allEntries = list
            applyFilter()
        }

        lifecycleScope.launch {
            val (today, week, total) = repo.journalStats()
            b.tvStats.text = "📌 Hoy: $today   ·   Semana: $week   ·   Total: $total"
        }

        b.fabAddEntry.setOnClickListener { showAddDialog() }
    }

    private fun applyFilter() {
        val filtered = filter?.let { f -> allEntries.filter { it.category == f } } ?: allEntries
        adapter.submit(filtered)
        b.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun confirmDelete(entry: JournalEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("¿Borrar este registro?")
            .setMessage(entry.text)
            .setPositiveButton("Borrar") { _, _ ->
                lifecycleScope.launch { repo.deleteJournal(entry.id) }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAddDialog() {
        val dialogView = DialogEntryBinding.inflate(layoutInflater)
        dialogView.spCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("💼 Trabajo / postulaciones", "🗣️ Inglés", "💻 Código", "📱 Apps", "💪 Cuerpo")
        )
        AlertDialog.Builder(requireContext())
            .setTitle("Registrar actividad")
            .setView(dialogView.root)
            .setPositiveButton("Guardar") { _, _ ->
                val text = dialogView.etInput.text.toString().trim()
                if (text.isNotEmpty()) {
                    val cat = categories[dialogView.spCategory.selectedItemPosition]
                    lifecycleScope.launch { repo.addJournal(text, cat) }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
