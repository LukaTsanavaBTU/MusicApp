package com.example.musicapp

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MusicPlayerPageAdapter(private val musicArray: ArrayList<MusicDataClass>) : RecyclerView.Adapter<MusicPlayerPageAdapter.ViewHolder>() {

    private val storageRef = Firebase.storage.reference
    private lateinit var runnable: Runnable
    private var handler = Handler(Looper.getMainLooper())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val textView: TextView = view.findViewById(R.id.textView)
        val textViewUsername: TextView = view.findViewById(R.id.textViewUsername)
        val playButton: Button = view.findViewById(R.id.play)
        val seekbar: SeekBar = view.findViewById(R.id.seekbar)
        val volumeSeekBar: SeekBar = view.findViewById(R.id.seekBar)
        val preButton5: Button = view.findViewById(R.id.previous5)
        val nxtButton5: Button = view.findViewById(R.id.next5)
        val preButton10: Button = view.findViewById(R.id.previous10)
        val nxtButton10: Button = view.findViewById(R.id.next10)
        val playedTime: TextView = view.findViewById(R.id.playedTime)
        val remainingTime: TextView = view.findViewById(R.id.leftTime)
        val highVolumeImage: ImageView = view.findViewById(R.id.imageView3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        storageRef.child(musicArray[position].musicPath).downloadUrl.addOnSuccessListener {
            val mediaPlayer = MediaPlayer.create(holder.imageView.context,it)
            holder.seekbar.progress = 0
            holder.seekbar.max = mediaPlayer.duration

            holder.preButton5.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition - 5000)
            }
            holder.nxtButton5.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition + 5000)
            }
            holder.preButton10.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition - 10000)
            }
            holder.nxtButton10.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition + 10000)
            }

            holder.playButton.setOnClickListener {
                if (!mediaPlayer.isPlaying){
                    mediaPlayer.start()
                    holder.playButton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_filled_24)
                }
                else{
                    mediaPlayer.pause()
                    holder.playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24)
                }
            }

            holder.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2){
                        mediaPlayer.seekTo(p1)

                        val p1S = p1 / 1000
                        val p1Min = (p1S / 60)
                        val p1Sec = (p1S % 60)

                        holder.playedTime.text = "${p1Min}:${p1Sec}"
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }

            })

            runnable = Runnable {

                holder.seekbar.progress = mediaPlayer.currentPosition
                handler.postDelayed(runnable,0)


                val am = holder.imageView.context.getSystemService(AUDIO_SERVICE) as AudioManager?
                val volumeLevel = am!!.getStreamVolume(AudioManager.STREAM_MUSIC)
                holder.volumeSeekBar.progress = volumeLevel

                if (holder.volumeSeekBar.progress > holder.volumeSeekBar.max / 2){
                    holder.highVolumeImage.setBackgroundResource(R.drawable.ic_baseline_volume_up_24)
                }
                else if (holder.volumeSeekBar.progress <= holder.volumeSeekBar.max / 2 && holder.volumeSeekBar.progress > 0){
                    holder.highVolumeImage.setImageDrawable(null)
                    holder.highVolumeImage.setBackgroundResource(R.drawable.ic_baseline_volume_down_24)
                }
                else if (holder.volumeSeekBar.progress == 0){
                    holder.highVolumeImage.setBackgroundResource(R.drawable.ic_baseline_volume_off_24)
                }


                val remTimeSec = ((mediaPlayer.duration / 1000) - (mediaPlayer.currentPosition / 1000))

                holder.playedTime.text = (((mediaPlayer.currentPosition / 1000) - ((mediaPlayer.currentPosition / 1000) % 60)) / 60).toString() + ":" + ((mediaPlayer.currentPosition / 1000) % 60).toString()
                holder.remainingTime.text = "-" + ((remTimeSec - (remTimeSec % 60)) / 60).toString() + ":" + (((mediaPlayer.duration / 1000) - (mediaPlayer.currentPosition / 1000)) % 60).toString()

            }
            handler.postDelayed(runnable,0)

            mediaPlayer.setOnCompletionListener {

                mediaPlayer.start()
                holder.playButton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_filled_24)

    }


        }
        storageRef.child(musicArray[position].imagePath).downloadUrl.addOnSuccessListener {
            Glide.with(holder.imageView.context)
                .load(it)
                .into(holder.imageView)
        }
        holder.textView.isSingleLine = true
        holder.textView.isSelected = true
        holder.textView.text = musicArray[position].musicName
        holder.textViewUsername.text = musicArray[position].username

        val audioManager = holder.imageView.context.getSystemService(AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        holder.volumeSeekBar.max = maxVolume
        val currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        holder.volumeSeekBar.progress = currVolume
        holder.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, p1,0)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })



    }

    override fun getItemCount(): Int = musicArray.size

}
