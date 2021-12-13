package com.example.chatapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.FirebaseAuthentication
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.launch

class VerifyOtpViewModel: ViewModel() {

    val _signInWithCredentialStatus = MutableLiveData<String>()
    val signInWithCrentialStatus = _signInWithCredentialStatus as LiveData<String>

    val _updateDeviceTokenStatus = MutableLiveData<Boolean>()
    val updateDeviceTokenStatus = _updateDeviceTokenStatus as LiveData<Boolean>

    fun signInWithCredentails(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            try {
               val signInStatus = FirebaseAuthentication.signInWithCredential(credential)
                Log.d("login",signInStatus)
                _signInWithCredentialStatus.value = signInStatus
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun updateDeviceToken(token: String?) {
        viewModelScope.launch {
            val status = DatabaseService.updateDeviceToken(token)
            _updateDeviceTokenStatus.value = status
        }

    }

}