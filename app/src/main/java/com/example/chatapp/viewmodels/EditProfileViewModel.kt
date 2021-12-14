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

class EditProfileViewModel : ViewModel() {
    private val _profilePhotoStatus = MutableLiveData<Uri?>()
    val profilePhotoStatus = _profilePhotoStatus as LiveData<Uri?>

    private val _profilePhotoAddStatus = MutableLiveData<Uri?>()
    val profilePhotoAddStatus = _profilePhotoAddStatus as LiveData<Uri?>

    private val _userDetailsStatus = MutableLiveData<User?>()
    val userDetailsStatus = _userDetailsStatus as LiveData<User?>

    private val _addUserDetailsStatus = MutableLiveData<Boolean>()
    val addUserDetailsStatus = _addUserDetailsStatus as LiveData<Boolean>

    fun fetchUserDetails() {
        viewModelScope.launch {
            val user = DatabaseService.getUserDetails()
            _userDetailsStatus.value = user
        }
    }

    fun uploadProfilePic(it: Uri) {
        viewModelScope.launch {
            val uri = DatabaseService.uploadProfilephoto(it)
            _profilePhotoAddStatus.value = uri
        }
    }

    fun addUserDetails(user: User, token: String?) {
        viewModelScope.launch {
            val firestoreStatus = DatabaseService.addUser(user, token)
            Log.d("addUser", "" + firestoreStatus)
            _addUserDetailsStatus.value = firestoreStatus
        }
    }
}