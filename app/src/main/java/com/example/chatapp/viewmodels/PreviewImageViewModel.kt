package com.example.chatapp.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import kotlinx.coroutines.launch

class PreviewImageViewModel: ViewModel() {
    val _uploadMessageImageStatus = MutableLiveData<Uri?>()
    val uploadMessageImageStatus = _uploadMessageImageStatus as LiveData<Uri?>

    val _uploadGrpMessageImageStatus = MutableLiveData<Uri?>()
    val uploadGrpMessageImageStatus = _uploadGrpMessageImageStatus as LiveData<Uri?>

    val _addingMewImageMessageStatus = MutableLiveData<Boolean>()
    val addingMewImageMessageStatus = _addingMewImageMessageStatus as LiveData<Boolean>

    val _addingMewGrpImageMessageStatus = MutableLiveData<Boolean>()
    val addingMewGrpImageMessageStatus = _addingMewGrpImageMessageStatus as LiveData<Boolean>

    fun uploadMessageImage(uri: Uri?) {
        viewModelScope.launch {
            val uri = DatabaseService.uploadMessageImage(uri)
            _uploadMessageImageStatus.value = uri
        }
    }


    fun sendMessage(receiver: String?, message: String, type: String) {
        viewModelScope.launch {
            val status= DatabaseService.addNewMessage(receiver, message, type)
            _addingMewImageMessageStatus.value = status
        }
    }

    fun uploadMessageImageTogrp(uri: Uri?) {
        viewModelScope.launch {
            val uri = DatabaseService.uploadMessageImage(uri)
            _uploadGrpMessageImageStatus.value = uri
        }
    }

    fun sendGrpMessage(groupId: String?, message: String, type: String) {
        viewModelScope.launch {
            val status = DatabaseService.addnewGrpMessage(groupId,message,type)
            _addingMewGrpImageMessageStatus.value = status
        }

    }
}