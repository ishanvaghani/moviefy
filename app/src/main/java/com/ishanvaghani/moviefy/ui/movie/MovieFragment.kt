package com.ishanvaghani.moviefy.ui.movie

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
import com.ishanvaghani.moviefy.databinding.FragmentMovieBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

    private lateinit var slideAdapter: SliderAdapter
    private lateinit var popularMovieAdapter: MovieAdapter
    private lateinit var topRatedMovieAdapter: MovieAdapter
    private lateinit var upcomingMoviesAdapter: MovieAdapter

    private val movieViewModel: MovieViewModel by viewModels()

    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieBinding.inflate(inflater, container, false)
        retainInstance = true

        toolbar = binding.toolbar
        toolbar.title = getString(R.string.movie)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        slideAdapter = SliderAdapter(this, ArrayList(), true)
        popularMovieAdapter = MovieAdapter(this, true)
        topRatedMovieAdapter = MovieAdapter(this, true)
        upcomingMoviesAdapter = MovieAdapter(this, true)

        bindUI()
        initViewModel()

        return binding.root
    }

    private fun initViewModel() {
        binding.swipRefreshLayout.isRefreshing = true
        movieViewModel.apply {
            readyNowPlayingMovies()
            getNowPlayingMovies().observe(viewLifecycleOwner, {
                if (it != null) {
                    slideAdapter.setData(it.subList(0, 5))
                }
            })

            movieViewModel.popularMovies.observe(viewLifecycleOwner) {
                popularMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }

            movieViewModel.topRatedMovies.observe(viewLifecycleOwner) {
                topRatedMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }

            movieViewModel.upcomingMovies.observe(viewLifecycleOwner) {
                upcomingMoviesAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        binding.swipRefreshLayout.isRefreshing = false
    }

    private fun bindUI() {

        binding.apply {

            swipRefreshLayout.apply {
                setColorSchemeColors(Color.parseColor("#FFD740"))
                setOnRefreshListener(this@MovieFragment)
            }

            imageSlider.apply {
                setSliderAdapter(slideAdapter)
                startAutoCycle()
            }

            popularRecyclerView.apply {
                setHasFixedSize(true)
                adapter = popularMovieAdapter
            }

            topRatedRecyclerView.apply {
                setHasFixedSize(true)
                adapter = topRatedMovieAdapter
            }

            upcomingRecyclerView.apply {
                setHasFixedSize(true)
                adapter = upcomingMoviesAdapter
            }

            retryButton.setOnClickListener {
                movieViewModel.readyNowPlayingMovies()
                movieViewModel.getNowPlayingMovies().observe(viewLifecycleOwner, {
                    if (it != null) {
                        slideAdapter.setData(it.subList(0, 5))
                    }
                })
                popularMovieAdapter.retry()
                topRatedMovieAdapter.retry()
                upcomingMoviesAdapter.retry()
            }

            popularButton.setOnClickListener {
                val action =
                    MovieFragmentDirections.actionHomeFragmentToViewAllFragment(
                        getString(R.string.popular_movies),
                        -1,
                        null
                    )
                findNavController().navigate(action)
            }

            topRatedButton.setOnClickListener {
                val action =
                    MovieFragmentDirections.actionHomeFragmentToViewAllFragment(
                        getString(R.string.top_rated_movies),
                        -1,
                        null
                    )
                findNavController().navigate(action)
            }

            upcomingButton.setOnClickListener {
                val action =
                    MovieFragmentDirections.actionHomeFragmentToViewAllFragment(
                        getString(R.string.upcoming_movies),
                        -1,
                        null
                    )
                findNavController().navigate(action)
            }
        }

        popularMovieAdapter.addLoadStateListener { loadState ->
            binding.apply {
                swipRefreshLayout.isRefreshing = loadState.source.refresh is LoadState.Loading
                linearLayout.isVisible = loadState.source.refresh is LoadState.NotLoading
                errorLayout.isVisible = loadState.source.refresh is LoadState.Error

                //empty view
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && popularMovieAdapter.itemCount < 1) {
                    linearLayout.isVisible = false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onRefresh() {
        initViewModel()
    }
}