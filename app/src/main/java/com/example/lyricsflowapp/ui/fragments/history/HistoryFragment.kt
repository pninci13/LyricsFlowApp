package com.example.lyricsflowapp.ui.fragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentHistoryBinding
import com.example.lyricsflowapp.ui.helpers.FirebaseRepository

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var repository: FirebaseRepository
    private lateinit var adapter: HistoryAdapter
    private val userId = "someUserId" // Replace with actual user ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        repository = FirebaseRepository()

        adapter = HistoryAdapter(mutableListOf()) { historyItem ->
            historyItem.searchQuery?.let {
                repository.deleteHistory(userId, it)
                loadHistory()
            }
        }
        binding.historyRecyclerView.adapter = adapter
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.clearAllHistoryButton.setOnClickListener {
            repository.clearHistory(userId)
            loadHistory()
        }

        loadHistory()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to home page
        binding.goBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_historyFragment_to_homeFragment)
        }
    }

    private fun loadHistory() {
        repository.getHistory(userId) { historyList ->
            adapter.updateHistory(historyList)
        }
    }
}
