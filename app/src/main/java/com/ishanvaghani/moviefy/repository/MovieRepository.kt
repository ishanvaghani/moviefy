package com.ishanvaghani.moviefy.repository

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.ishanvaghani.moviefy.model.*
import com.ishanvaghani.moviefy.netwrok.ApiBuilder
import com.ishanvaghani.moviefy.netwrok.MovieApi
import com.ishanvaghani.moviefy.pagination.*
import com.ishanvaghani.moviefy.utils.showErrorToast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieApi: MovieApi,
    private val apiBuilder: ApiBuilder,
    @ApplicationContext private val context: Context
) {

    var movieDetails: MovieDetails? = null
    var nowPlayingMovies: List<Movie>? = null
    var movieVideos: List<Video>? = null
    var movieGenres: ArrayList<Genre>? = null
    var movieCasts: ArrayList<Cast>? = null

    suspend fun getMovieDetails(movieId: Int) = withContext(Dispatchers.IO) {
        try {
            val response = apiBuilder.getMovieDetails(movieId)
            if (response.isSuccessful) {
                movieDetails = response.body()!!
            } else {
                showErrorToast(context)
            }
        } catch (e: IOException) {
            showErrorToast(context)
        } catch (e: HttpException) {
            withContext(Dispatchers.Main) {
                showErrorToast(context)
            }
        }
    }

    suspend fun getNowPlayingMovies() = withContext(Dispatchers.IO) {
        try {
            val response = apiBuilder.getNowPlayingMovies()
            if (response.isSuccessful) {
                nowPlayingMovies = response.body()!!.results
            } else {
                showErrorToast(context)
            }
        } catch (e: IOException) {
            showErrorToast(context)
        } catch (e: HttpException) {
            showErrorToast(context)
        }
    }

    suspend fun getMovieVideos(movieId: Int) = withContext(Dispatchers.IO) {
        try {
            val response = apiBuilder.getMovieVideos(movieId)
            if (response.isSuccessful) {
                movieVideos = response.body()!!.results
            } else {
                showErrorToast(context)
            }
        } catch (e: IOException) {
            showErrorToast(context)
        } catch (e: HttpException) {
            showErrorToast(context)
        }
    }

    suspend fun getMovieGenres() = withContext(Dispatchers.IO) {
        try {
            val response = apiBuilder.getMovieGenres()
            if (response.isSuccessful) {
                movieGenres = response.body()!!.genres
            } else {
                Log.d("movieRepo", response.errorBody()!!.string())
            }
        } catch (e: IOException) {
            showErrorToast(context)
        } catch (e: HttpException) {
            showErrorToast(context)
        }
    }

    suspend fun getMovieCredits(movieId: Int) = withContext(Dispatchers.IO) {
        try {
            val response = apiBuilder.getMovieCredits(movieId)
            if (response.isSuccessful) {
                movieCasts = response.body()!!.cast
            } else {
                showErrorToast(context)
            }
        } catch (e: IOException) {
            showErrorToast(context)
        } catch (e: HttpException) {
            showErrorToast(context)
        }
    }

    fun getPopularMovies() =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PopularMoviePagingSource(movieApi) }
        ).liveData

    fun getTopRatedMovies() = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { TopRatedMoviePagingSource(movieApi) }
    ).liveData

    fun getUpcomingMovies() = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { UpcomingMoviePagingSource(movieApi) }
    ).liveData

    fun getSimilarMovies(movieId: Int) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SimilarMoviePagingSource(movieId, movieApi) }
    ).liveData

    fun getGenreMovies(genreId: Int) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { MovieGenrePagingSource(genreId, movieApi) }
    ).liveData

}