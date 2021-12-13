package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.FirestoreDatabase
import com.example.chatapp.service.model.User
import kotlinx.coroutines.launch

class ParticipantDetailVm: ViewModel() {
    private val _getParticipantDetailsStatus = MutableLiveData<User?>()
    val getParticipantDetailsStatus = _getParticipantDetailsStatus as LiveData<User?>
    fun getParticipantDetails(participantId: String?) {
        viewModelScope.launch {
            val details = DatabaseService.getParticipantDetails(participantId)
            _getParticipantDetailsStatus.value = details
        }

    }
}