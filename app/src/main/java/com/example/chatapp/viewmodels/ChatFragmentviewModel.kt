package com.example.chatapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirestoreDatabase
import com.example.chatapp.service.model.UserIDToken
import com.example.chatapp.service.model.UserWithID
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatFragmentviewModel : ViewModel() {
    private val _chatStatus = MutableLiveData<MutableList<UserIDToken>>()
    val chatStatus = _chatStatus as LiveData<MutableList<UserIDToken>>

    private val _userdetailStatus = MutableLiveData<MutableList<UserWithID>>()
    val userdetailsStatus = _userdetailStatus as LiveData<MutableList<UserWithID>>

    fun readchats() {
        viewModelScope.launch {
            val friends = mutableListOf<UserIDToken>()
            FirestoreDatabase.readChats().collect {
                friends.clear()
                Log.d("chats", it.toString())
                val allUsers = FirestoreDatabase.readAllUsers()
                for (i in allUsers) {
                    if (i.uid in it) {
                        Log.d("chats", i.toString())
                        val chatUser = UserIDToken(i.uid, i.name, i.status, i.image, i.token)
                        friends.add(chatUser)
                    }
                }
                _chatStatus.value = friends
            }
        }
    }
}