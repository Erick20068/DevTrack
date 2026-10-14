package com.erickguaillas.devtrack.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY done ASC, createdAt DESC")
    fun all(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY done ASC, createdAt DESC")
    suspend fun allNow(): List<Task>

    @Query("SELECT COUNT(*) FROM tasks WHERE done = 1")
    fun doneCount(): LiveData<Int>

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun all(): LiveData<List<JournalEntry>>

    @Insert
    suspend fun insert(entry: JournalEntry): Long

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun delete(id: Long)

    @Update
    suspend fun update(entry: JournalEntry)

    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    suspend fun allNow(): List<JournalEntry>
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE type = :type ORDER BY createdAt ASC")
    fun byType(type: String): LiveData<List<Habit>>

    @Insert
    suspend fun insert(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COUNT(*) FROM habits")
    suspend fun count(): Int

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Habit?

    @Query("SELECT * FROM habits WHERE type = :type ORDER BY createdAt ASC")
    suspend fun byTypeNow(type: String): List<Habit>
}

@Dao
interface CheckDao {
    @Query("SELECT * FROM checks WHERE dayKey = :day")
    fun forDay(day: String): LiveData<List<Check>>

    @Query("SELECT * FROM checks WHERE dayKey = :day")
    suspend fun forDayNow(day: String): List<Check>

    @Query("SELECT * FROM checks WHERE dayKey = :day AND itemId = :itemId LIMIT 1")
    suspend fun find(day: String, itemId: String): Check?

    @Insert
    suspend fun insert(check: Check): Long

    @Update
    suspend fun update(check: Check)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun all(): LiveData<List<Goal>>

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    suspend fun allNow(): List<Goal>

    @Insert
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun delete(id: Long)
}
