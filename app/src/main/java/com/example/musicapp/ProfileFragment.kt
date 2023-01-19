package com.example.musicapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.musicapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        val database = Firebase.database("https://musicapp-f92ec-default-rtdb.firebaseio.com/")
        val myRef = database.reference
        auth = Firebase.auth

        binding.btUploadMusic.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.uploadMusicFragment)
        }

        val musicArray = arrayListOf<MusicDataClass>()
        myRef.child("users").child(auth.currentUser?.uid.toString()).child("uploadedMusic").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val musicPage = snapshot.getValue(MusicDataClass::class.java)
                if (musicPage != null){
                    musicArray.add(0, MusicDataClass(musicPage.username, musicPage.uid, musicPage.musicPath, musicPage.imagePath, musicPage.musicName))
                }

                binding.viewPager2.adapter = ViewPagerRecyclerAdapter(musicArray)

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

}