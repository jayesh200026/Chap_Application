package com.example.chatapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val _gotoSetProfilePageStatus = MutableLiveData<Boolean>()
    val gotoSetProfilePageStatus = _gotoSetProfilePageStatus as LiveData<Boolean>

    val _gotoSetVerifiOtpPageStatus = MutableLiveData<Boolean>()
    val gotoSetVerifiOtpPageStatus = _gotoSetVerifiOtpPageStatus as LiveData<Boolean>

    val _gotoHomePageStatus = MutableLiveData<Boolean>()
    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<Boolean>

    val _gotoRequestOtpPageStatus = MutableLiveData<Boolean>()
    val gotoRequestOtpPageStatus = _gotoRequestOtpPageStatus as LiveData<Boolean>

    val _gotoEditProfilePageStatus = MutableLiveData<Boolean>()
    val gotoEditProfilePageStatus = _gotoEditProfilePageStatus as LiveData<Boolean>

    val _gotoIndividualChatPageStatus = MutableLiveData<Boolean>()
    val gotoIndividualChatPageStatus = _gotoIndividualChatPageStatus as LiveData<Boolean>

    val _gotoGroupChatPageStatus = MutableLiveData<Boolean>()
    val gotoGroupChatPageStatus = _gotoGroupChatPageStatus as LiveData<Boolean>

    fun setGotoSetProfilePage(status: Boolean) {
        _gotoSetProfilePageStatus.value = status
    }

    fun setGotoVerifyotpPageStatus(b: Boolean) {
        _gotoSetVerifiOtpPageStatus.value = b
    }

    fun setGotoHomePageStatus(b: Boolean) {
        _gotoHomePageStatus.value = b
    }

    fun setGotoRequestOtpStatus(b: Boolean) {
        _gotoRequestOtpPageStatus.value = b
    }

    fun setGotoEditProfilePage(b: Boolean) {
        _gotoEditProfilePageStatus.value = b
    }

    fun setGotoIndividualChatStatus(b: Boolean) {
        _gotoIndividualChatPageStatus.value = b
    }

    fun setGotoGroupChatPage(b: Boolean) {
        _gotoGroupChatPageStatus.value = b
    }

}