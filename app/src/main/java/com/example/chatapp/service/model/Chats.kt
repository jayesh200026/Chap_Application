package com.example.chatapp.service.model

class Chats(
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val messageType: String,
    val sentTime: Long
) {
}