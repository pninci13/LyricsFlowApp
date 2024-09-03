package com.example.lyricsflowapp.ui.fragments.home

import com.example.lyricsflowapp.ui.fragments.spotify.SpotifyAppHandler
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var usernameListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        handleUserSelection()
    }

    override fun onResume() {
        super.onResume()
        loadUsername()
    }

    private fun loadUsername() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = database.child(userId).child("username")
            usernameListener = userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isAdded && view != null) {
                        val username = snapshot.getValue(String::class.java)
                        binding.tvUserName.text = username ?: "User"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error if needed
                }
            })
        }
    }

    private fun handleUserSelection() {
        val spotifyAppHandler = SpotifyAppHandler()

        // Get recommendations button
        binding.getSongsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_recommendationsFragment)
        }

        // History button
        binding.historyBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
        }

        // Spotify button
        binding.spotifyBtn.setOnClickListener {
            spotifyAppHandler.openSpotifyApp(requireContext())
        }

        // Settings button
        binding.settingsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        // Logout button
        binding.btnLogout.setOnClickListener {
            userLogout()
        }
    }

    private fun userLogout() {
        val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        auth.signOut()
        findNavController().navigate(R.id.action_homeFragment_to_authentication)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the listener to avoid updating UI when the fragment is not active
        val userId = auth.currentUser?.uid
        if (userId != null && usernameListener != null) {
            val userRef = database.child(userId).child("username")
            userRef.removeEventListener(usernameListener!!)
        }
        _binding = null
    }
}
