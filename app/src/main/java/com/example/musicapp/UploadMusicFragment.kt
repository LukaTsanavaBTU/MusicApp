package com.example.musicapp

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
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

    private var getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Glide.with(this)
                .load(uri)
                .into(binding.ivCoverArt)
        }
    }

    private var getMp3 = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.tvChosenMp3.text = uri.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadMusicBinding.bind(view)

        auth = Firebase.auth
        val database = Firebase.database("https://musicapp-f92ec-default-rtdb.firebaseio.com/")
        val myRef = database.reference
        val storageRef = Firebase.storage.reference

        binding.btChoosePicture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireActivity().applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                getImageFromGallery.launch("image/*")
            }
        }



        binding.btChooseFile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireActivity().applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                getMp3.launch("audio/*")
            }
        }

        binding.btUpload.setOnClickListener {
            if (binding.tvChosenMp3.text.toString() == "MP3 File Not Chosen"){
                Toast.makeText(activity, "Select An MP3 File (CUSTOM TOAST)", Toast.LENGTH_SHORT).show()
            } else if (binding.etMusicName.text.toString().length < 4) {
                binding.etMusicName.error = "Music Name Is Too Short"
            }
            else {
                val imageRef = storageRef.child("images/${binding.etMusicName.text.toString().replace("/", "")}.png")
                val bitmap = getBitmapFromView(binding.ivCoverArt)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()

                val uploadTaskImage = imageRef.putBytes(data)
                uploadTaskImage.addOnFailureListener {
                    Toast.makeText(activity, "An Error Has Occurred, Try a Different Image (CUSTOM TOAST)", Toast.LENGTH_SHORT).show()
                }
                uploadTaskImage.addOnSuccessListener {
                    val mp3Ref = storageRef.child("music/${binding.etMusicName.text.toString().replace("/", "")}.mp3")
                    val uploadTaskMp3 = mp3Ref.putFile(binding.tvChosenMp3.text.toString().toUri())
                    uploadTaskMp3.addOnFailureListener {
                        Toast.makeText(activity, "An Error Has Occurred, Try a Different MP3 File (CUSTOM TOAST)", Toast.LENGTH_SHORT).show()
                    }
                    uploadTaskMp3.addOnSuccessListener {
                        myRef.child("users").child("${auth.currentUser?.uid}").child("username").addValueEventListener ( object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val uploadedMusicData = MusicDataClass("${snapshot.value}",
                                    "${auth.currentUser?.uid}",
                                    "music/${binding.etMusicName.text.toString().replace("/", "")}.mp3",
                                    "images/${binding.etMusicName.text.toString().replace("/", "")}.png",
                                    "${binding.etMusicName.text}")
                                myRef.child("music").push().setValue(uploadedMusicData)
                                myRef.child("users").child(auth.currentUser?.uid.toString()).child("uploadedMusic").push().setValue(uploadedMusicData)
                                Toast.makeText(activity, "Uploaded Successfully (CUSTOM TOAST)", Toast.LENGTH_SHORT).show()
                                activity?.onBackPressed()
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }
                }
            }
            }
        }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}