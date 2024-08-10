package com.example.lyricsflowapp.ui.fragments.settings

data class BugReport(
    val id: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

