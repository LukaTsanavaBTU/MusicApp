package com.example.musicapp

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.musicapp.databinding.FragmentUploadMusicBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class UploadMusicFragment : Fragment(R.layout.fragment_upload_music) {

    private lateinit var binding: FragmentUploadMusicBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadMusicBinding.bind(view)

        auth = Firebase.auth
        val database = Firebase.database("https://musicapp-f92ec-default-rtdb.firebaseio.com/")
        val myRef = database.reference

        fun uploadMusic(id: String) {
            binding.btUpload.isEnabled = false
            myRef.child("users").child(auth.currentUser?.uid.toString()).child("username")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.value.toString()
                        val musicItem = MusicDataClass(
                            username,
                            auth.currentUser?.uid.toString(),
                            "https://www.youtube.com/watch?v=${id}",
                            "https://img.youtube.com/vi/${id}/maxresdefault.jpg",
                            binding.etMusicName.text.toString()
                        )
                        myRef.child("users").child(auth.currentUser?.uid.toString())
                            .child("uploadedMusic").push().setValue(musicItem)
                        if (!binding.cbPrivate.isChecked) {
                            myRef.child("music").push().setValue(musicItem)
                        }
                        Toast.makeText(activity, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                        binding.btUpload.isEnabled = true
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }

        binding.btCheckLink.setOnClickListener {
            val link = binding.etMusicLink.text.toString()
            if (URLUtil.isValidUrl(link)) {
                if (link.contains("youtu.be")) {
                    val id = link.split("e/")[1]
                    Glide.with(this).load("https://img.youtube.com/vi/${id}/maxresdefault.jpg")
                        .optionalCenterCrop().into(binding.ivCoverArt)
                } else if (link.contains("youtube.com")) {
                    var id = link.split("watch?v=")[1].split("&list")[0]
                    Glide.with(this).load("https://img.youtube.com/vi/${id}/maxresdefault.jpg")
                        .optionalCenterCrop().into(binding.ivCoverArt)
                } else {
                    Toast.makeText(activity, "Not A Valid Youtube Link", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Not A Valid Youtube Link", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btUpload.setOnClickListener {
            if (binding.etMusicName.text.toString().length < 4) {
                binding.etMusicName.error = "Music Name Too Short"
            } else {
            val link = binding.etMusicLink.text.toString()
            if (URLUtil.isValidUrl(link)) {
                if (link.contains("youtu.be")) {
                    val id = link.split("e/")[1]
                    uploadMusic(id)
                } else if (link.contains("youtube.com")) {
                    var id = link.split("watch?v=")[1].split("&list")[0]
                    uploadMusic(id)
                } else {
                    Toast.makeText(activity, "Not A Valid Youtube Link", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Not A Valid Youtube Link", Toast.LENGTH_SHORT).show()
            }
        }
        }
    }

}