package com.example.musicapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MusicRecyclerAdapter(private val musicArray: ArrayList<MusicDataClass>) : RecyclerView.Adapter<MusicRecyclerAdapter.ViewHolder> () {

    private val storageRef = Firebase.storage.reference

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val rvSongArt: ImageView = view.findViewById(R.id.rvSongArt)
        val rvSongUploader: TextView = view.findViewById(R.id.rvSongUploader)
        val rvSongTitle: TextView = view.findViewById(R.id.rvSongTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_viewer_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rvSongTitle.text = musicArray[position].musicName
        holder.rvSongUploader.text = musicArray[position].username
        storageRef.child(musicArray[position].imagePath).downloadUrl.addOnSuccessListener {
            Glide.with(holder.rvSongArt.context).load(it).into(holder.rvSongArt)
        }
        holder.itemView.setOnClickListener{
            storageRef.child(musicArray[position].musicPath).downloadUrl.addOnSuccessListener {
                val transferArray = arrayOf(musicArray[position].username, musicArray[position].uid, musicArray[position].musicPath, musicArray[position].imagePath, musicArray[position].musicName, it.toString())
                val action = HomeFragmentDirections.actionHomeFragmentToMusicItemFragment(transferArray)
                Navigation.findNavController(holder.itemView).navigate(action)
            }

        }

    }

    override fun getItemCount(): Int = musicArray.size


}