package com.ishanvaghani.moviefy.ui.tv_shows

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ishanvaghani.moviefy.model.Movie
import com.ishanvaghani.moviefy.repository.TvShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvShowViewModel @Inject constructor(private val tvShowRepository: TvShowRepository) :
    ViewModel() {

    private var tvAiringToday: MutableLiveData<List<Movie>> = MutableLiveData()

    fun readyTvAiringToday() {
        viewModelScope.launch {
            tvShowRepository.getTvAiringToday()
            tvAiringToday.value = tvShowRepository.tvAiringToday
        }
    }

    val popularTvShow = tvShowRepository.getPopularTvShow().cachedIn(viewModelScope)
    val topRatedTvShow = tvShowRepository.getTopRateTvShow().cachedIn(viewModelScope)
    val onTheAirTvShow = tvShowRepository.getOnTheAirTvShow().cachedIn(viewModelScope)

    fun getTvAiringToday(): LiveData<List<Movie>> = tvAiringToday
}