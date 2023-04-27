package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val taskDatabase: Provider<TaskDatabase>,
        @ApplicationCoroutine private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            scope.launch {
                val taskDao = taskDatabase.get().taskDao()
                taskDao.insert(Task("Wash the dishes"))
                taskDao.insert(Task("Do the laundry"))
                taskDao.insert(Task("Buy groceries", important = true))
                taskDao.insert(Task("Prepare food", completed = true))
                taskDao.insert(Task("Call mom"))
                taskDao.insert(Task("Visit grandma", completed = true))
                taskDao.insert(Task("Repair my bike"))
                taskDao.insert(Task("Call Elon Musk"))
            }
        }
    }
}