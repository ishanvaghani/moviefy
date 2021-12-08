package com.ishanvaghani.moviefy.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ishanvaghani.moviefy.model.Genre
import com.ishanvaghani.moviefy.repository.MovieRepository
import com.ishanvaghani.moviefy.repository.SearchRepository
import com.ishanvaghani.moviefy.repository.TvShowRepository
import com.ishanvaghani.moviefy.room.history.History
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository
) :
    ViewModel() {

    private var movieGenres: MutableLiveData<ArrayList<Genre>> = MutableLiveData()
    private var tvShowGenres: MutableLiveData<ArrayList<Genre>> = MutableLiveData()

    init {
        getAll()
    }

    val histories = MutableLiveData<List<History>>()

    fun insert(history: History) = viewModelScope.launch {
        searchRepository.insert(history)
    }

    fun delete(history: History) = viewModelScope.launch {
        searchRepository.delete(history)
    }

    private fun getAll() = viewModelScope.launch {
        searchRepository.getAll()
            .catch {
                it.message?.let { it1 ->
                    Log.d("main", it1)
                }
            }
            .collect {
                histories.value = it
            }
    }

    fun readyMovieGenre() {
        movieGenres = MutableLiveData()
        viewModelScope.launch {
            movieRepository.getMovieGenres()
            movieGenres.value = movieRepository.movieGenres
        }
    }

    fun readyTVShowGenre() {
        tvShowGenres = MutableLiveData()
        viewModelScope.launch {
            tvShowRepository.getTVShowGenres()
            tvShowGenres.value = tvShowRepository.tvShowGenres
        }
    }

    fun getMovieGenres(): LiveData<ArrayList<Genre>> = movieGenres
    fun getTVShowGenres(): LiveData<ArrayList<Genre>> = tvShowGenres

    fun search(query: String) = tvShowRepository.getSearch(query).cachedIn(viewModelScope)
}