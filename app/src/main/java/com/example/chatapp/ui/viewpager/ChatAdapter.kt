package com.example.chatapp.ui.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.UserIDToken
import com.example.chatapp.ui.OnItemClickListener

class ChatAdapter(val context: Context, val list: MutableList<UserIDToken>) :
    RecyclerView.Adapter<ChatViewHolder>() {
    private lateinit var mListner: OnItemClickListener

    fun setOnItemClickListner(listener: OnItemClickListener) {
        mListner = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat, parent, false)
        return ChatViewHolder(view, context,mListner)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}