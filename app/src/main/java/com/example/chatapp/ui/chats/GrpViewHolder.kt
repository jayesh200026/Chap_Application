package com.example.chatapp.ui.chats

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.GroupDetails
import com.example.chatapp.ui.OnItemClickListener

class GrpViewHolder(view: View,listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
    private val name = view.findViewById<TextView>(R.id.chatusername)
    init {
        view.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }
    fun bind(grp: GroupDetails) {
        name.text = grp.groupName
    }
}