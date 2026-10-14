package com.erickguaillas.devtrack.ui.today

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erickguaillas.devtrack.data.Task
import com.erickguaillas.devtrack.databinding.ItemTaskBinding

class TaskAdapter(
    private val onToggle: (Task) -> Unit,
    private val onEdit: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.VH>() {

    private var items: List<Task> = emptyList()

    fun submit(list: List<Task>) {
        items = list
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemTaskBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val task = items[position]
        holder.b.cbTask.text = task.title
        holder.b.cbTask.setOnCheckedChangeListener(null)
        holder.b.cbTask.isChecked = task.done
        holder.b.cbTask.paintFlags = if (task.done)
            holder.b.cbTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            holder.b.cbTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        holder.b.cbTask.setOnCheckedChangeListener { _, _ -> onToggle(task) }
        holder.b.btnEditTask.setOnClickListener { onEdit(task) }
        holder.b.root.setOnLongClickListener { onDelete(task); true }
    }

    override fun getItemCount() = items.size
}
