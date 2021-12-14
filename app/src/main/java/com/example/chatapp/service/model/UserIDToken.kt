package com.example.chatapp.service.model

data class UserIDToken(
    val uid: String,
    val name: String,
    val status: String,
    val image: String,
    val token: String
) {
}