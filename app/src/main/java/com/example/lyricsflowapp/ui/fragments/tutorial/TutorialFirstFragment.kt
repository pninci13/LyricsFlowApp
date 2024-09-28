package com.example.lyricsflowapp.ui.fragments.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentTutorialFirstBinding

class TutorialFirstFragment : Fragment() {
    private lateinit var binding: FragmentTutorialFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to home page
        binding.goBackFromTutorialBtn.setOnClickListener {
            findNavController().navigate(R.id.action_tutorialFirstFragment_to_homeFragment)
        }


        // Go to tutorial page 2
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_tutorialFirstFragment_to_tutorialSecondFragment)
        }

    }
}