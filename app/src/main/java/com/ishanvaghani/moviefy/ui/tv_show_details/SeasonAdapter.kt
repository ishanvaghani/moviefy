package com.ishanvaghani.moviefy.ui.tv_show_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ishanvaghani.moviefy.netwrok.MovieApi
import com.ishanvaghani.moviefy.model.TvShowDetails
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.SeasonItemBinding

class SeasonAdapter(
    private val fragment: Fragment,
    private val seasonList: List<TvShowDetails.Season>
) : RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder>() {

    private var onAttach = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val view = SeasonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeasonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        val season = seasonList[position]
        holder.bind(season)
    }

    override fun getItemCount(): Int = seasonList.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                onAttach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
    }

    inner class SeasonViewHolder(private val binding: SeasonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(season: TvShowDetails.Season) {
            val posterUrl: String = MovieApi.PHOTO_BASE_URL + season.posterPath

            binding.apply {
                binding.imageView.load(posterUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }

                seasonName.text = season.name
            }
        }
    }
}