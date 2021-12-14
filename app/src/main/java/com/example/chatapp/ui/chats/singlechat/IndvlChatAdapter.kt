package com.example.chatapp.ui.chats.singlechat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.Chats
import com.example.chatapp.ui.chats.IndvlChatViewHolder
import com.example.chatapp.ui.chats.IndvlChatViewHolderRight
import com.example.chatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth

class IndvlChatAdapter(val list: MutableList<Chats>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == Constants.MESSAGE_LEFT) {
            Log.d("chat", "left message onviewholder")
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.left_chat, parent, false)
            return IndvlChatViewHolder(view)
        } else {
            Log.d("chat", "right message viewholder")
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.right_chat, parent, false)
            return IndvlChatViewHolderRight(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = list[position]
        if (getItemViewType(position) == Constants.MESSAGE_LEFT) {
            (holder as IndvlChatViewHolder).bind(chat)
        } else {
            Log.d("duplicate", chat.message)
            (holder as IndvlChatViewHolderRight).bind(chat)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            if (list[position].senderId.equals(uid)) {
                Log.d("chat", "right message")
                return Constants.MESSAGE_RIGHT
            } else {
                Log.d("chat", "left message")
                return Constants.MESSAGE_LEFT
            }
        }
        return -1
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}