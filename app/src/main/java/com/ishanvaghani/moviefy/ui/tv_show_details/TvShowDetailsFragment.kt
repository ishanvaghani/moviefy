package com.ishanvaghani.moviefy.ui.tv_show_details

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
import com.ishanvaghani.moviefy.model.TvShowDetails
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.FragmentTvshowDetailsBinding
import com.ishanvaghani.moviefy.ui.common.CastAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TvShowDetailsFragment : Fragment() {

    private var _binding: FragmentTvshowDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: TvShowDetailsFragmentArgs by navArgs()
    private val tvShowDetailsViewModel: TvShowDetailsViewModel by viewModels()

    private lateinit var similarAdapter: MovieAdapter
    private lateinit var seasonAdapter: SeasonAdapter
    private lateinit var castAdapter: CastAdapter
    private lateinit var toolbar: Toolbar

    private lateinit var key: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvshowDetailsBinding.inflate(inflater, container, false)

        toolbar = binding.toolbar
        toolbar.title = args.title
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        similarAdapter = MovieAdapter(this, false)
        castAdapter = CastAdapter(requireContext(), listOf())

        initViewModel()

        binding.retryButton.setOnClickListener { initViewModel() }

        return binding.root
    }

    private fun initViewModel() {
        showProgressBar()
        tvShowDetailsViewModel.apply {
            readyTvShowDetails(args.tvShowId)
            getTvShowDetails().observe(viewLifecycleOwner, {
                if (it != null) {
                    bindWithUi(it)
                    showData()
                    readyTrailer(it.id)
                } else {
                    showError()
                }
            })

            similarTvShow(args.tvShowId).observe(viewLifecycleOwner) {
                similarAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                binding.similarRecyclerView.scheduleLayoutAnimation()
            }

            readyTvShowCasts(args.tvShowId)
            getTvShowCasts().observe(viewLifecycleOwner, {
                if(it != null) {
                    binding.castLayout.isVisible = true
                    castAdapter.updateData(it)
                }
            })
        }
    }

    private fun readyTrailer(id: Int) {
        tvShowDetailsViewModel.readyTvShowVideos(id)
        tvShowDetailsViewModel.getTvShowVideos().observe(viewLifecycleOwner, {
            if(it != null && it.isNotEmpty()) {
                key = if(it.size == 1) {
                    it[0].key
                } else {
                    it[it.size - 1].key
                }
                binding.playVideo.visibility = View.VISIBLE
            }
        })
    }

    private fun bindWithUi(tvShowDetails: TvShowDetails) {

        seasonAdapter = SeasonAdapter(this, tvShowDetails.seasons)

        binding.apply {
            movieTitle.text = tvShowDetails.name
            movieTagline.isVisible = tvShowDetails.tagline.isNotEmpty()
            movieTagline.text = tvShowDetails.tagline
            firstReleaseDate.text = tvShowDetails.firstRelease
            lastReleaseDate.text = tvShowDetails.lastRelease
            movieRating.text = tvShowDetails.rating.toString()
            noOfSeason.text = tvShowDetails.numberOfSeasons.toString()
            movieOverview.text = tvShowDetails.overview
            noOfEpisode.text = tvShowDetails.numberOfEpisodes.toString()

            val posterUrl: String = MovieApi.PHOTO_BASE_URL + tvShowDetails.posterPath
            binding.poster.load(posterUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            similarRecyclerView.apply {
                setHasFixedSize(true)
                adapter = similarAdapter
            }

            seasonsRecyclerView.apply {
                setHasFixedSize(true)
                adapter = seasonAdapter
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
                    TvShowDetailsFragmentDirections.actionTvShowDetailsFragmentToVideoActivity(key)
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