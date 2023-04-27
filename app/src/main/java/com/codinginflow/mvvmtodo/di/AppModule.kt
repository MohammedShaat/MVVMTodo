package com.codinginflow.mvvmtodo.di

import android.app.Application
import androidx.room.Room
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideTaskDatabase(app: Application, callback: TaskDatabase.Callback): TaskDatabase {
        return Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }

    @Provides
    fun provideTaskDao(db: TaskDatabase): TaskDao {
        return db.taskDao()
    }

    @ApplicationCoroutine
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationCoroutine