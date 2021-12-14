package com.example.chatapp.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.FirestoreDatabase
import com.example.chatapp.service.model.AllMessages
import com.example.chatapp.service.model.Chats
import com.example.chatapp.service.model.NotificationService
import com.example.chatapp.service.model.User
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class IndividualChatViewModel : ViewModel() {
    private val _readAllChatsStatsus = MutableLiveData<MutableList<AllMessages>>()
    val readAllChatsStatsus = _readAllChatsStatsus as LiveData<MutableList<AllMessages>>

    private val _chatsStatus = MutableLiveData<Chats?>()
    val chatStatus = _chatsStatus as LiveData<Chats?>

    private val _nextchatsStatus = MutableLiveData<MutableList<Chats>>()
    val nextchatsStatus = _nextchatsStatus as LiveData<MutableList<Chats>>

    private val _uploadMessageImageStatus = MutableLiveData<Uri?>()
    val uploadMessageImageStatus = _uploadMessageImageStatus as LiveData<Uri?>

    private val _sendMessageStatus = MutableLiveData<Boolean>()
    val sendMessageStatus = _sendMessageStatus as LiveData<Boolean>

    private val _currentUserStatus = MutableLiveData<User>()
    val currentUserStatus = _currentUserStatus as LiveData<User>

    fun sendMessage(receiver: String?, message: String, type: String) {
        viewModelScope.launch {
            val status = DatabaseService.addNewMessage(receiver, message, type)
            _sendMessageStatus.value = status
        }
    }

    fun subscribe(participant: String?) {
        viewModelScope.launch {
            FirestoreDatabase.subscribeToListener(participant).collect {
                _chatsStatus.value = it
            }
        }
    }

    fun loadNextTenChats(offset: Long, participant: String?) {
        viewModelScope.launch {
            val list = FirestoreDatabase.loadNextChats(participant, offset)
            _nextchatsStatus.value = list
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            val user = FirestoreDatabase.getUserdetails()
            _currentUserStatus.value = user
        }
    }

    fun sendPushNotification(token: String?, userName: String, textMessage: String) {
        viewModelScope.launch {
            if (textMessage == "") {
                NotificationService.pushNotification(token, userName, "Sent image")
            } else {
                NotificationService.pushNotification(token, userName, textMessage)
            }
        }
    }
}