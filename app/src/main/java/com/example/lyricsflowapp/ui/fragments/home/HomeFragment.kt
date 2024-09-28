package com.example.lyricsflowapp.ui.fragments.home

import android.annotation.SuppressLint
import android.app.Dialog
import com.example.lyricsflowapp.ui.fragments.spotify.SpotifyAppHandler
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentHomeBinding
import com.example.lyricsflowapp.ui.helpers.AlertHelper
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

    private fun loadUsername() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = database.child(userId).child("username")
            // Add a ValueEventListener to listen for real-time changes
            usernameListener = userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (isAdded && view != null) {
                        val username = snapshot.getValue(String::class.java)
                        // Set the username or fallback to "User" if null
                        binding.tvUserName.text = username ?: "User"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any database errors, show a Toast for feedback
                    Toast.makeText(requireContext(), "Failed to load username", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // If userId is null (user not logged in), set default "User"
            binding.tvUserName.text = "User"
            // Optionally show an error or redirect to login screen
            AlertHelper.showErrorDialog(requireActivity(), "Error", "User is not authenticated!") {}
        }
    }

    // Remove the listener in onStop to avoid memory leaks
    override fun onStop() {
        super.onStop()
        val userId = auth.currentUser?.uid
        if (userId != null && usernameListener != null) {
            // Remove the event listener if it exists
            val userRef = database.child(userId).child("username")
            userRef.removeEventListener(usernameListener!!)
            // Set usernameListener to null after removal
            usernameListener = null
        }
    }

    // Call loadUsername in onResume to reload the username when resuming the activity/fragment
    override fun onResume() {
        super.onResume()
        loadUsername()
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

        // Tutorial button
        binding.tutorialBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_tutorialFirstFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun userLogout() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_logout_confirmation)

        val logoutTextView: TextView = dialog.findViewById(R.id.logoutMessage)
        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val btnNo: Button = dialog.findViewById(R.id.btnNo)

        logoutTextView.text = "Are you sure you want to logout?"

        // Handle the "Yes" button action
        btnYes.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            auth.signOut()
            AlertHelper.showSuccessDialog(requireActivity(), "Success", "Logged out successfully!") {
                findNavController().navigate(R.id.action_homeFragment_to_authentication)
            }
            dialog.dismiss()
        }

        // Handle the "No" button action
        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        // Remove the default white borders by setting the background to null
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
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
