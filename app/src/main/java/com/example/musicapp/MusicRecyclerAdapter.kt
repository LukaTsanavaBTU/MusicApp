package com.example.musicapp

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.bumptech.glide.Glide

class MusicRecyclerAdapter(private val musicArray: ArrayList<MusicDataClass>) : RecyclerView.Adapter<MusicRecyclerAdapter.ViewHolder> () {

    private lateinit var mRecyclerView: RecyclerView

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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rvSongTitle.text = musicArray[position].musicName
        holder.rvSongUploader.text = musicArray[position].username
        Glide.with(holder.rvSongArt.context).load(musicArray[position].imagePath).centerCrop().into(holder.rvSongArt)
        holder.itemView.setOnClickListener{
            mRecyclerView.isEnabled = false
            mRecyclerView.rootView.findViewById<ImageView>(R.id.viewDisableLayout).visibility = View.VISIBLE
            object : YouTubeExtractor(holder.rvSongArt.context) {
                override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {
                    if (ytFiles != null) {
                        val itag = 251
                        val downloadUrl = ytFiles[itag].url
                        val transferArray = arrayOf(musicArray[holder.adapterPosition].username, musicArray[holder.adapterPosition].uid, downloadUrl, musicArray[holder.adapterPosition].imagePath, musicArray[holder.adapterPosition].musicName)
                        val action = HomeFragmentDirections.actionHomeFragmentToMusicItemFragment(transferArray)
                        Navigation.findNavController(holder.itemView).navigate(action)
                    }
                }
            }.extract(musicArray[position].musicPath)
        }

    }

    override fun getItemCount(): Int = musicArray.size


}