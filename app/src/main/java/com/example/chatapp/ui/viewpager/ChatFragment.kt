package com.example.chatapp.ui.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.UserIDToken
import com.example.chatapp.ui.OnItemClickListener
import com.example.chatapp.ui.chats.DisplayAllUserFragment
import com.example.chatapp.ui.chats.singlechat.IndividualChatFragment
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.ChatFragmentViewModelfactory
import com.example.chatapp.viewmodels.ChatFragmentviewModel
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ChatFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var chatFragmentviewModel: ChatFragmentviewModel
    lateinit var sharedViewModel: SharedViewModel
    lateinit var progressBar: ProgressBar
    private lateinit var adapter: ChatAdapter
    lateinit var addFab: FloatingActionButton
    var chatList = mutableListOf<UserIDToken>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        recyclerView = view.findViewById(R.id.rvChats)
        progressBar = view.findViewById(R.id.chatPB)
        addFab = view.findViewById(R.id.group_fragment_floating_button)
        addFab.setImageResource(R.drawable.ic_baseline_chat_24)
        addFab.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().add(
                R.id.flFragment,
                DisplayAllUserFragment()
            ).addToBackStack(null)
                .commit()
        }
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        chatFragmentviewModel = ViewModelProvider(
            this,
            ChatFragmentViewModelfactory()
        )[ChatFragmentviewModel::class.java]
        adapter = ChatAdapter(requireContext(), chatList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListner(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                SharedPref.addString(Constants.COLUMN_PARTICIPANTS, chatList[position].uid)
                SharedPref.addString(Constants.COLUMN_URI, chatList[position].image)
                SharedPref.addString(Constants.COLUMN_NAME, chatList[position].name)
                SharedPref.addString(Constants.PARTICIPANT_TOKEN,chatList[position].token)
                requireActivity().supportFragmentManager.beginTransaction().add(
                    R.id.flFragment,
                    IndividualChatFragment()
                )
                    .addToBackStack(null)
                    .commit()
            }
        })
        readChats()
        observe()
        return view
    }

    private fun observe() {
        chatFragmentviewModel.chatStatus.observe(viewLifecycleOwner) {
            chatList.clear()
            progressBar.isVisible = false
            for (i in it) {
                chatList.add(i)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun readChats() {
        progressBar.isVisible = true
        chatFragmentviewModel.readchats()
    }
}