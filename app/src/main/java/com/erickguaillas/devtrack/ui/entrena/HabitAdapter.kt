package com.erickguaillas.devtrack.ui.entrena

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erickguaillas.devtrack.data.Habit
import com.erickguaillas.devtrack.databinding.ItemHabitBinding

/** Lista de hábitos con CRUD: check (toggle), editar (lápiz) y borrar (mantener presionado) */
class HabitAdapter(
    private val onToggle: (Habit) -> Unit,
    private val onEdit: (Habit) -> Unit,
    private val onDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.VH>() {

    private var items: List<Habit> = emptyList()
    private var done: Set<String> = emptySet()

    fun submit(items: List<Habit>, doneIds: Set<String>) {
        this.items = items
        this.done = doneIds
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemHabitBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val habit = items[position]
        val isDone = done.contains("habit_${habit.id}")

        holder.b.cbHabit.text = habit.text
        holder.b.cbHabit.setOnCheckedChangeListener(null)
        holder.b.cbHabit.isChecked = isDone
        holder.b.cbHabit.paintFlags = if (isDone)
            holder.b.cbHabit.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            holder.b.cbHabit.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        holder.b.cbHabit.setOnCheckedChangeListener { _, _ -> onToggle(habit) }
        holder.b.btnEdit.setOnClickListener { onEdit(habit) }
        holder.b.root.setOnLongClickListener { onDelete(habit); true }
    }

    override fun getItemCount() = items.size
}
