package com.example.lyricsflowapp.ui.fragments.settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.FragmentSettingsBinding
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.lyricsflowapp.ui.helpers.AlertHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth

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
    }

    private fun showReportBugDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_report_bug)

        val bugDescriptionEditText: EditText = dialog.findViewById(R.id.bugDescriptionEditText)
        val sendButton: Button = dialog.findViewById(R.id.sendButton)

        bugDescriptionEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bugDescriptionEditText.hint = ""
            } else {
                if (bugDescriptionEditText.text.isEmpty()) {
                    bugDescriptionEditText.hint = "Type Here"
                }
            }
        }

        sendButton.setOnClickListener {
            val bugDescription = bugDescriptionEditText.text.toString()
            if (bugDescription.isNotEmpty()) {
                saveBugReportToFirebase(bugDescription)
            }
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

    private fun saveBugReportToFirebase(description: String) {
        val database = FirebaseDatabase.getInstance()
        val bugReportsRef = database.getReference("bugReports")

        val bugReportId = bugReportsRef.push().key
        if (bugReportId != null) {
            val bugReport = BugReport(id = bugReportId, description = description)
            bugReportsRef.child(bugReportId).setValue(bugReport)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Bug report submitted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to submit bug report",
                            Toast.LENGTH_SHORT
                        ).show()
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

            // Delete user data
            userRef.removeValue()
                .addOnSuccessListener {
                    // Optionally, delete the user's authentication record
                    FirebaseAuth.getInstance().currentUser?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign out the user and navigate away
                                FirebaseAuth.getInstance().signOut()
                                // Navigate to login or appropriate screen
                            } else {
                                // Handle the error, if any
                            }
                        }
                }
                .addOnFailureListener {
                    // Handle the error
                }
        }
    }

    private fun userLogout() {
        val sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        auth.signOut()
        findNavController().navigate(R.id.action_settingsFragment_to_authentication)
    }


}
