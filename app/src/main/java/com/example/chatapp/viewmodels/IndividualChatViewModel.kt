package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.model.AllMessages
import com.example.chatapp.service.model.Chats
import kotlinx.coroutines.launch

class IndividualChatViewModel : ViewModel() {
    val _readAllChatsStatsus = MutableLiveData<MutableList<AllMessages>>()
    val readAllChatsStatsus = _readAllChatsStatsus as LiveData<MutableList<AllMessages>>
    fun getAllChats(participant: String?) {
        viewModelScope.launch {
            val list = DatabaseService.getAllChats(participant)
            _readAllChatsStatsus.value = list
        }

    }
}