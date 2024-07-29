package com.example.lyricsflowapp.ui.fragments.history

data class SearchHistory(
    val searchQuery: String? = "",
    val topMatch: String? = ""
) {
    // No-argument constructor required for Firebase deserialization
    constructor() : this("", "")
}
