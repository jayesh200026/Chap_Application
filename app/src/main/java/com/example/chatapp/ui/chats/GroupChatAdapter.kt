package com.example.chatapp.ui.chats

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.GroupChat
import com.example.chatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth

class GroupChatAdapter(val list: MutableList<GroupChat>) :
    RecyclerView.Adapter<GrouchChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrouchChatViewHolder {
        if(viewType == Constants.MESSAGE_LEFT){
            Log.d("chat","left message onviewholder")
            val view = LayoutInflater.from(parent.context).inflate(R.layout.left_chat, parent, false)
            return GrouchChatViewHolder(view,viewType)
        }
        else{
            Log.d("chat","right message viewholder")
            val view = LayoutInflater.from(parent.context).inflate(R.layout.right_chat, parent, false)
            return GrouchChatViewHolder(view,viewType)
        }
    }

    override fun onBindViewHolder(holder: GrouchChatViewHolder, position: Int) {
        val chat = list[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    override fun getItemViewType(position: Int): Int {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if(uid != null){
            if(list[position].senderId.equals(uid)){
                Log.d("chat","right message")
                return Constants.MESSAGE_RIGHT
            }
            else{
                Log.d("chat","left message")
                return Constants.MESSAGE_LEFT
            }
        }
        return -1
    }

}