package com.example.chatapp.viewmodels

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
    fun getAllChats(participant: String?) {
        viewModelScope.launch {
            val list = DatabaseService.getAllChats(participant)
            _readAllChatsStatsus.value = list
        }

    }

    fun sendMessage(receiver: String?, message: String) {
        viewModelScope.launch {
            DatabaseService.addNewMessage(receiver,message)
        }
    }

    fun subscribe(participant: String?) {
        viewModelScope.launch {
            FirestoreDatabase.subscribeToListener(participant).collect {
                _chatsStatus.value = it
            }
        }

    }
}