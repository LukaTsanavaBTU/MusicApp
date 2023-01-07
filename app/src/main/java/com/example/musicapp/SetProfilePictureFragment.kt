package com.example.musicapp

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.musicapp.databinding.FragmentSetProfilePictureBinding


class SetProfilePictureFragment : Fragment(R.layout.fragment_set_profile_picture) {

    private lateinit var binding: FragmentSetProfilePictureBinding

    private var getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.ivUserProfilePicture)} }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetProfilePictureBinding.bind(view)

        //make placeholder image and draw it onto imageview !!!

        Glide.with(this)
            .load("https://images.squarespace-cdn.com/content/v1/54642373e4b024e8934bf4f4/1436320009300-4QNN2TPZ0SPJFEN1F662/No-Appointment-Necessary.jpg?format=500w")
            .circleCrop()
            .into(binding.ivUserProfilePicture)

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

        // when user presses confirm make sure the picture has been chosen, upload to firebase storage,
        // set image url as user image url and proceed to app activity

        // if user presses skip proceed to app activity (every user should already have generic profile pic
        // url set after registration)
        }
    }
