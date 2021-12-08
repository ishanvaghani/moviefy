package com.ishanvaghani.moviefy.netwrok

import com.ishanvaghani.moviefy.model.*
import retrofit2.Response
import javax.inject.Inject

class ApiBuilder @Inject constructor(private val movieApi: MovieApi) {

    //Movies
    suspend fun getMovieDetails(movieId: Int): Response<MovieDetails> =
        movieApi.getMovieDetails(movieId)

    suspend fun getNowPlayingMovies(): Response<MovieResponse> = movieApi.getNowPlayingMovies()

    suspend fun getMovieVideos(movieId: Int): Response<VideoResponse> =
        movieApi.getMovieVideos(movieId)

    suspend fun getMovieGenres(): Response<GenreResponse> = movieApi.getMovieGenres()

    suspend fun getMovieCredits(movieId: Int): Response<CreditResponse> =
        movieApi.getMovieCredits(movieId)

    //Tv shows
    suspend fun getTvAiringToday(): Response<MovieResponse> = movieApi.getTvAiringToday()

    suspend fun getTvShowVideos(tvShowId: Int): Response<VideoResponse> =
        movieApi.getTvShowVideos(tvShowId)

    suspend fun getTvShowDetails(tvShowId: Int): Response<TvShowDetails> =
        movieApi.getTvShowDetails(tvShowId)

    suspend fun getTVShowGenres(): Response<GenreResponse> = movieApi.getTVShowGenres()

    suspend fun getTVShowCredits(tvShowId: Int): Response<CreditResponse> =
        movieApi.getTVShowCredits(tvShowId)
}