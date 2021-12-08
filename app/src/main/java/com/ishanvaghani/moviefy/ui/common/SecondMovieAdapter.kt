package com.ishanvaghani.moviefy.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ishanvaghani.moviefy.netwrok.MovieApi
import com.ishanvaghani.moviefy.model.Movie
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.SecondMovieItemBinding
import com.ishanvaghani.moviefy.ui.search.SearchFragmentDirections
import com.ishanvaghani.moviefy.ui.view_all.ViewAllFragmentDirections

class SecondMovieAdapter(
    private val fragment: Fragment,
    private val isMovie: Boolean
) :
    PagingDataAdapter<Movie, SecondMovieAdapter.SecondMovieViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecondMovieViewHolder {
        val view =
            SecondMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SecondMovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: SecondMovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie!!)
    }

    inner class SecondMovieViewHolder(private val binding: SecondMovieItemBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        fun bind(movie: Movie) {
            val posterUrl: String = MovieApi.PHOTO_BASE_URL + movie.posterPath

            binding.imageView.load(posterUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }


            binding.root.setOnClickListener {
                if (isMovie) {
                    if (fragment.findNavController().currentDestination?.id == R.id.searchFragment) {
                        if (movie.title == null) {
                            val action =
                                SearchFragmentDirections.actionSearchFragmentToTvShowDetailsFragment(
                                    movie.id,
                                    movie.name!!
                                )
                            fragment.findNavController().navigate(action)
                        } else {
                            val action =
                                SearchFragmentDirections.actionSearchFragmentToMovieDetailsFragment(
                                    movie.id,
                                    movie.title
                                )
                            fragment.findNavController().navigate(action)
                        }
                    }
                    if (fragment.findNavController().currentDestination?.id == R.id.viewAllFragment) {
                        val action =
                            ViewAllFragmentDirections.actionViewAllFragmentToMovieDetailsFragment(
                                movie.id,
                                movie.title!!
                            )
                        fragment.findNavController().navigate(action)
                    }
                } else {
                    if (fragment.findNavController().currentDestination?.id == R.id.viewAllFragment) {
                        val action =
                            ViewAllFragmentDirections.actionViewAllFragmentToTvShowDetailsFragment(
                                movie.id,
                                movie.name!!
                            )
                        fragment.findNavController().navigate(action)
                    }
                }
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem == newItem
        }
    }
}