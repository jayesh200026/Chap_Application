package com.example.chatapp.service.model

data class GroupChat(
    val message: String,
    val messageId: String,
    val messageType: String,
    val senderId: String,
    val sentTime: Long
) {
}