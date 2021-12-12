package com.example.chatapp.ui.viewpager

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.GroupDetails
import com.example.chatapp.ui.OnItemClickListener

class GrpViewHolder(view: View,listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
    private val name = view.findViewById<TextView>(R.id.chatusername)
    private val lastMessage = view.findViewById<TextView>(R.id.lastMessage)
    init {
        view.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }
    fun bind(grp: GroupDetails) {
        lastMessage.isVisible = false
        name.text = grp.groupName
    }
}