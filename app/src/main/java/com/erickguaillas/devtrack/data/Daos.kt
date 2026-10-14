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
