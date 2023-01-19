package com.example.musicapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.databinding.FragmentHomeBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        val database = Firebase.database("https://musicapp-f92ec-default-rtdb.firebaseio.com/")
        val myRef = database.reference

        val musicArray = arrayListOf<MusicDataClass>()
        myRef.child("music").addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val musicPage = snapshot.getValue(MusicDataClass::class.java)
                if (musicPage != null){
                    musicArray.add(0, MusicDataClass(musicPage.username, musicPage.uid, musicPage.musicPath, musicPage.imagePath, musicPage.musicName))
                }
                binding.homeRecyclerView.layoutManager = LinearLayoutManager(activity)
                binding.homeRecyclerView.adapter = MusicRecyclerAdapter(musicArray)

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