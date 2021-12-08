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
import com.ishanvaghani.moviefy.databinding.MovieItemBinding
import com.ishanvaghani.moviefy.ui.movie.MovieFragmentDirections
import com.ishanvaghani.moviefy.ui.movie_details.MovieDetailsFragmentDirections
import com.ishanvaghani.moviefy.ui.tv_show_details.TvShowDetailsFragmentDirections
import com.ishanvaghani.moviefy.ui.tv_shows.TvShowFragmentDirections

class MovieAdapter(
    private val fragment: Fragment,
    private val isMovie: Boolean,
) :
    PagingDataAdapter<Movie, MovieAdapter.MovieViewHolder>(COMPARATOR) {

    private var onAttach = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie!!)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                onAttach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
    }

    inner class MovieViewHolder(private val binding: MovieItemBinding) : RecyclerView.ViewHolder(
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
                    if (fragment.findNavController().currentDestination?.id == R.id.homeFragment) {
                        val action =
                            MovieFragmentDirections.actionHomeFragmentToMovieDetailsFragment(
                                movie.id,
                                movie.title!!
                            )
                        fragment.findNavController().navigate(action)
                    }
                    else {
                        val action = MovieDetailsFragmentDirections.actionMovieDetailsFragmentSelf(
                            movie.id,
                            movie.title!!
                        )
                        fragment.findNavController().navigate(action)
                    }
                } else {
                    if(fragment.findNavController().currentDestination?.id == R.id.tvShowFragment) {
                        val action =
                            TvShowFragmentDirections.actionTvShowFragmentToTvShowDetailsFragment(
                                movie.id,
                                movie.name!!
                            )
                        fragment.findNavController().navigate(action)
                    }
                    else {
                        val action =
                            TvShowDetailsFragmentDirections.actionTvShowDetailsFragmentSelf(
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