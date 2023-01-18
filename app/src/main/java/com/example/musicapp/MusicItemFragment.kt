package com.example.musicapp

import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.musicapp.databinding.FragmentMusicItemBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MusicItemFragment : Fragment(R.layout.fragment_music_item) {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable: Runnable
    private var handler = Handler(Looper.getMainLooper())

    private lateinit var binding: FragmentMusicItemBinding
    private val args: MusicItemFragmentArgs  by navArgs()
    private val storageRef = Firebase.storage.reference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMusicItemBinding.bind(view)

        val username = args.musicArrayTransfer[0]
        val uid = args.musicArrayTransfer[1]
        val musicPath = args.musicArrayTransfer[2]
        val imagePath = args.musicArrayTransfer[3]
        val musicName = args.musicArrayTransfer[4]
        val musicUri = args.musicArrayTransfer[5].toUri()


        val textViewUsername: TextView = view.findViewById(R.id.textViewUsername)
        val seekbar: SeekBar = view.findViewById(R.id.seekbar)
        val playButton: Button = view.findViewById(R.id.play)
        val name: TextView = view.findViewById(R.id.textView)
        val photo: ImageView = view.findViewById(R.id.imageView)
        val playedTime: TextView = view.findViewById(R.id.playedTime)
        val remainingTime: TextView = view.findViewById(R.id.leftTime)
        val preButton5: Button = view.findViewById(R.id.previous5)
        val nxtButton5: Button = view.findViewById(R.id.next5)
        val preButton10: Button = view.findViewById(R.id.previous10)
        val nxtButton10: Button = view.findViewById(R.id.next10)
        val volumeSeekBar: SeekBar = view.findViewById(R.id.seekBar)
        val highVolumeImage: ImageView = view.findViewById(R.id.imageView3)


        storageRef.child(imagePath).downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(photo)
        }

        name.text = musicName
        textViewUsername.text = username

            mediaPlayer = MediaPlayer.create(activity, musicUri)

            seekbar.progress = 0
            seekbar.max = mediaPlayer.duration

            preButton5.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition - 5000)
            }
            nxtButton5.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition + 5000)
            }
            preButton10.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition - 10000)
            }
            nxtButton10.setOnClickListener {
                mediaPlayer.seekTo(mediaPlayer.currentPosition + 10000)
            }

            playButton.setOnClickListener {
                if (!mediaPlayer.isPlaying){
                    mediaPlayer.start()
                    playButton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_filled_24)
                }
                else{
                    mediaPlayer.pause()
                    playButton.setBackgroundResource(R.drawable.ic_baseline_play_circle_filled_24)
                }
            }


            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2){
                        mediaPlayer.seekTo(p1)

                        val p1S = p1 / 1000
                        val p1Min = (p1S / 60)
                        val p1Sec = (p1S % 60)

                        playedTime.text = "${p1Min}:${p1Sec}"
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }

            })

            runnable = Runnable {

                seekbar.progress = mediaPlayer.currentPosition
                handler.postDelayed(runnable,0)

                if (volumeSeekBar.progress > volumeSeekBar.max / 2){
                    highVolumeImage.setBackgroundResource(R.drawable.ic_baseline_volume_up_24)
                }
                else if (volumeSeekBar.progress <= volumeSeekBar.max / 2 && volumeSeekBar.progress > 0){
                    highVolumeImage.setImageDrawable(null)
                    highVolumeImage.setBackgroundResource(R.drawable.ic_baseline_volume_down_24)
                }
                else if (volumeSeekBar.progress == 0){
                    highVolumeImage.setBackgroundResource(R.drawable.ic_baseline_volume_off_24)
                }


                val remTimeSec = ((mediaPlayer.duration / 1000) - (mediaPlayer.currentPosition / 1000))

                playedTime.text = (((mediaPlayer.currentPosition / 1000) - ((mediaPlayer.currentPosition / 1000) % 60)) / 60).toString() + ":" + ((mediaPlayer.currentPosition / 1000) % 60).toString()
                remainingTime.text = "-" + ((remTimeSec - (remTimeSec % 60)) / 60).toString() + ":" + (((mediaPlayer.duration / 1000) - (mediaPlayer.currentPosition / 1000)) % 60).toString()

            }
            handler.postDelayed(runnable,0)

            mediaPlayer.setOnCompletionListener {

                mediaPlayer.start()
                playButton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_filled_24)

            }




        val audioManager = requireActivity().getSystemService(AUDIO_SERVICE) as AudioManager

        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.max = maxVolume

        val currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.progress = currVolume

        volumeSeekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, p1,0)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }
}