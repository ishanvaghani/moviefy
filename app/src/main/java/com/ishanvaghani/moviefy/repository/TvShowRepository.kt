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

class TvShowRepository @Inject constructor(
    private val movieApi: MovieApi,
    private val apiBuilder: ApiBuilder,
    @ApplicationContext private val context: Context
) {

    var tvAiringToday: List<Movie>? = null
    var tvShowVideos: List<Video>? = null
    var tvShowDetails: TvShowDetails? = null
    var tvShowGenres: ArrayList<Genre>? = null
    var tvShowCasts: ArrayList<Cast>? = null

    suspend fun getTvAiringToday() = withContext(Dispatchers.IO) {
        try {
            val response = apiBuilder.getTvAiringToday()
            if (response.isSuccessful) {
                tvAiringToday = response.body()!!.results
            } else {
                showErrorToast(context)
            }
        } catch (e: IOException) {
            showErrorToast(context)
        } catch (e: HttpException) {
            showErrorToast(context)
        }
    }

    suspend fun getTvShowVideos(tvShowId: Int) = withContext(Dispatchers.IO) {
        try {
            val response = apiBuilder.getTvShowVideos(tvShowId)
            if (response.isSuccessful) {
                tvShowVideos = response.body()!!.results
            } else {
                showErrorToast(context)
            }
        } catch (e: IOException) {
            showErrorToast(context)
        } catch (e: HttpException) {
            showErrorToast(context)
        }
    }

    suspend fun getTvShowDetails(tvShowId: Int) = withContext(Dispatchers.IO) {
        try {
            val response = apiBuilder.getTvShowDetails(tvShowId)
            if (response.isSuccessful) {
                tvShowDetails = response.body()!!
            } else {
                showErrorToast(context)
            }
        } catch (e: IOException) {
            showErrorToast(context)
        } catch (e: HttpException) {
            showErrorToast(context)
        }
    }

    suspend fun getTVShowGenres() = withContext(Dispatchers.IO) {
        try {
            val response = apiBuilder.getTVShowGenres()
            if (response.isSuccessful) {
                tvShowGenres = response.body()!!.genres
            } else {
                showErrorToast(context)
            }
        } catch (e: IOException) {
            showErrorToast(context)
        } catch (e: HttpException) {
            showErrorToast(context)
        }
    }

    suspend fun getTVShowCredits(tvShowId: Int) = withContext(Dispatchers.IO) {
        try {
            val response = apiBuilder.getMovieCredits(tvShowId)
            if (response.isSuccessful) {
                tvShowCasts = response.body()!!.cast
            } else {
                Log.d("tvShowRepo", response.errorBody()!!.string())
            }
        } catch (e: IOException) {
            showErrorToast(context)
        } catch (e: HttpException) {
            showErrorToast(context)
        }
    }

    fun getPopularTvShow() = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PopularTvShowPagingSource(movieApi) }
    ).liveData

    fun getTopRateTvShow() = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { TopRatedTvShowPagingSource(movieApi) }
    ).liveData

    fun getOnTheAirTvShow() = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { OnTheAirTvShowPagingSource(movieApi) }
    ).liveData

    fun getSimilarTvShow(tvShowId: Int) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SimilarTvShowPagingSource(tvShowId, movieApi) }
    ).liveData

    fun getSearch(query: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { SearchPagingSource(query, movieApi) }
    ).liveData

    fun getGenreTVShows(genreId: Int) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { TVShowGenrePagingSource(genreId, movieApi) }
    ).liveData
}