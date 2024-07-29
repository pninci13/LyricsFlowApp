package com.example.lyricsflowapp.ui.helpers

import com.google.firebase.database.*
import com.example.lyricsflowapp.ui.fragments.history.SearchHistory

class FirebaseRepository {
    private val database = FirebaseDatabase.getInstance().reference

    fun insertHistory(userId: String, searchQuery: String, topMatch: String) {
        val searchId = database.child("userHistory").child(userId).child("searchHistory").push().key
        val history = SearchHistory(searchQuery, topMatch)
        if (searchId != null) {
            database.child("userHistory").child(userId).child("searchHistory").child(searchId).setValue(history)
        }
    }

    fun getHistory(userId: String, callback: (List<SearchHistory>) -> Unit) {
        database.child("userHistory").child(userId).child("searchHistory")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val historyList = mutableListOf<SearchHistory>()
                    for (data in snapshot.children) {
                        val history = data.getValue(SearchHistory::class.java)
                        if (history != null) {
                            historyList.add(history)
                        }
                    }
                    callback(historyList)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    fun clearHistory(userId: String) {
        database.child("userHistory").child(userId).child("searchHistory").removeValue()
    }

    fun deleteHistory(userId: String, searchQuery: String) {
        database.child("userHistory").child(userId).child("searchHistory")
            .orderByChild("searchQuery").equalTo(searchQuery)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        data.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}
