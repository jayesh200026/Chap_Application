package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseAuthentication
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus = _logoutStatus as LiveData<Boolean>
    fun logout() {
        viewModelScope.launch {
            val status = FirebaseAuthentication.logout()
            _logoutStatus.value = status
        }
    }
}