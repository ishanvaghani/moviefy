package com.ishanvaghani.moviefy.ui.movie_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ishanvaghani.moviefy.model.Cast
import com.ishanvaghani.moviefy.model.MovieDetails
import com.ishanvaghani.moviefy.model.Video
import com.ishanvaghani.moviefy.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(private val movieRepository: MovieRepository) :
    ViewModel() {

    private var movieDetails: MutableLiveData<MovieDetails> = MutableLiveData()
    private var movieVideos: MutableLiveData<List<Video>> = MutableLiveData()
    private var movieCasts: MutableLiveData<List<Cast>> = MutableLiveData()

    fun readyMovieDetails(movieId: Int) {
        movieDetails = MutableLiveData()
        viewModelScope.launch {
            movieRepository.getMovieDetails(movieId)
            movieDetails.value = movieRepository.movieDetails
        }
    }

    fun readyMovieVideos(movieId: Int) {
        viewModelScope.launch {
            movieRepository.getMovieVideos(movieId)
            movieVideos.value = movieRepository.movieVideos
        }
    }

    fun readyMovieCasts(movieId: Int) {
        viewModelScope.launch {
            movieRepository.getMovieCredits(movieId)
            movieCasts.value = movieRepository.movieCasts
        }
    }

    fun similarMovies(movieId: Int) =
        movieRepository.getSimilarMovies(movieId).cachedIn(viewModelScope)

    fun getMovieDetails(): LiveData<MovieDetails> = movieDetails
    fun getMovieVideos(): LiveData<List<Video>> = movieVideos
    fun getMovieCasts(): LiveData<List<Cast>> = movieCasts
}