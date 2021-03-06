package com.ishanvaghani.moviefy.ui.tv_shows

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ishanvaghani.moviefy.ui.common.MovieAdapter
import com.ishanvaghani.moviefy.ui.common.SliderAdapter
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.FragmentTvShowBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvShowFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentTvShowBinding? = null
    private val binding get() = _binding!!

    private lateinit var slideAdapter: SliderAdapter
    private lateinit var tvPopularAdapter: MovieAdapter
    private lateinit var tvTopRatedAdapter: MovieAdapter
    private lateinit var tvOnTheAirAdapter: MovieAdapter

    private val tvShowViewModel: TvShowViewModel by viewModels()

    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvShowBinding.inflate(inflater, container, false)
        retainInstance = true

        toolbar = binding.toolbar
        toolbar.title = getString(R.string.tv_show)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        slideAdapter = SliderAdapter(this, ArrayList(), false)
        tvPopularAdapter = MovieAdapter(this, false)
        tvTopRatedAdapter = MovieAdapter(this, false)
        tvOnTheAirAdapter = MovieAdapter(this, false)

        bindUI()
        initViewModel()

        return binding.root
    }

    private fun initViewModel() {
        binding.swipRefreshLayout.isRefreshing = true
        tvShowViewModel.apply {
            readyTvAiringToday()
            getTvAiringToday().observe(viewLifecycleOwner, {
                if (it != null) {
                    slideAdapter.setData(it.subList(0, 5))
                }
            })

            tvShowViewModel.onTheAirTvShow.observe(viewLifecycleOwner) {
                tvOnTheAirAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }

            tvShowViewModel.popularTvShow.observe(viewLifecycleOwner) {
                tvPopularAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }

            tvShowViewModel.topRatedTvShow.observe(viewLifecycleOwner) {
                tvTopRatedAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        binding.swipRefreshLayout.isRefreshing = false
    }

    private fun bindUI() {
        binding.apply {

            swipRefreshLayout.apply {
                setColorSchemeColors(Color.parseColor("#FFD740"))
                setOnRefreshListener(this@TvShowFragment)
            }

            imageSlider.apply {
                setSliderAdapter(slideAdapter)
                startAutoCycle()
            }

            popularRecyclerView.apply {
                setHasFixedSize(true)
                adapter = tvPopularAdapter
            }

            topRatedRecyclerView.apply {
                setHasFixedSize(true)
                adapter = tvTopRatedAdapter
            }

            onTheAirRecyclerView.apply {
                setHasFixedSize(true)
                adapter = tvOnTheAirAdapter
            }

            retryButton.setOnClickListener {
                tvShowViewModel.readyTvAiringToday()
                tvShowViewModel.getTvAiringToday().observe(viewLifecycleOwner, {
                    if (it != null) {
                        slideAdapter.setData(it.subList(0, 5))
                    }
                })
                tvPopularAdapter.retry()
                tvTopRatedAdapter.retry()
                tvOnTheAirAdapter.retry()
            }

            popularButton.setOnClickListener {
                val action =
                    TvShowFragmentDirections.actionTvShowFragmentToViewAllFragment(getString(R.string.popular_tv_shows), -1, null)
                findNavController().navigate(action)
            }

            topRatedButton.setOnClickListener {
                val action =
                    TvShowFragmentDirections.actionTvShowFragmentToViewAllFragment(getString(R.string.top_rated_tv_shows), -1, null)
                findNavController().navigate(action)
            }

            onTheAirButton.setOnClickListener {
                val action =
                    TvShowFragmentDirections.actionTvShowFragmentToViewAllFragment(getString(R.string.on_the_air_tv_shows), -1, null)
                findNavController().navigate(action)
            }
        }

        tvPopularAdapter.addLoadStateListener { loadState ->
            binding.apply {
                swipRefreshLayout.isRefreshing = loadState.source.refresh is LoadState.Loading
                linearLayout.isVisible = loadState.source.refresh is LoadState.NotLoading
                errorLayout.isVisible = loadState.source.refresh is LoadState.Error

                //empty view
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && tvPopularAdapter.itemCount < 1) {
                    linearLayout.isVisible = false
                }
            }
        }
    }

    override fun onRefresh() {
        initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}