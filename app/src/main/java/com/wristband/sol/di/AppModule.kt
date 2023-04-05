package com.wristband.sol.di

import android.content.Context
import androidx.room.Room
import com.wristband.sol.data.SessionManager
import com.wristband.sol.data.db.AppDatabase
import com.wristband.sol.data.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * Module to tell Hilt how to provide instances of types that cannot be constructor-injected.
 *
 * As these types are scoped to the application lifecycle using @Singleton, they're installed
 * in Hilt's ApplicationComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Qualifier
    @Retention(RUNTIME)
    annotation class RemoteTasksDataSource

    @Qualifier
    @Retention(RUNTIME)
    annotation class LocalTasksDataSource

//    @Singleton
//    @RemoteTasksDataSource
//    @Provides
//    fun provideTasksRemoteDataSource(): TasksDataSource {
//        return TasksRemoteDataSource
//    }

    @Singleton
    @Provides
    fun provideUserRepository(database: AppDatabase): UserRepository {
        return UserRepository(database.userDao())
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()
    }

    @Singleton
    @Provides
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager.getInstance(context)
    }
}