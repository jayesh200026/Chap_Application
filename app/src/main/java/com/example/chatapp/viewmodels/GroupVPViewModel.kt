package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.FirestoreDatabase
import com.example.chatapp.service.model.GroupDetails
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroupVPViewModel: ViewModel() {
    val _grpListStatus = MutableLiveData<MutableList<GroupDetails>>()
    val grpListStatus = _grpListStatus as LiveData<MutableList<GroupDetails>>
//    fun getGroups() {
//        viewModelScope.launch {
//            val list = DatabaseService.getGroups()
//            _grpListStatus.value = list
//        }
//    }
fun getGroups() {
    viewModelScope.launch {
        FirestoreDatabase.getGroups().collect {
            _grpListStatus.value = it
        }

    }
}
}