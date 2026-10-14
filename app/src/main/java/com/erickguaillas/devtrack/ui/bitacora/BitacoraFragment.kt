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
import com.erickguaillas.devtrack.databinding.DialogEntryBinding
import com.erickguaillas.devtrack.databinding.FragmentBitacoraBinding
import kotlinx.coroutines.launch

class BitacoraFragment : Fragment(R.layout.fragment_bitacora) {

    private var _b: FragmentBitacoraBinding? = null
    private val b get() = _b!!
    private lateinit var repo: DevRepository

    private val categories = listOf("TRABAJO", "INGLES", "CODIGO", "APPS", "CUERPO")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentBitacoraBinding.bind(view)
        repo = DevRepository(requireContext())

        val adapter = JournalAdapter()
        b.rvJournal.layoutManager = LinearLayoutManager(requireContext())
        b.rvJournal.adapter = adapter

        repo.journal.observe(viewLifecycleOwner) { list ->
            adapter.submit(list)
            b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        b.fabAddEntry.setOnClickListener { showAddDialog() }
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
