package com.ishanvaghani.moviefy.ui.view_all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.ishanvaghani.moviefy.ui.common.SecondMovieAdapter
import com.ishanvaghani.moviefy.pagination.MovieLoadStateAdapter
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.FragmentViewAllBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewAllFragment : Fragment() {

    private var _binding: FragmentViewAllBinding? = null
    private val binding get() = _binding!!

    private val viewAllViewModel: ViewAllViewModel by viewModels()

    private lateinit var secondMovieAdapter: SecondMovieAdapter
    private lateinit var toolbar: Toolbar

    private val args: ViewAllFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewAllBinding.inflate(layoutInflater, container, false)

        toolbar = binding.toolbar
        if (args.genreId == -1) {
            toolbar.title = args.type
        } else {
            if (args.type == getString(R.string.movie)) {
                toolbar.title = args.genreName + " " + getString(R.string.movies)
            } else {
                toolbar.title = args.genreName + " " + getString(R.string.tv_shows)
            }
        }
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        secondMovieAdapter =
            if (args.type == getString(R.string.popular_movies) || args.type == getString(R.string.top_rated_movies)
                || args.type == getString(R.string.upcoming_movies) || args.type == getString(R.string.movie)
            ) {
                SecondMovieAdapter(this, true)
            } else {
                SecondMovieAdapter(this, false)
            }

        bindUI()

        return binding.root
    }

    private fun bindUI() {
        if (args.type == getString(R.string.popular_movies)) {
            viewAllViewModel.popularMovies.observe(viewLifecycleOwner) {
                secondMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        if (args.type == getString(R.string.top_rated_movies)) {
            viewAllViewModel.topRatedMovies.observe(viewLifecycleOwner) {
                secondMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        if (args.type == getString(R.string.upcoming_movies)) {
            viewAllViewModel.upcomingMovies.observe(viewLifecycleOwner) {
                secondMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        if (args.type == getString(R.string.movie)) {
            viewAllViewModel.getGenreMovies(args.genreId)
                .observe(viewLifecycleOwner) {
                    secondMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
        }
        if (args.type == getString(R.string.popular_tv_shows)) {
            viewAllViewModel.popularTvShow.observe(viewLifecycleOwner) {
                secondMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        if (args.type == getString(R.string.top_rated_tv_shows)) {
            viewAllViewModel.topRatedTvShow.observe(viewLifecycleOwner) {
                secondMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        if (args.type == getString(R.string.on_the_air_tv_shows)) {
            viewAllViewModel.onTheAirTvShow.observe(viewLifecycleOwner) {
                secondMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        if (args.type == getString(R.string.tv_show)) {
            viewAllViewModel.getGenreTVShows(args.genreId)
                .observe(viewLifecycleOwner) {
                    secondMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
        }

        binding.apply {
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 3)
                adapter = secondMovieAdapter.withLoadStateHeaderAndFooter(
                    header = MovieLoadStateAdapter { secondMovieAdapter.retry() },
                    footer = MovieLoadStateAdapter { secondMovieAdapter.retry() }
                )
            }
            retryButton.setOnClickListener {
                secondMovieAdapter.retry()
            }
        }

        secondMovieAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                errorLayout.isVisible = loadState.source.refresh is LoadState.Error

                //empty view
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && secondMovieAdapter.itemCount < 1) {
                    recyclerView.isVisible = false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}