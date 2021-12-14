package com.example.chatapp.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.FirestoreDatabase
import com.example.chatapp.service.model.GroupChat
import com.example.chatapp.service.model.NotificationService
import com.example.chatapp.service.model.UserIDToken
import com.example.chatapp.service.model.UserWithID
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroupChatViewModel : ViewModel() {
    private val _groupChatMessageStatus = MutableLiveData<GroupChat?>()
    val groupChatMessageStatus = _groupChatMessageStatus as LiveData<GroupChat?>

    private val _uploadMessageImageStatus = MutableLiveData<Uri?>()
    val uploadMessageImageStatus = _uploadMessageImageStatus as LiveData<Uri?>

    private val _nextTenMessagesStatus = MutableLiveData<MutableList<GroupChat>>()
    val nextTenMessagesStatus = _nextTenMessagesStatus as LiveData<MutableList<GroupChat>>

    private val _sendNewMessageStatus = MutableLiveData<Boolean>()
    val sendNewMessageStatus = _sendNewMessageStatus as LiveData<Boolean>

    private val _participantsDetails = MutableLiveData<MutableList<UserIDToken>>()
    val participantsDetails = _participantsDetails as LiveData<MutableList<UserIDToken>>

    fun getAllChats(groupId: String?) {
        viewModelScope.launch {
            FirestoreDatabase.subscribeToGroup(groupId).collect {
                _groupChatMessageStatus.value = it
            }
        }

    }

    fun sendMessage(groupId: String?, message: String, type: String) {
        viewModelScope.launch {
            val status = DatabaseService.addnewGrpMessage(groupId, message, type)
            _sendNewMessageStatus.value = status
        }
    }

    fun loadNextTenGrpChats(groupId: String?, offset: Long) {
        viewModelScope.launch {
            val list = FirestoreDatabase.loadNextGroupChats(groupId, offset)
            _nextTenMessagesStatus.value = list
        }
    }

    fun getParticipantsDetails(groupId: String?) {
        viewModelScope.launch {
            val list = DatabaseService.getGrpParticipantsDetails(groupId)
            _participantsDetails.value = list
        }
    }

    fun sendPushNotification(
        groupName: String,
        participantList: MutableList<UserIDToken>,
        textMessage: String
    ) {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            for (i in participantList) {
                if (i.uid != uid!!)
                    NotificationService.pushNotification(i.token, groupName, textMessage)
            }
        }
    }
}