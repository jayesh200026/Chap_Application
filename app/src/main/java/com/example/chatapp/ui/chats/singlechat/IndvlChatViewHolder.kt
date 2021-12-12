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

class IndvlChatViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val senderMessage = view.findViewById<TextView>(R.id.senderMsg)

    private val leftImage = view.findViewById<ImageView>(R.id.leftImage)

    fun bind(chat: Chats) {
            if(chat.messageType == Constants.MESSAGE_TYPE_TEXT){
                leftImage.isVisible = false
                senderMessage.isVisible = true
                senderMessage.text = chat.message
            }
            else if(chat.messageType == Constants.MESSAGE_TYPE_IMAGE){
                senderMessage.isVisible = false
                leftImage.isVisible = true
                Glide.with(view)
                    .load(chat.imageUri)
                    .optionalCenterInside()
                    .into(leftImage)
            }
            Log.d("chat", "inside view holder")

        }

}

class IndvlChatViewHolderRight(val view: View): RecyclerView.ViewHolder(view){

    private val rightImage = view.findViewById<ImageView>(R.id.rightImage)
    private val myMessage = view.findViewById<TextView>(R.id.myMessage)
    fun bind(chat: Chats){
        Log.d("inside bind",chat.messageType)
        if(chat.messageType == Constants.MESSAGE_TYPE_TEXT){
            rightImage.isVisible = false
            myMessage.isVisible = true
            myMessage.text = chat.message
        }
        else if(chat.messageType == Constants.MESSAGE_TYPE_IMAGE){
            Log.d("duplicate","loading image")
            myMessage.isVisible = false
            rightImage.isVisible = true
            Glide.with(view)
                .load(chat.imageUri)
                .optionalCenterInside()
                .into(rightImage)
        }

    }

}