package com.example.chatapp.service.model

data class Chats(
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val messageType: String,
    val imageUri: String,
    val sentTime: Long
) {
}