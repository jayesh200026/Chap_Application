package com.example.chatapp.ui.chats

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.Chats
import com.example.chatapp.util.Constants
import java.text.DateFormat

class IndvlChatViewHolder(view: View, val viewType: Int) : RecyclerView.ViewHolder(view) {
    private val senderMessage = view.findViewById<TextView>(R.id.senderMsg)
    private val myMessage = view.findViewById<TextView>(R.id.myMessage)
    fun bind(chat: String, time: Long) {
        if (viewType == Constants.MESSAGE_LEFT) {
            Log.d("chat", "inside view holder")
            senderMessage.text = chat
        } else if (viewType == Constants.MESSAGE_RIGHT) {
            Log.d("chat", "inside right view holder")
            myMessage.text = chat
        }
//        Log.d("chat","inside view holder")
//        Log.d("chat",chat)
//        myMessage.setText(chat)
    }

}