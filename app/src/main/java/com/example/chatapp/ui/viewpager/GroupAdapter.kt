package com.example.chatapp.ui.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.GroupDetails
import com.example.chatapp.ui.OnItemClickListener

class GroupAdapter(val list: MutableList<GroupDetails>):RecyclerView.Adapter<GrpViewHolder>() {
    private lateinit var mListner: OnItemClickListener

    fun setOnItemClickListner(listener: OnItemClickListener) {
        mListner = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat, parent, false)
        return GrpViewHolder(view,mListner)
    }

    override fun onBindViewHolder(holder: GrpViewHolder, position: Int) {
        val grp = list[position]
        holder.bind(grp)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}