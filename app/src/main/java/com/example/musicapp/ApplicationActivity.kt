package com.example.musicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapp.databinding.ActivityApplicationBinding

class ApplicationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragments = arrayListOf(HomeFragment(), ProfileFragment(), SettingsFragment())
        binding.viewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3
            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        val myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bnmBottomNavMenu.selectedItemId = when(position) {
                    0 -> R.id.homeFragment
                    1 -> R.id.profileFragment
                    else -> R.id.settingsFragment
                }
            }
        }

        binding.viewPager2.registerOnPageChangeCallback(myPageChangeCallback)

        binding.bnmBottomNavMenu.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> {
                    binding.viewPager2.currentItem = 0
                    true
                }
                R.id.profileFragment -> {
                    binding.viewPager2.currentItem = 1
                    true
                }
                R.id.settingsFragment -> {
                    binding.viewPager2.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }
}