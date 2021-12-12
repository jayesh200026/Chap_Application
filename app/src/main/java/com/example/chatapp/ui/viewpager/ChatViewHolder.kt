package com.example.chatapp.ui.viewpager

import android.content.Context
import android.sax.TextElementListener
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.service.model.Chat
import com.example.chatapp.service.model.UserWithID
import com.example.chatapp.ui.OnItemClickListener
import com.mikhaellopez.circularimageview.CircularImageView

class ChatViewHolder(view: View,val context: Context,listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
    private val name = view.findViewById<TextView>(R.id.chatusername)
    private val pic = view.findViewById<CircularImageView>(R.id.chatuserpic)
    private val lastMessage = view.findViewById<TextView>(R.id.lastMessage)

    init {
        view.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }

    fun bind(item: UserWithID){
        name.text = item.userName
        lastMessage.text = item.status
        if(item.uri != ""){
            Glide.with(context)
                .load(item.uri)
                .centerInside()
                .into(pic)
        }

    }
}