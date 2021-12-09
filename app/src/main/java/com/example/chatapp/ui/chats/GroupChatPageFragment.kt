package com.example.chatapp.ui.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.GroupChat
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.GroupChatViewModel
import com.example.chatapp.viewmodels.GroupChatViewModelFactory
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.mikhaellopez.circularimageview.CircularImageView


class GroupChatPageFragment : Fragment() {
    lateinit var sharedViewModel:SharedViewModel
    lateinit var groupChatViewModel: GroupChatViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: GroupChatAdapter
    lateinit var groupName: TextView
    lateinit var profilePic: CircularImageView
    lateinit var backBtn: ImageView
    lateinit var sendBtn : ImageButton
    lateinit var chatMessage: EditText
    val list = mutableListOf<GroupChat>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        var groupId = SharedPref.get(Constants.GROUP_ID)
        var groupname = SharedPref.get(Constants.GROUP_NAME)
        groupChatViewModel = ViewModelProvider(
            this,
            GroupChatViewModelFactory()
        )[GroupChatViewModel::class.java]
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        recyclerView = view.findViewById(R.id.rvIndlChat)
        val layoutMangaer = LinearLayoutManager(requireContext())
        //layoutMangaer.stackFromEnd=  true
        recyclerView.layoutManager = layoutMangaer
        adapter = GroupChatAdapter(list)
        recyclerView.adapter = adapter
        chatMessage = view.findViewById(R.id.chatET)
        sendBtn = view.findViewById(R.id.chatsendBtn)
        groupName = view.findViewById(R.id.participantName)
        backBtn = view.findViewById(R.id.chatBackBtn)
        groupname?.let {
            groupName.text = it
        }
        sendBtn.setOnClickListener {
            val message = chatMessage.text.toString()
            if(message.isNotEmpty()) {
                groupChatViewModel.sendMessage(groupId, message)
                chatMessage.setText("")
            }
        }
        backBtn.setOnClickListener {
            sharedViewModel.setGotoHomePageStatus(true)
        }
        getAllChatsOfGroup(groupId)
        observe()
        return  view
    }

    private fun observe() {
        groupChatViewModel.groupChatMessageStatus.observe(viewLifecycleOwner){
            if(it != null){
                list.add(it)
                adapter.notifyItemInserted(list.size - 1)
            }
        }
    }

    private fun getAllChatsOfGroup(groupId: String?) {
        groupChatViewModel.getAllChats(groupId)

    }

}