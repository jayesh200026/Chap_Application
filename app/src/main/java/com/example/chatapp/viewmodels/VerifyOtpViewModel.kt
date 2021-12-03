package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthCredential

class VerifyOtpViewModel: ViewModel() {

    val _signInWithCredentialStatus = MutableLiveData<Boolean>()
    val signInWithCrentialStatus = _signInWithCredentialStatus as LiveData<Boolean>

    fun signInWithCredentails(credential: PhoneAuthCredential) {

    }

}