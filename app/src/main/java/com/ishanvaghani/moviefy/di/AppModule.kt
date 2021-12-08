package com.ishanvaghani.moviefy.di

import android.app.Application
import androidx.room.Room
import com.ishanvaghani.moviefy.netwrok.MovieApi
import com.ishanvaghani.moviefy.room.history.HistoryDao
import com.ishanvaghani.moviefy.room.history.HistoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun providesBaseUrl(): String = "https://api.themoviedb.org/3/"

    @Provides
    @Singleton
    fun providesRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun providesMovieDetailsApi(retrofit: Retrofit): MovieApi =
        retrofit.create(MovieApi::class.java)

    @Provides
    @Singleton
    fun providesHistoryDatabase(application: Application) =
        Room.databaseBuilder(application, HistoryDatabase::class.java, "HistoryDatabase")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providesHistoryDao(db: HistoryDatabase): HistoryDao = db.getHistoryDao()
}