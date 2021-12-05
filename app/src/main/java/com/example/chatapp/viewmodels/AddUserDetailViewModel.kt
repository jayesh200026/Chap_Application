package com.example.chatapp.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.model.User
import kotlinx.coroutines.launch

class AddUserDetailViewModel : ViewModel() {
    val _addUserDetailsStatus = MutableLiveData<Boolean>()
    val addUserDetailsStatus = _addUserDetailsStatus as LiveData<Boolean>
    fun addUserDetails(user: User) {
        viewModelScope.launch {
            val firestoreStatus = DatabaseService.addUser(user)
            Log.d("addUser",""+firestoreStatus)
            _addUserDetailsStatus.value = firestoreStatus
        }
    }

    fun addProfilePic(it: Uri) {
        viewModelScope.launch {
            DatabaseService.uploadProfilephoto(it)
        }
    }
}