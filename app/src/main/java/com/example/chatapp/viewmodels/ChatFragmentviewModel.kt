package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.model.Chat
import com.example.chatapp.service.model.User
import com.example.chatapp.service.model.UserIDToken
import com.example.chatapp.service.model.UserWithID
import kotlinx.coroutines.launch

class ChatFragmentviewModel: ViewModel() {
    val _chatStatus = MutableLiveData<MutableList<UserIDToken>>()
    val chatStatus = _chatStatus as LiveData<MutableList<UserIDToken>>

    val _userdetailStatus = MutableLiveData<MutableList<UserWithID>>()
    val userdetailsStatus = _userdetailStatus as LiveData<MutableList<UserWithID>>

    fun readchats() {
        viewModelScope.launch {
            val list = DatabaseService.readChats()
            _chatStatus.value = list
        }
    }


}