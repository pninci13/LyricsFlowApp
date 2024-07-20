package com.example.lyricsflowapp.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentRecoverAccountBinding
import com.example.lyricsflowapp.ui.helpers.AlertHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverAccountFragment : Fragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        initClicks()
    }

    private fun initClicks() {
        binding.sendRecoverAccountButton.setOnClickListener { validateUserInputData() }
        binding.goBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_recoverAccountFragment_to_loginFragment)
        }
    }

    private fun validateUserInputData() {
        val email = binding.recoverAccountEmailInput.text.toString().trim()

        if (email.isNotEmpty()) {
            binding.recoverAccProgressBar.isVisible = true
            recoverUserAccount(email)
        } else {
            AlertHelper.showAlertDialog(requireActivity(),"Please fill out the email field!")
        }
    }

    private fun recoverUserAccount(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    binding.recoverAccProgressBar.isVisible = false
                    AlertHelper.showSuccessDialog(requireActivity(), "Success", "Recovery email sent successfully!") {
                        findNavController().navigate(R.id.action_recoverAccountFragment_to_loginFragment)
                    }
                } else {
                    binding.recoverAccProgressBar.isVisible = false
                    AlertHelper.showAlertDialog(requireActivity(), "Failed to send recovery email!")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
