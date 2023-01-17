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
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.musicapp.databinding.FragmentChangeProfilePictureBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class ChangeProfilePictureFragment : Fragment(R.layout.fragment_change_profile_picture) {

    private lateinit var binding: FragmentChangeProfilePictureBinding
    private lateinit var auth: FirebaseAuth

    private var imageSet = false

    private var getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.ivUserProfilePictureCg)

            Glide.with(this)
                .load(uri)
                .into(binding.ivUserProfilePictureHiddenCg)}
        imageSet = true }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChangeProfilePictureBinding.bind(view)

        auth = Firebase.auth
        val database = Firebase.database("https://musicapp-9c0d8-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.reference
        var storageRef = Firebase.storage.reference


//        val profilePicListener = myRef.child("users").child(auth.uid.toString())
//            .child("profilePhoto").addValueEventListener(object: ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val defaultProfilePhoto = snapshot.value.toString()
//                    Toast.makeText(activity, defaultProfilePhoto.toString(), Toast.LENGTH_SHORT).show()
//
//
                    Glide.with(requireParentFragment())
                        .load(myRef.child("images/${auth.currentUser?.uid}").get())
                        .circleCrop()
                        .into(binding.ivUserProfilePictureCg)

                    Glide.with(requireParentFragment())
                        .load(myRef.child("images/${auth.currentUser?.uid}").get())
                        .into(binding.ivUserProfilePictureHiddenCg)
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//            })


        binding.btUploadPictureCg.setOnClickListener {
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


        binding.btConfirmCg.setOnClickListener {
            if (binding.ivUserProfilePictureCg.drawable != null && imageSet) {
                val imageRef = storageRef.child("images/${auth.uid}.jpg")
                val bitmap = getBitmapFromView(binding.ivUserProfilePictureHiddenCg)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    Toast.makeText(activity, "An Error Has Occurred, Try a Different Image (CUSTOM TOAST)", Toast.LENGTH_SHORT).show()
                }
                uploadTask.addOnSuccessListener {
                    myRef.child("users").child(auth.uid.toString()).child("profilePhoto").setValue("images/${auth.uid}.jpg")
                    Toast.makeText(activity, "Successfully Changed Image (CUSTOM TOAST)", Toast.LENGTH_SHORT).show()
                    activity?.onBackPressed()
                }
            } else { Toast.makeText(activity, "Select An Image (CUSTOM TOAST)", Toast.LENGTH_SHORT).show() }
        }

        binding.tvCancel.setOnClickListener {
            activity?.onBackPressed()
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