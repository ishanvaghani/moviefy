package com.ishanvaghani.moviefy.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ishanvaghani.moviefy.databinding.HistoryItemBinding
import com.ishanvaghani.moviefy.room.history.History

class HistoryAdapter(
    private val context: Context,
    private var histories: List<History>,
    private val onRemoveHistoryListener: OnRemoveHistoryListener,
    private val onSearchHistoryListener: OnSearchHistoryListener
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding, onRemoveHistoryListener, onSearchHistoryListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = histories[position]
        holder.bind(history)
    }

    override fun getItemCount(): Int = histories.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(histories: List<History>) {
        this.histories = histories
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: HistoryItemBinding,
        private val onRemoveHistoryListener: OnRemoveHistoryListener,
        private val onSearchHistoryListener: OnSearchHistoryListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(history: History) {
            binding.name.text = history.value
            binding.remove.setOnClickListener {
                onRemoveHistoryListener.onRemoveHistoryClick(histories[position])
            }
            binding.name.setOnClickListener {
                onSearchHistoryListener.onSearchHistoryClick(histories[position])
            }
        }
    }

    interface OnRemoveHistoryListener {
        fun onRemoveHistoryClick(history: History)
    }

    interface OnSearchHistoryListener {
        fun onSearchHistoryClick(history: History)
    }
}