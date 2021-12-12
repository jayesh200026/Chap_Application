package com.example.chatapp.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chatapp.ui.viewpager.ChatFragment
import com.example.chatapp.ui.viewpager.GroupFragment


class FragmentAdapter(fragmentManager: FragmentManager, lifeCycle: Lifecycle) :
    FragmentStateAdapter(
        fragmentManager, lifeCycle
    ) {
    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatFragment()
            1 -> GroupFragment()
            else -> ChatFragment()
        }
    }
}