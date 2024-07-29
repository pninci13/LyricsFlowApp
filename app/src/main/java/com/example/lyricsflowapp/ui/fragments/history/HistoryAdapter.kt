package com.example.lyricsflowapp.ui.fragments.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lyricsflowapp.databinding.ListItemHistoryBinding

class HistoryAdapter(
    private var historyList: MutableList<SearchHistory>,
    private val onDeleteClick: (SearchHistory) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ListItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.bind(item)
    }

    override fun getItemCount() = historyList.size

    fun updateHistory(newHistoryList: List<SearchHistory>) {
        historyList.clear()
        historyList.addAll(newHistoryList)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(private val binding: ListItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: SearchHistory) {
            binding.topicText.text = "Topic Searched\t: ${item.searchQuery}"
            binding.matchText.text = "Top Song\t: ${item.topMatch}"
            binding.deleteButton.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }
}
