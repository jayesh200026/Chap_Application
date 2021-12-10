package com.example.chatapp.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.FirestoreDatabase
import com.example.chatapp.service.model.AllMessages
import com.example.chatapp.service.model.Chats
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class IndividualChatViewModel : ViewModel() {
    val _readAllChatsStatsus = MutableLiveData<MutableList<AllMessages>>()
    val readAllChatsStatsus = _readAllChatsStatsus as LiveData<MutableList<AllMessages>>

    val _chatsStatus = MutableLiveData<Chats?>()
    val chatStatus = _chatsStatus as LiveData<Chats?>

    val _uploadMessageImageStatus = MutableLiveData<Uri?>()
    val uploadMessageImageStatus = _uploadMessageImageStatus as LiveData<Uri?>

    fun getAllChats(participant: String?) {
        viewModelScope.launch {
            val list = DatabaseService.getAllChats(participant)
            _readAllChatsStatsus.value = list
        }
    }

    fun sendMessage(receiver: String?, message: String, type: String) {
        viewModelScope.launch {
            DatabaseService.addNewMessage(receiver, message, type)
        }
    }

    fun subscribe(participant: String?) {
        viewModelScope.launch {
            FirestoreDatabase.subscribeToListener(participant).collect {
                _chatsStatus.value = it
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