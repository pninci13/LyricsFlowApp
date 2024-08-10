package com.example.lyricsflowapp.ui.fragments.settings

import android.app.Dialog
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
import com.google.firebase.database.FirebaseDatabase

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back to home page
        binding.goBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_homeFragment)
        }

        // Report bug
        binding.btnReportBug.setOnClickListener {
            showReportBugDialog()
        }
    }

    private fun showReportBugDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_report_bug)

        val titleTextView: TextView = dialog.findViewById(R.id.alertDialogMessage)
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
}
