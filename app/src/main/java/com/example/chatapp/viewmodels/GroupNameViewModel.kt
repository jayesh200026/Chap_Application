package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.model.CreateGroup
import kotlinx.coroutines.launch

class GroupNameViewModel: ViewModel() {
    private val _grpCreatedStatus = MutableLiveData<Boolean>()
    val grpCreatedStatus = _grpCreatedStatus as LiveData<Boolean>
    fun createGrp(name: String,list: ArrayList<String>?) {
        viewModelScope.launch {
            val status = DatabaseService.createGrp(name,list)
            _grpCreatedStatus.value = status
        }
    }
}