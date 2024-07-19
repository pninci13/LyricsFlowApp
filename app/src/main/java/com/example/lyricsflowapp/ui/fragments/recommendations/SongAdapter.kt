package com.example.lyricsflowapp.ui.fragments.recommendations

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lyricsflowapp.R
import com.example.lyricsflowapp.databinding.ItemSongBinding

class SongAdapter(private val songs: List<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.tvTitle.text = song.title
            binding.tvArtist.text = song.artist
            binding.tvExplanation.text = song.explanation
            binding.tvScore.text = String.format("%.2f%%", song.score * 100)
            Glide.with(binding.ivSongImage.context)
                .load(song.imageUrl)
                .placeholder(R.drawable.music_placeholder)  // Add a placeholder image in your drawable folder
                .into(binding.ivSongImage)
            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(song.songUrl))
                binding.root.context.startActivity(intent)
            }
        }
    }
}
