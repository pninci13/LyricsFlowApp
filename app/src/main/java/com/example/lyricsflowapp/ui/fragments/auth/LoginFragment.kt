package com.example.lyricsflowapp.ui.fragments.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentLoginBinding
import com.example.lyricsflowapp.ui.helpers.AlertHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

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
        database = FirebaseDatabase.getInstance().reference
        initClicks()
    }

    private fun initClicks() {
        binding.loginBtn.setOnClickListener { validateUserInputData() }
        binding.forgotPasswordBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverAccountFragment)
        }
        binding.goBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_landingPageFragment)
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
                AlertHelper.showWarningDialog(requireActivity(), "Warning", "Fill out the password field!") {
                }
            }
        } else {
            AlertHelper.showWarningDialog(requireActivity(), "Warning", "Fill out the email field!") {
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userId = it.uid
                        fetchUsernameFromDatabase(userId)
                    }
                } else {
                    binding.loginProgressBar.isVisible = false
                    AlertHelper.showErrorDialog(requireActivity(), "Error", "Authentication Failed: ${task.exception?.message}, please try again!"){
                    }
                }
            }
            .addOnFailureListener { exception ->
                binding.loginProgressBar.isVisible = false
                AlertHelper.showErrorDialog(requireActivity(), "Error", "Authentication Failed: ${exception.message}, please try again!"){}
            }
    }

    private fun fetchUsernameFromDatabase(userId: String) {
        database.child("users").child(userId).get()
            .addOnSuccessListener { snapshot ->
                val username = snapshot.child("username").getValue(String::class.java) ?: "User"
                saveUsernameToSharedPreferences(username)
                binding.loginProgressBar.isVisible = false
                findNavController().navigate(R.id.action_authentication_to_homeFragment)
            }
            .addOnFailureListener { exception ->
                binding.loginProgressBar.isVisible = false
                AlertHelper.showAlertDialog(requireActivity(), "Failed to fetch user data: ${exception.message}")
            }
    }

    private fun saveUsernameToSharedPreferences(username: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
