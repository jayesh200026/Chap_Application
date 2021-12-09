package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.FirestoreDatabase
import com.example.chatapp.service.model.GroupChat
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroupChatViewModel: ViewModel() {
    val _groupChatMessageStatus = MutableLiveData<GroupChat?>()
    val groupChatMessageStatus = _groupChatMessageStatus as LiveData<GroupChat?>
    fun getAllChats(groupId: String?) {
        viewModelScope.launch {
            FirestoreDatabase.subscribeToGroup(groupId).collect {
                _groupChatMessageStatus.value = it
            }
        }

    }

    fun sendMessage(groupId: String?, message: String) {
        viewModelScope.launch {
            DatabaseService.addnewGrpMessage(groupId,message)
        }

    }
}