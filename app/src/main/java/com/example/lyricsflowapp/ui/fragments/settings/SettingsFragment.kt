package com.example.lyricsflowapp.ui.fragments.settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentSettingsBinding
import com.example.lyricsflowapp.ui.helpers.AlertHelper
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.database.FirebaseDatabase

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        // Go back to home page
        binding.goBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        }

        // Report bug
        binding.btnReportBug.setOnClickListener {
            showReportBugDialog()
        }

        // Logout from app
        binding.btnLogout.setOnClickListener {
            userLogout()
        }

        // Delete Account from app
        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        // Update Username
        binding.btnChangeUsername.setOnClickListener {
            showUpdateUsernameDialog()
        }

    }

    private fun showReportBugDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_report_bug)

        val bugDescriptionEditText: EditText = dialog.findViewById(R.id.bugDescriptionEditText)
        val sendButton: Button = dialog.findViewById(R.id.sendButton)

        // Handles the click on the input text field
        bugDescriptionEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bugDescriptionEditText.hint = ""
            } else {
                if (bugDescriptionEditText.text.isEmpty()) {
                    bugDescriptionEditText.hint = "Type Here"
                }
            }
        }

        // Listener for the "send" button
        sendButton.setOnClickListener {
            val bugDescription = bugDescriptionEditText.text.toString().trim()

            if (bugDescription.isEmpty()) {
                AlertHelper.showErrorDialog(requireActivity(), "Error", "Bug description cannot be empty!"){}
            } else {
                saveBugReportToFirebase(bugDescription)
                dialog.dismiss()
            }
        }

        // Set dialog width to match parent
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()
    }

    private fun saveBugReportToFirebase(description: String) {
        val database = FirebaseDatabase.getInstance()
        val bugReportsRef = database.getReference("bugReports")

        val bugReportId = bugReportsRef.push().key
        if (bugReportId != null) {
            val bugReport = BugReport(id = bugReportId, description = description)
            bugReportsRef.child(bugReportId).setValue(bugReport)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        AlertHelper.showErrorDialog(requireActivity(), "Success", "Bug report submitted successfully!"){}
                    } else {
                        AlertHelper.showErrorDialog(requireActivity(), "Error", "Failed to submit bug report!"){}
                    }
                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDeleteAccountDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.delete_account_dialog)

        // Assuming you have a TextView for the message and Buttons for confirming/canceling
        val titleTextView: TextView = dialog.findViewById(R.id.deleteAccountMessage)
        val confirmButton: Button = dialog.findViewById(R.id.btnYes)
        val cancelButton: Button = dialog.findViewById(R.id.btnNo)

        // Set your custom message
        titleTextView.text = "Are you sure you want to delete your account?"

        // Handle confirm button click
        confirmButton.setOnClickListener {
            deleteAccountFromFirebase()
            dialog.dismiss()

            AlertHelper.showSuccessDialog(requireActivity(), "Success", "Account deleted successfully!") {
                findNavController().navigate(R.id.action_settingsFragment_to_authentication)
            }
        }

        // Handle cancel button click
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // Set dialog width to match parent
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        dialog.show()
    }

    private fun deleteAccountFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users/$userId")

            // Delete user data from the Firebase Realtime Database
            userRef.removeValue()
                .addOnSuccessListener {
                    // Delete the user's authentication record
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                FirebaseAuth.getInstance().signOut()
                                findNavController().navigate(R.id.action_settingsFragment_to_authentication)
                            } else {
                                // Handle deletion failure due to re-authentication requirement or other errors
                                if (task.exception is FirebaseAuthRecentLoginRequiredException) {
                                    // Prompt the user to re-authenticate before deleting their account
                                    AlertHelper.showErrorDialog(requireActivity(), "Error", "Please re-authenticate to delete your account.") {}
                                } else {
                                    // Handle other potential errors (network issues, permission errors, etc.)
                                    AlertHelper.showErrorDialog(requireActivity(), "Error", "Failed to delete account. Please try again.") {}
                                }
                            }
                        }
                        ?.addOnFailureListener { exception ->
                            // Handle failure to delete the authentication record
                            AlertHelper.showErrorDialog(requireActivity(), "Error", "Failed to delete account authentication record: ${exception.message}") {}
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle failure to delete user data from Firebase Database
                    AlertHelper.showErrorDialog(requireActivity(), "Error", "Failed to delete user data: ${exception.message}") {}
                }
        } else {
            // Handle case where userId is null (user not logged in)
            AlertHelper.showErrorDialog(requireActivity(), "Error", "User is not authenticated!") {}
        }
    }

    @SuppressLint("SetTextI18n")
    private fun userLogout() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_logout_confirmation)

        val logoutTextView: TextView = dialog.findViewById(R.id.logoutMessage)
        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val btnNo: Button = dialog.findViewById(R.id.btnNo)

        logoutTextView.text = "Are you sure you want to logout?"

        // Handle the "Yes" button action
        btnYes.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            auth.signOut()
            AlertHelper.showSuccessDialog(requireActivity(), "Success", "Logged out successfully!") {
                findNavController().navigate(R.id.action_homeFragment_to_authentication)
            }
            dialog.dismiss()
        }

        // Handle the "No" button action
        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        // Remove the default white borders by setting the background to null
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }



    private fun showUpdateUsernameDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_update_username)

        val usernameEditText: EditText = dialog.findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = dialog.findViewById(R.id.passwordEditText)
        val updateButton: Button = dialog.findViewById(R.id.updateButton)

        updateButton.setOnClickListener {
            val newUsername = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (newUsername.isNotEmpty() && password.isNotEmpty()) {
                // Pass both username and password to the updated function
                updateUsernameInFirebase(newUsername, password)
                dialog.dismiss()
            } else {
                AlertHelper.showErrorDialog(requireActivity(), "Error", "Username and password can't be empty!"){}
            }
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.show()
    }

    private fun updateUsernameInFirebase(newUsername: String, password: String) {
        val user = auth.currentUser
        if (user != null) {
            // Get the user's credentials (for re-authentication)
            val credential = EmailAuthProvider.getCredential(user.email!!, password)

            // Re-authenticate the user
            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // If re-authentication is successful, proceed to update the username
                        val userId = user.uid
                        val userRef = FirebaseDatabase.getInstance().getReference("users/$userId")
                        userRef.child("username").setValue(newUsername)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    AlertHelper.showSuccessDialog(requireActivity(), "Success", "Username updated successfully!") {}
                                } else {
                                    AlertHelper.showErrorDialog(requireActivity(), "Error", "Failed to update username!") {}
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        AlertHelper.showErrorDialog(requireActivity(), "Error", "Re-authentication failed!") {}
                    }
                }
                .addOnFailureListener { exception ->
                    AlertHelper.showErrorDialog(requireActivity(), "Error", "Re-authentication error: ${exception.message}"){}
                }
        } else {
            AlertHelper.showErrorDialog(requireActivity(), "Error", "User is not authenticated!") {}
        }
    }

}
