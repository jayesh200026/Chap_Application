package com.example.chatapp.ui.chats

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.service.model.Chats
import com.example.chatapp.util.Constants
import java.text.DateFormat

class IndvlChatViewHolder(val view: View, val viewType: Int) : RecyclerView.ViewHolder(view) {
    private val senderMessage = view.findViewById<TextView>(R.id.senderMsg)
    private val myMessage = view.findViewById<TextView>(R.id.myMessage)
    private val leftImage = view.findViewById<ImageView>(R.id.leftImage)
    private val rightImage = view.findViewById<ImageView>(R.id.rightImage)

    fun bind(chat: Chats) {
        if (viewType == Constants.MESSAGE_LEFT) {
            if(chat.messageType == Constants.MESSAGE_TYPE_TEXT){
                senderMessage.text = chat.message
            }
            else if(chat.messageType == Constants.MESSAGE_TYPE_IMAGE){
                senderMessage.isVisible = false
                leftImage.isVisible = true
                Glide.with(view)
                    .load(chat.imageUri)
                    .centerInside()
                    .into(leftImage)
            }
            Log.d("chat", "inside view holder")

        } else if (viewType == Constants.MESSAGE_RIGHT) {
            if(chat.messageType == Constants.MESSAGE_TYPE_TEXT) {
                myMessage.text = chat.message
            }else if(chat.messageType == Constants.MESSAGE_TYPE_IMAGE){
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