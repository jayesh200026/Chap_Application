package com.example.chatapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.ui.chats.DisplayAllUserFragment

class DisplayAllUserVMFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DisplayAllUSerVM() as T
    }
}