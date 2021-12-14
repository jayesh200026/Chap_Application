package com.example.chatapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory

class SplashFragment : Fragment() {
    lateinit var logo: ImageView
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        logo = view.findViewById(R.id.splashImage)
        logo.alpha = 0f
        logo.animate().setDuration(1500).alpha(1f).withEndAction {
            if(sharedViewModel.getCurrentUser() != null){
                sharedViewModel.setGotoHomePageStatus(true)
            }
            else{
                sharedViewModel.setGotoRequestOtpStatus(true)
            }
        }
        return  view
    }
}