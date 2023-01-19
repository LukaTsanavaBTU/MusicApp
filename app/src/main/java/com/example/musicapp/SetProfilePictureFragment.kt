package com.example.musicapp

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.musicapp.databinding.FragmentSetProfilePictureBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class SetProfilePictureFragment : Fragment(R.layout.fragment_set_profile_picture) {

    private lateinit var binding: FragmentSetProfilePictureBinding
    private lateinit var auth: FirebaseAuth

    private var imageSet = false

    private var getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.ivUserProfilePicture)

            Glide.with(this)
                .load(uri)
                .into(binding.ivUserProfilePictureHidden)}
        imageSet = true }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetProfilePictureBinding.bind(view)

        auth = Firebase.auth
        val database = Firebase.database("https://musicapp-f92ec-default-rtdb.firebaseio.com/")
        val myRef = database.reference
        var storageRef = Firebase.storage.reference



        //make placeholder image and draw it onto imageview !!!

        Glide.with(this)
            .load(R.drawable.defaultprofile)
            .circleCrop()
            .into(binding.ivUserProfilePicture)

        Glide.with(this)
            .load(R.drawable.defaultprofile)
            .into(binding.ivUserProfilePictureHidden)


        binding.btUploadPicture.setOnClickListener {
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

        // set up cloud storage authentication rules


        binding.btConfirm.setOnClickListener {
            if (binding.ivUserProfilePicture.drawable != null && imageSet) {
                val imageRef = storageRef.child("images/${auth.uid}.jpg")
                val bitmap = getBitmapFromView(binding.ivUserProfilePictureHidden)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    Toast.makeText(activity, "An Error Has Occurred, Try a Different Image (CUSTOM TOAST)", Toast.LENGTH_SHORT).show()
                }
                uploadTask.addOnSuccessListener {
                    myRef.child("users").child(auth.uid.toString()).child("profilePhoto").setValue("images/${auth.uid}.jpg")
                    val intent = Intent(activity, ApplicationActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            } else { Toast.makeText(activity, "Select An Image (CUSTOM TOAST)", Toast.LENGTH_SHORT).show() }
        }

        binding.tvSkip.setOnClickListener {

            Glide.with(this)
                .load(R.drawable.defaultprofile)
                .into(binding.ivUserProfilePictureHidden)

            val imageRef = storageRef.child("images/${auth.uid}.jpg")
            val bitmap = getBitmapFromView(binding.ivUserProfilePictureHidden)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = imageRef.putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(activity, "Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
            uploadTask.addOnSuccessListener {
                myRef.child("users").child(auth.uid.toString()).child("profilePhoto").setValue("images/${auth.uid}.jpg")
                val intent = Intent(activity, ApplicationActivity::class.java)
                startActivity(intent)
                activity?.finish()
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
