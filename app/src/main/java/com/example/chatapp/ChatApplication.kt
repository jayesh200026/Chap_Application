package com.example.chatapp

import android.app.Application
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            val message = token.toString()
            SharedPref.initSharedPref(this)
            SharedPref.addString(Constants.MESSAGE_TOKEN, message)

        })
    }
}