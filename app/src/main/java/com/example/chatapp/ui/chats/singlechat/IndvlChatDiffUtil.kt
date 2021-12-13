package com.example.chatapp.ui.chats.singlechat

import androidx.recyclerview.widget.DiffUtil
import com.example.chatapp.service.model.Chats

class IndvlChatDiffUtil(
    val oldList: MutableList<Chats>,
    val newList: MutableList<Chats>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].messageId == newList[newItemPosition].messageId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].messageId != newList[newItemPosition].messageId -> {
                false
            }
            oldList[oldItemPosition].message != newList[newItemPosition].message -> {
                false
            }
            oldList[oldItemPosition].messageType != newList[newItemPosition].messageType -> {
                false
            }
            oldList[oldItemPosition].receiverId != newList[newItemPosition].receiverId -> {
                false
            }
            oldList[oldItemPosition].senderId != newList[newItemPosition].senderId -> {
                false
            }
            oldList[oldItemPosition].sentTime != newList[newItemPosition].sentTime -> {
                false
            }
            oldList[oldItemPosition].imageUri != newList[newItemPosition].imageUri -> {
                false
            }
            else -> {
                true
            }
        }

    }
}