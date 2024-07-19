package com.example.lyricsflowapp.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        initClicks()
    }

    private fun initClicks() {
        binding.loginBtn.setOnClickListener { validateUserInputData() }
        binding.forgotPasswordBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverAccountFragment)
        }
    }

    private fun validateUserInputData() {
        val email = binding.loginEmailInput.text.toString().trim()
        val password = binding.loginPasswordInput.text.toString().trim()

        if (email.isNotEmpty()) {
            if (password.isNotEmpty()) {
                binding.loginProgressBar.isVisible = true
                loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill out the password field! ", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please fill out the email field! ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser(email: String, password: String) {
        Log.d("LoginFragment", "Attempting to sign in with email: $email")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginFragment", "signInWithEmail:success")
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Log.w("LoginFragment", "signInWithEmail:failure", task.exception)
                    binding.loginProgressBar.isVisible = false
                    Toast.makeText(requireContext(), "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LoginFragment", "signInWithEmail:failure", exception)
                binding.loginProgressBar.isVisible = false
                Toast.makeText(requireContext(), "Authentication Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
