package com.ishanvaghani.moviefy.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.ishanvaghani.moviefy.R
import com.ishanvaghani.moviefy.databinding.CastItemBinding
import com.ishanvaghani.moviefy.model.Cast
import com.ishanvaghani.moviefy.netwrok.MovieApi

class CastAdapter(private val context: Context, private var casts: List<Cast>) :
    RecyclerView.Adapter<CastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CastItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cast = casts[position]
        holder.bind(cast)
    }

    override fun getItemCount(): Int = casts.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(casts: List<Cast>) {
        this.casts = casts
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: CastItemBinding): RecyclerView.ViewHolder(binding.root) {
        
        fun bind(cast: Cast) {
            val imageUrl: String = MovieApi.PHOTO_BASE_URL + cast.profile_path
            binding.apply {
                image.load(imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_user_placeholder)
                    error(R.drawable.ic_user_placeholder)
                    transformations(CircleCropTransformation())
                }
                name.text = cast.name
            }
        }
    }
}