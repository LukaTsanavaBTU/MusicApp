package com.example.musicapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ViewPagerRecyclerAdapter(private val musicArray: ArrayList<MusicDataClass>) : RecyclerView.Adapter<ViewPagerRecyclerAdapter.ViewHolder> () {

    private val storageRef = Firebase.storage.reference

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vpCover: ImageView = view.findViewById(R.id.vpCover)
        val vpTitle: TextView = view.findViewById(R.id.vpTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewpager_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.vpTitle.text = musicArray[position].musicName

        storageRef.child(musicArray[position].imagePath).downloadUrl.addOnSuccessListener {
            Glide.with(holder.vpCover.context).load(it).into(holder.vpCover)
        }

        holder.itemView.setOnClickListener{
            storageRef.child(musicArray[position].musicPath).downloadUrl.addOnSuccessListener {
                val transferArray = arrayOf(musicArray[position].username, musicArray[position].uid, musicArray[position].musicPath, musicArray[position].imagePath, musicArray[position].musicName, it.toString())
                val action = ProfileFragmentDirections.actionProfileFragmentToMusicItemFragment(transferArray)
                Navigation.findNavController(holder.itemView).navigate(action)
            }

        }
    }

    override fun getItemCount(): Int = musicArray.size

}