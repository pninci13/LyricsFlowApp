package com.example.lyricsflowapp.ui.fragments.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentTutorialSecondBinding

class TutorialSecondFragment : Fragment() {
    private lateinit var binding: FragmentTutorialSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to home page
        binding.previousButton.setOnClickListener {
            findNavController().navigate(R.id.action_tutorialSecondFragment_to_tutorialFirstFragment)
        }

        // Go back to next tutorial page 3
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_tutorialSecondFragment_to_tutorialThirdFragment)
        }
    }

}