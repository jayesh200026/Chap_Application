package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.DatabaseService
import com.example.chatapp.service.FirestoreDatabase
import com.example.chatapp.service.model.UserIDToken
import com.example.chatapp.service.model.UserWithID
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddUserToGrpVM : ViewModel() {
    val userList = ArrayList<UserIDToken>()

    private val _getUserListStatus = MutableLiveData<Boolean>()
    val getUserListStatus = _getUserListStatus as LiveData<Boolean>

    fun getUserListFromDb() {
        viewModelScope.launch {
            FirestoreDatabase.getAllUsersFromDb().collect {
                userList.clear()
                userList.addAll(it as ArrayList<UserIDToken>)
                _getUserListStatus.postValue(true)
            }
        }
    }
}