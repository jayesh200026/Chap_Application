package com.example.chatapp.util

import com.example.chatapp.service.model.UserIDToken
import java.io.Serializable

data class GroupParticipants(val list: MutableList<UserIDToken>?) : Serializable {
}