package com.ishanvaghani.moviefy.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ishanvaghani.moviefy.databinding.GenreListItemBinding
import com.ishanvaghani.moviefy.model.Genre

class GenreAdapter(
    private val context: Context,
    private var genreList: List<Genre>,
    private val onGenreClickListener: OnGenreClickListener,
    private val type: String
) :
    RecyclerView.Adapter<GenreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GenreListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding, onGenreClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = genreList[position]
        holder.bind(genre)
    }

    override fun getItemCount(): Int = genreList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(genreList: List<Genre>) {
        this.genreList = genreList
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: GenreListItemBinding,
        private val onGenreClickListener: OnGenreClickListener
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(genre: Genre) {
            binding.apply {
                type.text = genre.name
                root.setOnClickListener(this@ViewHolder)
            }
        }

        override fun onClick(p0: View?) {
            onGenreClickListener.onGenreClick(position, type, genreList[position])
        }
    }

    interface OnGenreClickListener {
        fun onGenreClick(position: Int, type: String, genre: Genre)
    }
}