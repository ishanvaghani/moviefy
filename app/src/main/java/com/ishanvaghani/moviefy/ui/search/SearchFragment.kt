package com.ishanvaghani.moviefy.ui.search

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ishanvaghani.moviefy.ui.common.GenreAdapter
import com.ishanvaghani.moviefy.ui.common.SecondMovieAdapter
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.FragmentSearchBinding
import com.ishanvaghani.moviefy.model.Genre
import com.ishanvaghani.moviefy.room.history.History
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class SearchFragment : Fragment(), GenreAdapter.OnGenreClickListener,
    HistoryAdapter.OnRemoveHistoryListener, HistoryAdapter.OnSearchHistoryListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var secondMovieAdapter: SecondMovieAdapter
    private lateinit var movieGenreAdapter: GenreAdapter
    private lateinit var tvShowGenreAdapter: GenreAdapter

    private val moviesGenres = ArrayList<Genre>()
    private val tvShowsGenres = ArrayList<Genre>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)

        secondMovieAdapter = SecondMovieAdapter(this, true)
        movieGenreAdapter = GenreAdapter(
            requireContext(),
            listOf(),
            this,
            requireContext().getString(R.string.movie)
        )
        tvShowGenreAdapter = GenreAdapter(
            requireContext(),
            listOf(),
            this,
            requireContext().getString(R.string.tv_show)
        )

        bindUI()
        initObservers()
        handleBackPress()

        return binding.root
    }

    private fun handleBackPress() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {
                if (binding.emptyState.isVisible) {
                    findNavController().popBackStack()
                } else {
                    binding.apply {
                        recyclerView.isVisible = false
                        noResultFound.isVisible = false
                        back.isVisible = false
                        emptyState.isVisible = true
                        editText.setText("")
                    }
                }
            }
    }

    private fun search() {
        binding.back.isVisible = true
        val text = binding.editText.text.toString().trim()
        searchViewModel.insert(History(text.lowercase()))
        searchViewModel.search(text)
            .observe(viewLifecycleOwner) {
                secondMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                binding.recyclerView.scheduleLayoutAnimation()
                binding.editText.clearFocus()
            }
    }

    private fun bindUI() {

        binding.apply {
            val defaultPadding = resources.getDimension(R.dimen.default_padding_half).toInt()
            moviesGenreRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 2)
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        outRect.left =
                            if (position % 2 == 0) 0 else defaultPadding / 2
                        outRect.right =
                            if (position % 2 == 0) defaultPadding / 2 else 0
                        outRect.bottom = defaultPadding
                        if (position == 0 || position == 1) {
                            outRect.top = defaultPadding
                        }
                    }
                })
                adapter = movieGenreAdapter
            }

            tvShowsGenreRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 2)
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        outRect.left =
                            if (position % 2 == 0) 0 else defaultPadding / 2
                        outRect.right =
                            if (position % 2 == 0) defaultPadding / 2 else 0
                        outRect.bottom = defaultPadding
                        if (position == 0 || position == 1) {
                            outRect.top = defaultPadding
                        }
                    }
                })
                adapter = tvShowGenreAdapter
            }

            val historyAdapter =
                HistoryAdapter(requireContext(), listOf(), this@SearchFragment, this@SearchFragment)

            historyRecyclerview.apply {
                setHasFixedSize(true)
                adapter = historyAdapter
            }
            searchViewModel.histories.observe(viewLifecycleOwner, {
                historyAdapter.updateData(it)
            })

            editText.setOnEditorActionListener { _, actionId, _ ->
                emptyState.isVisible = false
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchViewModel.apply {
                        search()
                    }
                }
                true
            }

            movieGenreRetryButton.setOnClickListener {
                initObservers()
            }

            tvShowGenreRetryButton.setOnClickListener {
                initObservers()
            }

            back.setOnClickListener {
                binding.apply {
                    recyclerView.isVisible = false
                    noResultFound.isVisible = false
                    back.isVisible = false
                    emptyState.isVisible = true
                    editText.setText("")
                }
            }
        }

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 3)
            adapter = secondMovieAdapter
        }

        secondMovieAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                if (binding.editText.text.isNotEmpty()) {
                    recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                }
                errorText.isVisible = loadState.source.refresh is LoadState.Error

                //empty view
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && secondMovieAdapter.itemCount < 1) {
                    recyclerView.isVisible = false
                    noResultFound.isVisible = true
                }
            }
        }

    }

    private fun initObservers() {
        binding.apply {
            moviesGenreProgressBar.isVisible = true
            tvShowsGenreProgressBar.isVisible = true
            tvShowGenreErrorLayout.isVisible = false
            movieGenreErrorLayout.isVisible = false
        }
        searchViewModel.apply {
            readyMovieGenre()
            readyTVShowGenre()
            getMovieGenres().observe(viewLifecycleOwner, {
                if (it != null) {
                    moviesGenres.clear()
                    moviesGenres.addAll(it)
                    val data = it.subList(0, 3)
                    data.add(Genre("See All", -1))
                    movieGenreAdapter.updateData(data)
                    binding.moviesGenreProgressBar.isVisible = false
                } else {
                    binding.apply {
                        moviesGenreProgressBar.isVisible = false
                        movieGenreErrorLayout.isVisible = true
                    }
                }
            })

            getTVShowGenres().observe(viewLifecycleOwner, {
                if (it != null) {
                    tvShowsGenres.clear()
                    tvShowsGenres.addAll(it)
                    val data = it.subList(0, 3)
                    data.add(Genre("See All", -1))
                    tvShowGenreAdapter.updateData(data)
                    binding.tvShowsGenreProgressBar.isVisible = false
                } else {
                    binding.apply {
                        tvShowsGenreProgressBar.isVisible = false
                        tvShowGenreErrorLayout.isVisible = true
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.editText.text.toString().isNotEmpty()) {
            search()
            binding.recyclerView.isVisible = true
            binding.emptyState.isVisible = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onGenreClick(position: Int, type: String, genre: Genre) {
        binding.emptyState.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        if (genre.id == -1) {
            if(type == context?.getString(R.string.tv_show)) {
                val action = SearchFragmentDirections.actionSearchFragmentToGenresFragment(
                    type,
                    tvShowsGenres.toTypedArray()
                )
                findNavController().navigate(action)
            }
            else {
                val action = SearchFragmentDirections.actionSearchFragmentToGenresFragment(
                    type,
                    moviesGenres.toTypedArray()
                )
                findNavController().navigate(action)
            }
        } else {
            if (type == context?.getString(R.string.tv_show)) {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToViewAllFragment(
                        type = getString(R.string.tv_show),
                        genreId = genre.id,
                        genreName = genre.name
                    )
                findNavController().navigate(action)
            } else {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToViewAllFragment(
                        type = getString(R.string.movie),
                        genreId = genre.id,
                        genreName = genre.name
                    )
                findNavController().navigate(action)
            }
        }
    }

    override fun onRemoveHistoryClick(history: History) {
        searchViewModel.delete(history)
    }

    override fun onSearchHistoryClick(history: History) {
        binding.back.isVisible = true
        binding.editText.setText(history.value)
        searchViewModel.search(history.value)
            .observe(viewLifecycleOwner) {
                secondMovieAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                binding.recyclerView.scheduleLayoutAnimation()
                binding.editText.clearFocus()
                binding.recyclerView.isVisible = true
                binding.emptyState.isVisible = false
            }
    }
}