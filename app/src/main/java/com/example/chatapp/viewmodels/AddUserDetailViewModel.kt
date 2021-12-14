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
    private val _addUserDetailsStatus = MutableLiveData<Boolean>()
    val addUserDetailsStatus = _addUserDetailsStatus as LiveData<Boolean>

    private val _addProfilePicStatus = MutableLiveData<Uri?>()
    val addProfilePicStatus = _addProfilePicStatus as LiveData<Uri?>

    fun addUserDetails(user: User, token: String?) {
        viewModelScope.launch {
            val firestoreStatus = DatabaseService.addUser(user, token)
            Log.d("addUser", "" + firestoreStatus)
            _addUserDetailsStatus.value = firestoreStatus
        }
    }

    fun addProfilePic(it: Uri?) {
        viewModelScope.launch {
            val status = DatabaseService.uploadProfilephoto(it)
            _addProfilePicStatus.value = status
        }
    }
}