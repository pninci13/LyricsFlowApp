package com.example.lyricsflowapp.ui.fragments.spotify

import android.content.Context
import android.content.Intent
import android.net.Uri

class SpotifyAppHandler {

    fun openSpotifyApp(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.setClassName("com.spotify.music", "com.spotify.music.MainActivity")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            openSpotifyInPlayStore(context)
        }
    }

    private fun openSpotifyInPlayStore(context: Context) {
        val playStoreIntent = Intent(Intent.ACTION_VIEW)
        playStoreIntent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music")
        playStoreIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(playStoreIntent)
    }
}
