package com.example.chatapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {
    private lateinit var logout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        logout = view.findViewById(R.id.logout)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@HomeFragment.requireContext(),MainActivity::class.java)
            intent.putExtra(Constants.LOGIN,"false")
            startActivity(intent)
        }
        return view
    }
}