package com.example.lyricsflowapp.ui.fragments.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentTutorialFourthBinding
import com.example.lyricsflowapp.databinding.FragmentTutorialThirdBinding

class TutorialFourthFragment : Fragment() {

    private lateinit var binding: FragmentTutorialFourthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialFourthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to previous tutorial
        binding.previousButton.setOnClickListener {
            findNavController().navigate(R.id.action_tutorialFourthFragment_to_tutorialThirdFragment)
        }

        // Go next tutorial
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_tutorialFourthFragment_to_tutorialFifthFragment)
        }
    }
}







