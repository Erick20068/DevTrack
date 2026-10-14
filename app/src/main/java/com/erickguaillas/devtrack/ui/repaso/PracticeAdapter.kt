package com.erickguaillas.devtrack.ui.repaso

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erickguaillas.devtrack.data.Content
import com.erickguaillas.devtrack.databinding.ItemPracticeBinding

class PracticeAdapter(
    private val onToggleDone: (id: String) -> Unit
) : RecyclerView.Adapter<PracticeAdapter.VH>() {

    private var items: List<Content.PracticeItem> = emptyList()
    private var doneIds: Set<String> = emptySet()
    private val hintOpen = mutableSetOf<String>()
    private val solutionOpen = mutableSetOf<String>()

    fun submit(items: List<Content.PracticeItem>, doneIds: Set<String>) {
        this.items = items
        this.doneIds = doneIds
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemPracticeBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemPracticeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.b.tvPrompt.text = item.prompt
        holder.b.tvHint.text = "💡 ${item.hint}"
        holder.b.tvSolution.text = item.solution

        holder.b.tvHint.visibility = if (hintOpen.contains(item.id)) View.VISIBLE else View.GONE
        holder.b.tvSolution.visibility = if (solutionOpen.contains(item.id)) View.VISIBLE else View.GONE

        holder.b.btnHint.setOnClickListener {
            if (hintOpen.contains(item.id)) hintOpen.remove(item.id) else hintOpen.add(item.id)
            notifyItemChanged(position)
        }
        holder.b.btnSolution.setOnClickListener {
            if (solutionOpen.contains(item.id)) solutionOpen.remove(item.id) else solutionOpen.add(item.id)
            notifyItemChanged(position)
        }

        holder.b.cbPracticed.setOnCheckedChangeListener(null)
        holder.b.cbPracticed.isChecked = doneIds.contains(item.id)
        holder.b.cbPracticed.setOnCheckedChangeListener { _, _ -> onToggleDone(item.id) }
    }

    override fun getItemCount() = items.size
}
