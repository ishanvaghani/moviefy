package com.ishanvaghani.moviefy.ui.movie_details

import android.annotation.SuppressLint
import android.graphics.Rect
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
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ishanvaghani.moviefy.netwrok.MovieApi
import com.ishanvaghani.moviefy.ui.common.MovieAdapter
import com.ishanvaghani.moviefy.model.MovieDetails
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.FragmentMovieDetailsBinding
import com.ishanvaghani.moviefy.ui.common.CastAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: MovieDetailsFragmentArgs by navArgs()
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModels()

    private lateinit var similarAdapter: MovieAdapter
    private lateinit var castAdapter: CastAdapter
    private lateinit var toolbar: Toolbar

    private lateinit var key: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        toolbar = binding.toolbar
        toolbar.title = args.title
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        similarAdapter = MovieAdapter(this, true)
        castAdapter = CastAdapter(requireContext(), listOf())

        initViewModel()

        binding.retryButton.setOnClickListener { initViewModel() }

        return binding.root
    }

    private fun initViewModel() {
        showProgressBar()
        movieDetailsViewModel.apply {
            readyMovieDetails(args.movieId)
            getMovieDetails().observe(viewLifecycleOwner, {
                if (it != null) {
                    bindWithUi(it)
                    showData()
                    readyTrailer(it.id)
                } else {
                    showError()
                }
            })

            similarMovies(args.movieId).observe(viewLifecycleOwner) {
                similarAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                binding.similarRecyclerView.scheduleLayoutAnimation()
            }

            readyMovieCasts(args.movieId)
            getMovieCasts().observe(viewLifecycleOwner, {
                if (it != null) {
                    binding.castLayout.isVisible = true
                    castAdapter.updateData(it)
                }
            })
        }
    }

    private fun readyTrailer(id: Int) {
        movieDetailsViewModel.readyMovieVideos(id)
        movieDetailsViewModel.getMovieVideos().observe(viewLifecycleOwner, {
            if (it != null && it.isNotEmpty()) {
                key = if (it.size == 1) {
                    it[0].key
                } else {
                    it[it.size - 1].key
                }
                binding.playVideo.visibility = View.VISIBLE
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun bindWithUi(movieDetails: MovieDetails) {
        binding.apply {
            movieTitle.text = movieDetails.title
            movieTagline.isVisible = movieDetails.tagline.isNotEmpty()
            movieTagline.text = movieDetails.tagline
            movieReleaseDate.text = movieDetails.releaseDate
            movieRating.text = movieDetails.rating.toString()
            movieRuntime.text = movieDetails.runtime.toString() + " " + getString(R.string.minutes)
            movieOverview.text = movieDetails.overview

            val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)
            movieBudget.text = formatCurrency.format(movieDetails.budget)
            movieRevenue.text = formatCurrency.format(movieDetails.revenue)

            val posterUrl: String = MovieApi.PHOTO_BASE_URL + movieDetails.posterPath

            binding.poster.load(posterUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            var movieGenreList = ""
            movieDetails.genres.forEachIndexed { index, genre ->
                if (index == 0) {
                    movieGenreList += genre.name
                } else {
                    movieGenreList = movieGenreList + ", " + genre.name
                }
            }

            movieGenres.text = movieGenreList

            similarRecyclerView.apply {
                setHasFixedSize(true)
                adapter = similarAdapter
            }

            castsRecyclerview.apply {
                val defaultPadding = resources.getDimension(R.dimen.default_padding_fourth).toInt()
                setHasFixedSize(true)
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.right = defaultPadding
                    }
                })
                adapter = castAdapter
            }

            playVideo.setOnClickListener {
                val action =
                    MovieDetailsFragmentDirections.actionMovieDetailsFragmentToVideoActivity(key)
                findNavController().navigate(action)
            }
        }

        similarAdapter.addLoadStateListener { loadState ->
            binding.apply {

                //empty view
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && similarAdapter.itemCount < 1) {
                    viewSimilar.isVisible = false
                    textSimilar.isVisible = false
                }
            }
        }
    }

    private fun showData() {
        binding.apply {
            errorLayout.isVisible = false
            linearLayout.isVisible = true
            progressBar.isVisible = false
        }
    }

    private fun showProgressBar() {
        binding.apply {
            errorLayout.isVisible = false
            linearLayout.isVisible = false
            progressBar.isVisible = true
        }
    }

    private fun showError() {
        binding.apply {
            errorLayout.isVisible = true
            linearLayout.isVisible = false
            progressBar.isVisible = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}