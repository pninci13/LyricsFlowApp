package com.example.lyricsflowapp.ui.fragments.recommendations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lyricsflowapp.databinding.FragmentRecommendationsBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class RecommendationsFragment : Fragment() {

    private var _binding: FragmentRecommendationsBinding? = null
    private val binding get() = _binding!!
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFetchRecommendations.setOnClickListener {
            val query = binding.etQuery.text.toString().trim()
            if (query.isNotEmpty()) {
                fetchRecommendations(query)
            } else {
                Toast.makeText(requireContext(), "Please enter a topic", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClear.setOnClickListener {
            binding.etQuery.text.clear()
            binding.recyclerView.visibility = View.GONE
            Toast.makeText(requireContext(), "Cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRecommendations(query: String) {
        Log.d("RecommendationsFragment", "Fetching recommendations for query: $query")
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        val url = "http://10.0.2.2:5000/recommend"  // Ensure this is the correct endpoint
        val json = JSONObject().put("query", query)
        val requestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaType(), json.toString())

        Log.d("RecommendationsFragment", "Sending request to URL: $url with body: $json")

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("RecommendationsFragment", "Network request failed: ${e.message}")
                activity?.runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch recommendations",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e(
                        "RecommendationsFragment",
                        "Network request unsuccessful: ${response.message}"
                    )
                    activity?.runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch recommendations",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return
                }
                response.body?.string()?.let { responseBody ->
                    Log.d("RecommendationsFragment", "Network response: $responseBody")
                    val songs = parseSongs(responseBody)
                    activity?.runOnUiThread {
                        displayRecommendations(songs)
                    }
                } ?: run {
                    Log.e("RecommendationsFragment", "Response body is null")
                    activity?.runOnUiThread {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch recommendations",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun parseSongs(responseBody: String): List<Song> {
        val songs = mutableListOf<Song>()
        val jsonArray = JSONArray(responseBody)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val title = jsonObject.getString("title")
            val artist = jsonObject.getString("artist")
            val imageUrl = jsonObject.getString("image_url")
            val songUrl = jsonObject.getString("song_url")
            val explanation =
                jsonObject.getString("about_section") // Assuming this is the explanation
            val score = jsonObject.getDouble("score") // Add this line
            songs.add(Song(title, artist, imageUrl, songUrl, explanation, score))
        }
        Log.d("RecommendationsFragment", "Parsed songs: $songs")
        return songs
    }

    private fun displayRecommendations(songs: List<Song>) {
        binding.progressBar.visibility = View.GONE
        if (songs.isNotEmpty()) {
            Log.d("RecommendationsFragment", "Displaying songs: $songs")
            binding.recyclerView.visibility = View.VISIBLE
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = SongAdapter(songs)
        } else {
            Toast.makeText(requireContext(), "No recommendations found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
