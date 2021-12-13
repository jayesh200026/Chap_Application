package com.example.chatapp

import android.app.Application
import com.example.chatapp.service.FirebaseMessaging
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging().getToken {
            SharedPref.addString(Constants.DEVICE_TOKEN, it)
        }
    }
}