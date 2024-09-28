package com.example.lyricsflowapp.ui.fragments.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentTutorialThirdBinding

class TutorialThirdFragment : Fragment() {
    private lateinit var binding: FragmentTutorialThirdBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to previous tutorial
        binding.previousButton.setOnClickListener {
            findNavController().navigate(R.id.action_tutorialThirdFragment_to_tutorialSecondFragment)
        }

        // Go next tutorial
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_tutorialThirdFragment_to_tutorialFourthFragment)
        }
    }
}