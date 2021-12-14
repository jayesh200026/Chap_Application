package com.example.chatapp.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.model.NotificationService
import com.example.chatapp.service.model.UserIDToken
import com.example.chatapp.util.Constants
import com.example.chatapp.util.GroupParticipants
import com.example.chatapp.util.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
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
            NotificationService.pushNotification(SharedPref.get(Constants.PARTICIPANT_TOKEN)!!,SharedPref.get(Constants.CURRENT_USER_USERNAME)!!,"sent image")
            _addingMewImageMessageStatus.value = status
        }
    }

    fun uploadMessageImageTogrp(uri: Uri?) {
        viewModelScope.launch {
            val uri = DatabaseService.uploadMessageImage(uri)
            _uploadGrpMessageImageStatus.value = uri
        }
    }

    fun sendGrpMessage(groupId: String?, message: String, type: String,grpUsers: MutableList<UserIDToken>) {
        viewModelScope.launch {
            val status = DatabaseService.addnewGrpMessage(groupId,message,type)
            for(i in grpUsers){
                if(i.uid != FirebaseAuth.getInstance().currentUser!!.uid && i.token != ""){
                    NotificationService.pushNotification(i.token,SharedPref.get(Constants.GROUP_NAME)!!,"sent image")

                }
            }
            _addingMewGrpImageMessageStatus.value = status
        }
    }

    fun sendNotification() {
        viewModelScope.launch {
            NotificationService.pushNotification(SharedPref.get(Constants.PARTICIPANT_TOKEN)!!,SharedPref.get(Constants.CURRENT_USER_USERNAME)!!,"sent image")
        }
    }

    fun sendGrpNotification(grpUsers: MutableList<UserIDToken>) {
        Log.d("grp",grpUsers.toString())
        viewModelScope.launch {
            for(i in grpUsers){
                if(i.uid != FirebaseAuth.getInstance().currentUser!!.uid && i.token != ""){
                    Log.d("grp",i.toString())
                    Log.d("grpname",SharedPref.get(Constants.GROUP_NAME)!!)
                    NotificationService.pushNotification(i.token,SharedPref.get(Constants.GROUP_NAME)!!,"sent image")
                }
            }
        }
    }
}