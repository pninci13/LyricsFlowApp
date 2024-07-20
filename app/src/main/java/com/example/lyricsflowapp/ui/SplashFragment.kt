package com.example.lyricsflowapp.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentSplashBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Wait 2 seconds on splash screen  and then goes to login page
        Handler(Looper.getMainLooper()).postDelayed(this::authenticateUser, 2000)
    }

    private fun authenticateUser() {
        auth = Firebase.auth

        if (auth.currentUser == null) {         // User not authenticated in the app
            findNavController().navigate(R.id.action_splashFragment_to_authentication)
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}