package com.erickguaillas.devtrack.ui.bitacora

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erickguaillas.devtrack.data.JournalEntry
import com.erickguaillas.devtrack.databinding.ItemJournalBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalAdapter(
    private val onDelete: (JournalEntry) -> Unit = {}
) : RecyclerView.Adapter<JournalAdapter.VH>() {

    private var items: List<JournalEntry> = emptyList()

    fun submit(list: List<JournalEntry>) {
        items = list
        notifyDataSetChanged()
    }

    private fun emoji(category: String) = when (category) {
        "TRABAJO" -> "💼"
        "INGLES" -> "🗣️"
        "CODIGO" -> "💻"
        "APPS" -> "📱"
        "CUERPO" -> "💪"
        else -> "📝"
    }

    inner class VH(val b: ItemJournalBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemJournalBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = items[position]
        holder.b.tvEmoji.text = emoji(e.category)
        holder.b.tvText.text = e.text
        holder.b.tvTime.text = SimpleDateFormat("EEE d MMM · HH:mm", Locale("es")).format(Date(e.timestamp))
        holder.b.root.setOnLongClickListener { onDelete(e); true }
    }

    override fun getItemCount() = items.size
}
