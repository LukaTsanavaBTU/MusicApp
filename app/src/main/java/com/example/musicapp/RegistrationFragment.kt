package com.example.musicapp

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.musicapp.databinding.FragmentRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegistrationBinding.bind(view)

        auth = Firebase.auth

        val database = Firebase.database("https://musicapp-f92ec-default-rtdb.firebaseio.com/")
        val myRef = database.reference

        binding.btRegister2.setOnClickListener {
            if (binding.etUsername.text.toString().length < 3) {
                binding.etUsername.error = "The Username Has To Be Longer Than 3 Characters"
            } else if (binding.etEmail2.text.toString().length < 6 || !binding.etEmail2.text.toString().isValidEmail()) {
                binding.etEmail2.error = "The Email Has To Be Longer Than 6 Characters And Has To Be In The Correct Format"
            } else if (binding.etPassword2.text.toString().length < 6) {
                binding.etPassword2.error = "The Password Has To Be Longer Than 6 Characters"
            } else if (binding.etConfirmPassword.text.toString() != binding.etPassword2.text.toString()) {
                binding.etConfirmPassword.error = "The Passwords Do Not Match"
            } else {
                    auth.createUserWithEmailAndPassword(binding.etEmail2.text.toString(), binding.etPassword2.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val newUser = UserInfoDataClass(binding.etUsername.text.toString(), binding.etEmail2.text.toString(), auth.uid.toString(), "images/default.jpg")
                                myRef.child("users").child(auth.uid.toString()).setValue(newUser)
                                Navigation.findNavController(view).navigate(R.id.action_registrationFragment_to_setProfilePictureFragment)
                            } else { Toast.makeText(activity, "This User Already Exists", Toast.LENGTH_SHORT).show() }
                        }
                }
        }
    }

    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

}