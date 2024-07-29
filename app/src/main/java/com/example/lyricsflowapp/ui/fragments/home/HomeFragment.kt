package com.example.lyricsflowapp.ui.fragments.home

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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

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

        displayWelcomeMessage()
        handleUserSelection()
    }

    private fun displayWelcomeMessage() {
        val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")
        binding.tvUserName.text = username
    }

    private fun handleUserSelection() {
        binding.getSongsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_recommendationsFragment)
        }

        binding.historyBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
        }

        binding.settingsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

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
        _binding = null
    }
}
