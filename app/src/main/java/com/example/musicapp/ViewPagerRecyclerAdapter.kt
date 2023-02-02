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

class ViewPagerRecyclerAdapter(private val musicArray: ArrayList<MusicDataClass>) : RecyclerView.Adapter<ViewPagerRecyclerAdapter.ViewHolder> () {

    private lateinit var mRecyclerView: RecyclerView

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vpCover: ImageView = view.findViewById(R.id.vpCover)
        val vpTitle: TextView = view.findViewById(R.id.vpTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewpager_item, parent, false)

        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.vpTitle.text = musicArray[position].musicName
        Glide.with(holder.vpCover.context).load(musicArray[position].imagePath).centerCrop().into(holder.vpCover)
        holder.itemView.setOnClickListener{
            holder.itemView.isClickable = false
            mRecyclerView.rootView.findViewById<ImageView>(R.id.viewDisableLayout).visibility = View.VISIBLE
            object : YouTubeExtractor(holder.vpCover.context) {
                override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {
                    if (ytFiles != null) {
                        val itag = 251
                        val downloadUrl = ytFiles[itag].url
                        val transferArray = arrayOf(musicArray[holder.adapterPosition].username, musicArray[holder.adapterPosition].uid, downloadUrl, musicArray[holder.adapterPosition].imagePath, musicArray[holder.adapterPosition].musicName)
                        val action = ProfileFragmentDirections.actionProfileFragmentToMusicItemFragment(transferArray)
                        Navigation.findNavController(holder.itemView).navigate(action)
                    }
                }
            }.extract(musicArray[position].musicPath)
        }


    }

    override fun getItemCount(): Int = musicArray.size

}