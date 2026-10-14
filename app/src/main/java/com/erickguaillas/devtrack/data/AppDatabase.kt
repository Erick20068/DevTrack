package com.erickguaillas.devtrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Task::class, JournalEntry::class, Goal::class, Check::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun journalDao(): JournalDao
    abstract fun goalDao(): GoalDao
    abstract fun checkDao(): CheckDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "devtrack.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedCallback())
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

    /** Datos de ejemplo la primera vez que se abre la app */
    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.taskDao().insert(Task(title = "Postular a 5 empleos"))
                    database.taskDao().insert(Task(title = "Estudiar inglés 30 min"))
                    database.taskDao().insert(Task(title = "1 propuesta en Workana"))
                    database.goalDao().insert(Goal(title = "Conseguir mi primer empleo como dev"))
                    database.goalDao().insert(Goal(title = "Inglés nivel B2"))
                    database.journalDao().insert(
                        JournalEntry(text = "Empecé a usar DevTrack 🚀", category = "CODIGO")
                    )
                }
            }
        }
    }
}
