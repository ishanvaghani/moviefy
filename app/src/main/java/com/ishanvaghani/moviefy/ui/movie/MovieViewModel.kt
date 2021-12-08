package com.ishanvaghani.moviefy.ui.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ishanvaghani.moviefy.model.*
import com.ishanvaghani.moviefy.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val movieRepository: MovieRepository) :
    ViewModel() {

    private var nowPlayingMovies: MutableLiveData<List<Movie>> = MutableLiveData()

    fun readyNowPlayingMovies() {
        viewModelScope.launch {
            movieRepository.getNowPlayingMovies()
            nowPlayingMovies.value = movieRepository.nowPlayingMovies
        }
    }

    val popularMovies = movieRepository.getPopularMovies().cachedIn(viewModelScope)
    val topRatedMovies = movieRepository.getTopRatedMovies().cachedIn(viewModelScope)
    val upcomingMovies = movieRepository.getUpcomingMovies().cachedIn(viewModelScope)

    fun getGenreMovies(genreId: Int) = movieRepository.getGenreMovies(genreId).cachedIn(viewModelScope)

    fun getNowPlayingMovies(): LiveData<List<Movie>> = nowPlayingMovies
}