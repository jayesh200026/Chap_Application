package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.model.Chat
import com.example.chatapp.service.model.User
import com.example.chatapp.service.model.UserWithID
import kotlinx.coroutines.launch

class ChatFragmentviewModel: ViewModel() {
    val _chatStatus = MutableLiveData<MutableList<UserWithID>>()
    val chatStatus = _chatStatus as LiveData<MutableList<UserWithID>>

    val _userdetailStatus = MutableLiveData<MutableList<UserWithID>>()
    val userdetailsStatus = _userdetailStatus as LiveData<MutableList<UserWithID>>

    fun readchats() {
        viewModelScope.launch {
            val list = DatabaseService.readChats()
            _chatStatus.value = list
        }
    }

    fun readuserdetails() {
        viewModelScope.launch {
            val list = DatabaseService.readAllUsers()
            _userdetailStatus.value = list
        }
    }
}