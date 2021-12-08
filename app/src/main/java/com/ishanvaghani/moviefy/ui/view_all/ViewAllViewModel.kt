package com.ishanvaghani.moviefy.ui.view_all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ishanvaghani.moviefy.repository.MovieRepository
import com.ishanvaghani.moviefy.repository.TvShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewAllViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository
) : ViewModel() {

    val popularMovies = movieRepository.getPopularMovies().cachedIn(viewModelScope)
    val topRatedMovies = movieRepository.getTopRatedMovies().cachedIn(viewModelScope)
    val upcomingMovies = movieRepository.getUpcomingMovies().cachedIn(viewModelScope)
    fun getGenreMovies(genreId: Int) = movieRepository.getGenreMovies(genreId).cachedIn(viewModelScope)

    val popularTvShow = tvShowRepository.getPopularTvShow().cachedIn(viewModelScope)
    val topRatedTvShow = tvShowRepository.getTopRateTvShow().cachedIn(viewModelScope)
    val onTheAirTvShow = tvShowRepository.getOnTheAirTvShow().cachedIn(viewModelScope)
    fun getGenreTVShows(genreId: Int) = tvShowRepository.getGenreTVShows(genreId).cachedIn(viewModelScope)
}