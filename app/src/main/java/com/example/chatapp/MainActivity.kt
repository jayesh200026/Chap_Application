package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.ui.profile.AddUserDetailFragment
import com.example.chatapp.ui.home.HomeFragment
import com.example.chatapp.ui.login.RequestOTPFragment
import com.example.chatapp.ui.login.VerifyOTPFragment
import com.example.chatapp.ui.profile.EditProfileFragment
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SharedPref.initSharedPref(this)
        val loginStatus = intent.getStringExtra(Constants.LOGIN)
        sharedViewModel = ViewModelProvider(
            this@MainActivity,
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        observe()
        if (FirebaseAuth.getInstance().currentUser != null) {
            gotoHomePage()
        } else {
            gotoRequestOtp()
        }
    }

    private fun gotoRequestOtp() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, RequestOTPFragment())
            commit()
        }
    }

    private fun observe() {
        sharedViewModel.gotoSetProfilePageStatus.observe(this, {
            if (it) {
                gotoSetProfilePage()
            }
        })
        sharedViewModel.gotoHomePageStatus.observe(this, {
            if (it) {
                gotoHomePage()
            }
        })
        sharedViewModel.gotoSetVerifiOtpPageStatus.observe(this, {
            if (it) {
                gotoVerifyOtp()
            }
        })
        sharedViewModel.gotoRequestOtpPageStatus.observe(this, {
            if (it) {
                gotoRequestOtp()
            }
        })
        sharedViewModel.gotoEditProfilePageStatus.observe(this, {
            if (it) {
                gotoEditProfilePage()
            }
        })
    }

    private fun gotoEditProfilePage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, EditProfileFragment())
            addToBackStack(null)
            commit()
        }
    }

    private fun gotoVerifyOtp() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, VerifyOTPFragment())
            commit()
        }
    }

    private fun gotoSetProfilePage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, AddUserDetailFragment())
            commit()
        }
    }

    private fun gotoHomePage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, HomeFragment())
            commit()
        }
    }
}