package com.example.chatapp.ui.chats.groupchat

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.service.model.GroupChat
import com.example.chatapp.util.Constants

class GrouchChatViewHolder(val view: View, val viewType: Int) : RecyclerView.ViewHolder(view) {
    fun bind(chat: GroupChat) {
        if (viewType == Constants.MESSAGE_LEFT) {
            val senderName = view.findViewById<TextView>(R.id.senderName)
            val senderMessage = view.findViewById<TextView>(R.id.senderMsg)
            val leftImage = view.findViewById<ImageView>(R.id.leftImage)
            senderName.isVisible = true
            senderName.text = chat.senderName
            if (chat.messageType == Constants.MESSAGE_TYPE_TEXT) {
                leftImage.isVisible = false
                senderMessage.isVisible = true
                senderMessage.text = chat.message
            } else {
                senderMessage.isVisible = false
                leftImage.isVisible = true
                Glide.with(view)
                    .load(chat.imageUri)
                    .centerInside()
                    .into(leftImage)
            }
        } else if (viewType == Constants.MESSAGE_RIGHT) {
            val myMessage = view.findViewById<TextView>(R.id.myMessage)
            val rightImage = view.findViewById<ImageView>(R.id.rightImage)
            Log.d("chat", "inside right view holder")
            if (chat.messageType == Constants.MESSAGE_TYPE_TEXT) {
                rightImage.isVisible = false
                myMessage.isVisible = true
                myMessage.text = chat.message
            } else {
                myMessage.isVisible = false
                rightImage.isVisible = true
                Glide.with(view)
                    .load(chat.imageUri)
                    .centerInside()
                    .into(rightImage)
            }
        }
    }
}