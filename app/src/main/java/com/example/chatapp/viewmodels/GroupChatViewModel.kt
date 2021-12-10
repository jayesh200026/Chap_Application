package com.example.chatapp.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.FirestoreDatabase
import com.example.chatapp.service.model.GroupChat
import com.example.chatapp.service.model.UserWithID
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroupChatViewModel: ViewModel() {
    val userList = ArrayList<UserWithID>()

    val _groupChatMessageStatus = MutableLiveData<GroupChat?>()
    val groupChatMessageStatus = _groupChatMessageStatus as LiveData<GroupChat?>

    val _uploadMessageImageStatus = MutableLiveData<Uri?>()
    val uploadMessageImageStatus = _uploadMessageImageStatus as LiveData<Uri?>

    fun getAllChats(groupId: String?) {
        viewModelScope.launch {
            FirestoreDatabase.subscribeToGroup(groupId).collect {
                _groupChatMessageStatus.value = it
            }
        }

    }

    fun sendMessage(groupId: String?, message: String,type: String) {
        viewModelScope.launch {
            DatabaseService.addnewGrpMessage(groupId,message,type)
        }

    }

    fun getAllUSerDetails() {
        viewModelScope.launch {
            FirestoreDatabase.getAllUsersFromDb().collect {
                userList.clear()
                userList.addAll(it as ArrayList<UserWithID>)
            }
        }
    }

    fun uploadMessageImage(selectedImagePath: Uri?) {
        viewModelScope.launch {
            val uri = DatabaseService.uploadMessageImage(selectedImagePath)
            _uploadMessageImageStatus.value = uri
        }
    }
}