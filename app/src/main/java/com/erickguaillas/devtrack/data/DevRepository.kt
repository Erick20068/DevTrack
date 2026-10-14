package com.erickguaillas.devtrack.data

import android.content.Context
import androidx.lifecycle.LiveData

class DevRepository(context: Context) {

    private val db = AppDatabase.get(context)

    val tasks: LiveData<List<Task>> = db.taskDao().all()
    val doneCount: LiveData<Int> = db.taskDao().doneCount()
    val journal: LiveData<List<JournalEntry>> = db.journalDao().all()
    val goals: LiveData<List<Goal>> = db.goalDao().all()

    suspend fun addTask(title: String) = db.taskDao().insert(Task(title = title))

    suspend fun toggleTask(task: Task) = db.taskDao().update(
        task.copy(done = !task.done, doneAt = if (task.done) null else System.currentTimeMillis())
    )

    suspend fun deleteTask(id: Long) = db.taskDao().delete(id)

    suspend fun pendingTaskTitles(): String {
        val pending = db.taskDao().allNow().filter { !it.done }
        return if (pending.isEmpty()) "No tienes tareas pendientes. Máquina."
        else "Tienes ${pending.size} tareas pendientes: " + pending.joinToString(". ") { it.title }
    }

    suspend fun addJournal(text: String, category: String) =
        db.journalDao().insert(JournalEntry(text = text, category = category))

    suspend fun addGoal(title: String) = db.goalDao().insert(Goal(title = title))

    suspend fun bumpGoal(goal: Goal, delta: Int) {
        val p = (goal.progress + delta).coerceIn(0, 100)
        db.goalDao().update(goal.copy(progress = p))
    }

    suspend fun deleteGoal(id: Long) = db.goalDao().delete(id)
}
