package com.erickguaillas.devtrack.ui.entrena

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erickguaillas.devtrack.databinding.ItemTaskBinding

/** Checklist reutilizable: ejercicios del día, tips de cuidado y práctica */
class CheckAdapter(
    private val onToggle: (id: String) -> Unit
) : RecyclerView.Adapter<CheckAdapter.VH>() {

    private var items: List<Pair<String, String>> = emptyList()
    private var done: Set<String> = emptySet()

    fun submit(items: List<Pair<String, String>>, doneIds: Set<String>) {
        this.items = items
        this.done = doneIds
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemTaskBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val (id, text) = items[position]
        val isDone = done.contains(id)

        holder.b.cbTask.text = text
        holder.b.cbTask.setOnCheckedChangeListener(null)
        holder.b.cbTask.isChecked = isDone
        holder.b.cbTask.paintFlags = if (isDone)
            holder.b.cbTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            holder.b.cbTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        holder.b.cbTask.setOnCheckedChangeListener { _, _ -> onToggle(id) }
    }

    override fun getItemCount() = items.size
}
