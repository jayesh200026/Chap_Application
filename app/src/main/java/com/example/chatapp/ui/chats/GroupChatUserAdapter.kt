package com.example.chatapp.ui.chats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.service.model.UserWithID
import com.mikhaellopez.circularimageview.CircularImageView


class GroupChatUserAdapter(
    private val userList: ArrayList<UserWithID>,
    private val context: Context
) : RecyclerView.Adapter<GroupChatUserAdapter.GroupChatUserViewHolder>() {

    var selectedUser = mutableListOf<String>()

    class GroupChatUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatUserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.select_user_rv_item,
            parent, false
        )
        return GroupChatUserViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: GroupChatUserViewHolder, position: Int) {
        val userName = holder.itemView.findViewById<TextView>(R.id.recycler_item_userName)
        val profileImage =
            holder.itemView.findViewById<CircularImageView>(R.id.imageView_recycler_item)
        val checkBox = holder.itemView.findViewById<CheckBox>(R.id.select_user_cb)

        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                selectedUser.add(userList[position].userId)
            } else if (!checkBox.isChecked) {
                selectedUser.remove(userList[position].userId)
            }
        }
        holder.itemView.apply {
            userName.text = userList[position].userName
            if (userList[position].uri.isNotBlank()) {
                Glide.with(context).load(userList[position].uri).dontAnimate()
                    .into(profileImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun getSelectedList(): MutableList<String> {
        return selectedUser
    }
}