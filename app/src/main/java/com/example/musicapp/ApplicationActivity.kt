package com.example.musicapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ApplicationActivity : AppCompatActivity() {

    //TEMP TEMP TEMP
    private lateinit var auth: FirebaseAuth
    //TEMP TEMP TEMP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application)

        //TEMP TEMP TEMP
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        auth = Firebase.auth
        val btSignOutTemp = findViewById<Button>(R.id.btSignOutTemp)

        btSignOutTemp.setOnClickListener {
            editor.apply { putBoolean("RememberMe", false) }.apply()
            auth.signOut()
            finish()
        }

        //TEMP TEMP TEMP

    }
}