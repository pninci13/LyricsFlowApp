package com.example.lyricsflowapp.ui.fragments.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentRegisterAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterAccountFragment : Fragment() {

    private var _binding: FragmentRegisterAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        initClickListener()
    }

    private fun initClickListener() {
        binding.registerAccountButton.setOnClickListener { validateUserInputData() }
    }

    private fun validateUserInputData() {
        val email = binding.createAccEmailInput.text.toString().trim()
        val password = binding.createAccPasswordInput.text.toString().trim()
        val username = binding.createAccUsernameInput.text.toString().trim()

        if (username.isNotEmpty()) {
            if (email.isNotEmpty()) {
                if (password.isNotEmpty()) {
                    binding.createAccProgressBar.isVisible = true
                    registerUser(email, password, username)
                } else {
                    Toast.makeText(requireContext(), "Please fill out the password field!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill out the email field!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please fill out the username field!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userMap = hashMapOf(
                        "username" to username,
                        "email" to email
                    )

                    if (userId != null) {
                        database.child("users").child(userId).setValue(userMap)
                            .addOnSuccessListener {
                                saveUsernameToSharedPreferences(username)
                                findNavController().navigate(R.id.action_registerAccountFragment_to_homeFragment)
                            }
                            .addOnFailureListener { e ->
                                binding.createAccProgressBar.isVisible = false
                                Toast.makeText(requireContext(), "Failed to save user data", Toast.LENGTH_SHORT).show()
                                e.printStackTrace()
                            }
                    }
                } else {
                    binding.createAccProgressBar.isVisible = false
                    Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
                    task.exception?.printStackTrace()
                }
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
