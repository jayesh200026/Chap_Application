package com.example.chatapp.ui.network

data class PushMessage(
    val to: String,
    val notification: PushContent
)