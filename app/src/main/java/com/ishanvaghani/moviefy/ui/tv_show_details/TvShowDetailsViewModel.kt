package com.ishanvaghani.moviefy.ui.tv_show_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ishanvaghani.moviefy.model.Cast
import com.ishanvaghani.moviefy.model.TvShowDetails
import com.ishanvaghani.moviefy.model.Video
import com.ishanvaghani.moviefy.repository.TvShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvShowDetailsViewModel @Inject constructor(private val tvShowRepository: TvShowRepository) :
    ViewModel() {

    private var tvShowVideos: MutableLiveData<List<Video>> = MutableLiveData()
    private var tvShowDetails: MutableLiveData<TvShowDetails> = MutableLiveData()
    private var tvShowCasts: MutableLiveData<List<Cast>> = MutableLiveData()

    fun readyTvShowVideos(tvShowId: Int) {
        viewModelScope.launch {
            tvShowRepository.getTvShowVideos(tvShowId)
            tvShowVideos.value = tvShowRepository.tvShowVideos
        }
    }

    fun readyTvShowDetails(tvShowId: Int) {
        tvShowDetails = MutableLiveData()
        viewModelScope.launch {
            tvShowRepository.getTvShowDetails(tvShowId)
            tvShowDetails.value = tvShowRepository.tvShowDetails
        }
    }

    fun readyTvShowCasts(tvShowId: Int) {
        viewModelScope.launch {
            tvShowRepository.getTVShowCredits(tvShowId)
            tvShowCasts.value = tvShowRepository.tvShowCasts
        }
    }

    fun getTvShowVideos(): LiveData<List<Video>> = tvShowVideos
    fun getTvShowDetails(): LiveData<TvShowDetails> = tvShowDetails
    fun getTvShowCasts(): LiveData<List<Cast>> = tvShowCasts

    fun similarTvShow(tvShowId: Int) =
        tvShowRepository.getSimilarTvShow(tvShowId).cachedIn(viewModelScope)
}