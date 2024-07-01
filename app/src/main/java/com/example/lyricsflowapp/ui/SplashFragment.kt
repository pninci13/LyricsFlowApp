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

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Wait 3 seconds on splash screen  and then goes to login page
        Handler(Looper.getMainLooper()).postDelayed(this::authenticateUser, 2000)
    }

    private fun authenticateUser() {
        findNavController().navigate(R.id.action_splashFragment_to_landingPageFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}