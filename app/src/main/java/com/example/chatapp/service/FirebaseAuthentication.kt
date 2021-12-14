package com.example.chatapp.service

import android.util.Log
import com.example.chatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import kotlin.coroutines.suspendCoroutine

object FirebaseAuthentication {
    suspend fun signInWithCredential(credential: PhoneAuthCredential): String {
        return suspendCoroutine { cont ->
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result!!.additionalUserInfo!!.isNewUser) {
                            cont.resumeWith(Result.success(Constants.NEW_USER))
                        } else {
                            Log.d("login", "existing user")
                            cont.resumeWith(Result.success(Constants.EXISTING_USER))
                        }
                    } else {
                        cont.resumeWith(Result.failure(it.exception as Exception))
                    }
                }
        }
    }

    suspend fun logout(): Boolean {
        return suspendCoroutine { cont ->
            FirebaseAuth.getInstance().signOut()
            if (FirebaseAuth.getInstance().currentUser == null) {
                cont.resumeWith(Result.success(true))
            } else {
                cont.resumeWith(Result.success(false))
            }
        }
    }

    fun getCurrentuser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }
}