package com.example.musicapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import com.example.musicapp.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        auth = Firebase.auth

        val sharedPreferences = activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()

        binding.btSignOut.setOnClickListener {
            editor?.apply { putBoolean("RememberMe", false) }?.apply()
            auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.btChangePassword.setOnClickListener {
            val changePasswordDialogFragment = ChangePasswordDialogFragment()
            changePasswordDialogFragment.show(childFragmentManager, "Change Password Dialog")
        }

        binding.btChangeProfilePicture.setOnClickListener {

        }


    }

}