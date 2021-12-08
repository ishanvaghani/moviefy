package com.ishanvaghani.moviefy.ui.genres

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.FragmentGenresBinding
import com.ishanvaghani.moviefy.model.Genre
import com.ishanvaghani.moviefy.ui.common.GenreAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenresFragment : Fragment(), GenreAdapter.OnGenreClickListener {

    private var _binding: FragmentGenresBinding? = null
    private val binding get() = _binding

    private lateinit var genreAdapter: GenreAdapter

    private val args: GenresFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGenresBinding.inflate(inflater, container, false)

        val toolbar = binding?.toolbar
        if (args.type == getString(R.string.tv_show)) {
            toolbar?.title = getString(R.string.tv_show_genres)
        } else {
            toolbar?.title = getString(R.string.movie_genres)
        }
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        genreAdapter = GenreAdapter(requireContext(), args.data.toList(), this, args.type)

        bindUI()

        return binding?.root
    }

    private fun bindUI() {
        binding?.apply {
            genreRecyclerView.apply {
                val defaultPadding = resources.getDimension(R.dimen.default_padding_half).toInt()
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
                            if (position % 2 == 0) defaultPadding else defaultPadding / 2
                        outRect.right =
                            if (position % 2 == 0) defaultPadding / 2 else defaultPadding
                        outRect.bottom = defaultPadding
                        if (position == 0 || position == 1) {
                            outRect.top = defaultPadding
                        }
                    }
                })
                adapter = genreAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onGenreClick(position: Int, type: String, genre: Genre) {
        if (type == context?.getString(R.string.tv_show)) {
            val action =
                GenresFragmentDirections.actionGenresFragmentToViewAllFragment(
                    type = getString(R.string.tv_show),
                    genreId = genre.id,
                    genreName = genre.name
                )
            findNavController().navigate(action)
        } else {
            val action =
                GenresFragmentDirections.actionGenresFragmentToViewAllFragment(
                    type = getString(R.string.movie),
                    genreId = genre.id,
                    genreName = genre.name
                )
            findNavController().navigate(action)
        }
    }
}