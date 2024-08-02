package com.example.lyricsflowapp.ui.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentHistoryBinding
import com.example.lyricsflowapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to home page
        binding.goBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        }
    }
}