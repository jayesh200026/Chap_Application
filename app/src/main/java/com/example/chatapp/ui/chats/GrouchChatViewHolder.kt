package com.example.chatapp.ui.chats

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.GroupChat
import com.example.chatapp.util.Constants

class GrouchChatViewHolder(val view: View,val viewType: Int):RecyclerView.ViewHolder(view) {
    fun bind(chat: GroupChat) {
        if(viewType == Constants.MESSAGE_LEFT){
             val senderName = view.findViewById<TextView>(R.id.senderName)
             val senderMessage = view.findViewById<TextView>(R.id.senderMsg)
            Log.d("chat","inside view holder")
            Log.d("chat",chat.senderId)
            senderMessage.text = chat.message
//            senderName.isVisible = true
//            senderName.text = chat.senderId
        }
        else if(viewType == Constants.MESSAGE_RIGHT){
             val myMessage = view.findViewById<TextView>(R.id.myMessage)
            Log.d("chat","inside right view holder")
            myMessage.text = chat.message
        }

    }
}