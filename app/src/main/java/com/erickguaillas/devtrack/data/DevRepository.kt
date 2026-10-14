package com.erickguaillas.devtrack.data

import android.content.Context
import androidx.lifecycle.LiveData

class DevRepository(context: Context) {

    private val db = AppDatabase.get(context)

    val tasks: LiveData<List<Task>> = db.taskDao().all()
    val doneCount: LiveData<Int> = db.taskDao().doneCount()
    val journal: LiveData<List<JournalEntry>> = db.journalDao().all()
    val goals: LiveData<List<Goal>> = db.goalDao().all()

    fun checksForDay(dayKey: String): LiveData<List<Check>> = db.checkDao().forDay(dayKey)

    fun todayKey(): String {
        val c = java.util.Calendar.getInstance()
        return "%04d-%02d-%02d".format(
            c.get(java.util.Calendar.YEAR),
            c.get(java.util.Calendar.MONTH) + 1,
            c.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    /** Marca/desmarca un elemento de checklist (entrenamiento, tips, práctica) */
    suspend fun toggleCheck(dayKey: String, itemId: String) {
        val existing = db.checkDao().find(dayKey, itemId)
        if (existing == null) db.checkDao().insert(Check(dayKey = dayKey, itemId = itemId, done = true))
        else db.checkDao().update(existing.copy(done = !existing.done))
    }

    suspend fun deleteJournal(id: Long) = db.journalDao().delete(id)

    suspend fun updateJournal(id: Long, text: String, category: String) {
        val all = db.journalDao().allNow()
        all.firstOrNull { it.id == id }?.let { db.journalDao().update(it.copy(text = text, category = category)) }
    }

    suspend fun updateTask(id: Long, title: String) {
        db.taskDao().allNow().firstOrNull { it.id == id }?.let { db.taskDao().update(it.copy(title = title)) }
    }

    suspend fun updateGoal(id: Long, title: String) {
        db.goalDao().allNow().firstOrNull { it.id == id }?.let { db.goalDao().update(it.copy(title = title)) }
    }

    // ─── Hábitos (ejercicios y cuidado) — CRUD completo ───
    fun habits(type: String): LiveData<List<Habit>> = db.habitDao().byType(type)

    suspend fun addHabit(text: String, type: String) = db.habitDao().insert(Habit(text = text, type = type))

    suspend fun updateHabit(id: Long, text: String) {
        val habit = db.habitDao().findById(id) ?: return
        db.habitDao().update(habit.copy(text = text))
    }

    suspend fun deleteHabit(id: Long) = db.habitDao().delete(id)

    suspend fun journalStats(): Triple<Int, Int, Int> {
        val all = db.journalDao().allNow()
        val todayKey = todayKey()
        val weekAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        val today = all.count {
            val c = java.util.Calendar.getInstance().apply { timeInMillis = it.timestamp }
            "%04d-%02d-%02d".format(c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH) + 1, c.get(java.util.Calendar.DAY_OF_MONTH)) == todayKey
        }
        val week = all.count { it.timestamp >= weekAgo }
        return Triple(today, week, all.size)
    }

    suspend fun weekSummaryText(): String {
        val all = db.journalDao().allNow().take(20)
        if (all.isEmpty()) return "Bitácora vacía"
        return all.joinToString("; ") { "${it.category}: ${it.text}" }
    }

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

    /** Nombres de los ejercicios/hábitos (para el asistente) */
    suspend fun habitsSummary(type: String): String {
        val list = db.habitDao().byTypeNow(type)
        return if (list.isEmpty()) "ninguno"
        else list.joinToString("; ") { it.text }
    }

    suspend fun addGoal(title: String) = db.goalDao().insert(Goal(title = title))

    suspend fun tasksSummary(): String {
        val pending = db.taskDao().allNow().filter { !it.done }
        return if (pending.isEmpty()) "Sin tareas pendientes"
        else "Tareas pendientes: " + pending.joinToString("; ") { it.title }
    }

    suspend fun goalsSummary(): String {
        val goals = db.goalDao().allNow()
        return if (goals.isEmpty()) "Sin metas registradas"
        else "Metas: " + goals.joinToString("; ") { "${it.title} (${it.progress}%)" }
    }

    suspend fun bumpGoal(goal: Goal, delta: Int) {
        val p = (goal.progress + delta).coerceIn(0, 100)
        db.goalDao().update(goal.copy(progress = p))
    }

    suspend fun deleteGoal(id: Long) = db.goalDao().delete(id)
}
