package com.ishanvaghani.moviefy.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.ishanvaghani.moviefy.netwrok.MovieApi
import com.ishanvaghani.moviefy.model.Movie
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.SliderItemBinding
import com.ishanvaghani.moviefy.ui.movie.MovieFragmentDirections
import com.ishanvaghani.moviefy.ui.tv_shows.TvShowFragmentDirections
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(
    private val fragment: Fragment,
    private var sliders: List<Movie>,
    private val isMovie: Boolean
) : SliderViewAdapter<SliderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = SliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val slider = sliders[position]

        holder.bind(slider, position)
    }

    override fun getCount(): Int = sliders.size

    inner class ViewHolder(private val binding: SliderItemBinding) :
        SliderViewAdapter.ViewHolder(binding.root) {

        fun bind(movie: Movie, position: Int) {
            val posterUrl: String = MovieApi.PHOTO_BASE_URL + movie.backdropPath
            binding.apply {
                imageView.load(posterUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
                name.text = movie.title ?: movie.name
                count.text = "${position+1} / ${sliders.size}"

                root.setOnClickListener {
                    if (isMovie) {
                        val action =
                            MovieFragmentDirections.actionHomeFragmentToMovieDetailsFragment(
                                movie.id,
                                movie.title!!
                            )
                        fragment.findNavController().navigate(action)
                    } else {
                        val action =
                            TvShowFragmentDirections.actionTvShowFragmentToTvShowDetailsFragment(
                                movie.id,
                                movie.name!!
                            )
                        fragment.findNavController().navigate(action)
                    }
                }
            }
        }
    }

    fun setData(sliders: List<Movie>) {
        this.sliders = sliders
        notifyDataSetChanged()
    }
}