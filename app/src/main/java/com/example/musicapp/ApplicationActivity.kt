package com.example.musicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapp.databinding.ActivityApplicationBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ApplicationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = findNavController(R.id.ncApplication)
        val bnmBottomNavMenu = findViewById<BottomNavigationView>(R.id.bnmBottomNavMenu)
        bnmBottomNavMenu.setupWithNavController(navHost)
    }
}