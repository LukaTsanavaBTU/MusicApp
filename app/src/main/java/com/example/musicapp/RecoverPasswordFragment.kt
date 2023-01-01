package com.example.musicapp

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicapp.databinding.FragmentRecoverPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverPasswordFragment : Fragment(R.layout.fragment_recover_password) {

    private lateinit var binding: FragmentRecoverPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecoverPasswordBinding.bind(view)

        auth = Firebase.auth

        binding.btRecoverPassword2.setOnClickListener {
            if (binding.etEmail3.text.toString().length < 6 || !binding.etEmail3.text.toString().isValidEmail()) {
                binding.etEmail3.error = "The Email Has To Be Longer Than 6 Characters And Has To Be In The Correct Format"
            } else {
                Firebase.auth.sendPasswordResetEmail(binding.etEmail3.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, "The Email Has Been Sent CUSTOM TOAST", Toast.LENGTH_SHORT).show()
                            binding.etEmail3.text.clear()
                        }
                    }
            }
        }

    }

    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

}