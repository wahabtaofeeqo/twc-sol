package com.wristband.sol.di

import android.content.Context
import androidx.room.Room
import com.wristband.sol.data.Constants
import com.wristband.sol.data.SessionManager
import com.wristband.sol.data.api.EndpointsInterface
import com.wristband.sol.data.db.AppDatabase
import com.wristband.sol.data.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


/**
 * Module to tell Hilt how to provide instances of types that cannot be constructor-injected.
 *
 * As these types are scoped to the application lifecycle using @Singleton, they're installed
 * in Hilt's ApplicationComponent.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserRepository(database: AppDatabase): UserRepository {
        return UserRepository(database.userDao())
    }

    @Singleton
    @Provides
    fun provideLoginRepository(database: AppDatabase): LoginRepository {
        return LoginRepository(database.userDao())
    }

    @Singleton
    @Provides
    fun provideAttendanceRepository(database: AppDatabase): AttendanceRepository {
        return AttendanceRepository(database.attendanceDao())
    }

    @Singleton
    @Provides
    fun provideMemberRepository(retrofit: Retrofit, database: AppDatabase): MemberRepository {
        return MemberRepository(retrofit.create(EndpointsInterface::class.java), database.memberDao())
    }

    @Singleton
    @Provides
    fun provideTicketRepository(retrofit: Retrofit, database: AppDatabase): TicketRepository {
        return TicketRepository(retrofit.create(EndpointsInterface::class.java), database.ticketDao())
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        return Retrofit.Builder()
            .client(client).baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Singleton
    @Provides
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager.getInstance(context.applicationContext)
    }
}