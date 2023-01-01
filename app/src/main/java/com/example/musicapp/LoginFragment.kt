package com.example.musicapp

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.musicapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        auth = Firebase.auth

        val sharedPreferences = activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val rememberMeStatus = sharedPreferences!!.getBoolean("RememberMe", false)

        binding.btLogin.setOnClickListener {
            if (binding.etEmail.text.toString().length < 6 || !binding.etEmail.text.toString().isValidEmail()) {
                binding.etEmail.error = "The Email Has To Be Longer Than 6 Characters And Has To Be In The Correct Format"
            }
            else if (binding.etPassword.text.toString().length < 6 ) {
                binding.etPassword.error = "The Password Has To Be Longer Than 6 Characters"
            } else {
                auth.signInWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            editor?.apply{ putBoolean("RememberMe", binding.cbRememberMe.isChecked) }?.apply()
                            Toast.makeText(activity, "Move To App, remember me: ${binding.cbRememberMe.isChecked}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "Incorrect Credentials (CUSTOM TOAST)", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.tvRegister.setOnClickListener {
        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        binding.tvRecoverPassword.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_recoverPasswordFragment)
        }

    }

    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

}