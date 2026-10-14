package com.erickguaillas.devtrack.ui.metas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erickguaillas.devtrack.data.Goal
import com.erickguaillas.devtrack.databinding.ItemGoalBinding

class GoalAdapter(
    private val onBump: (Goal, Int) -> Unit,
    private val onDelete: (Goal) -> Unit
) : RecyclerView.Adapter<GoalAdapter.VH>() {

    private var items: List<Goal> = emptyList()

    fun submit(list: List<Goal>) {
        items = list
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemGoalBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val goal = items[position]
        holder.b.tvGoalTitle.text = if (goal.progress >= 100) "🏆 ${goal.title}" else goal.title
        holder.b.progressGoal.progress = goal.progress
        holder.b.tvPercent.text = "${goal.progress}%"
        holder.b.btnPlus.setOnClickListener { onBump(goal, 10) }
        holder.b.btnMinus.setOnClickListener { onBump(goal, -10) }
        holder.b.root.setOnLongClickListener { onDelete(goal); true }
    }

    override fun getItemCount() = items.size
}
