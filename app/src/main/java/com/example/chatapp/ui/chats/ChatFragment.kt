package com.example.chatapp.ui.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.UserWithID
import com.example.chatapp.ui.OnItemClickListener
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.ChatFragmentViewModelfactory
import com.example.chatapp.viewmodels.ChatFragmentviewModel
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory


class ChatFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var chatFragmentviewModel: ChatFragmentviewModel
    lateinit var sharedViewModel: SharedViewModel
    lateinit var progressBar: ProgressBar
    private lateinit var adapter: ChatAdapter
    var participantList = mutableListOf<String>()
    var userList = mutableListOf<UserWithID>()
    var userIdList = mutableListOf<String>()
    var chatList = mutableListOf<UserWithID>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.rvChats)
        progressBar = view.findViewById(R.id.chatPB)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        chatFragmentviewModel = ViewModelProvider(
            this,
            ChatFragmentViewModelfactory()
        )[ChatFragmentviewModel::class.java]
        adapter = ChatAdapter(requireContext(),chatList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListner(object : OnItemClickListener{
            override fun onItemClick(position: Int) {
                SharedPref.addString(Constants.COLUMN_PARTICIPANTS,chatList[position].userId)
                SharedPref.addString(Constants.COLUMN_URI,chatList[position].uri)
                SharedPref.addString(Constants.COLUMN_NAME,chatList[position].userName)
                sharedViewModel.setGotoIndividualChatStatus(true)
            }

        })
        readChats()
        //getAlluserDetails()
        observe()
        return view
    }

    private fun getAlluserDetails() {
        chatFragmentviewModel.readuserdetails()
    }

    private fun observe() {
        chatFragmentviewModel.chatStatus.observe(viewLifecycleOwner){
            //participantList.clear()
            progressBar.isVisible = false
            chatList.clear()
            for(i in it){
                chatList.add(i)
                adapter.notifyItemInserted(chatList.size - 1)
            }
        }
//        chatFragmentviewModel.userdetailsStatus.observe(viewLifecycleOwner){
//            chatList.clear()
//            for(i in it){
//                if(i.userId in participantList){
//                    chatList.add(i)
//                    adapter.notifyItemInserted(chatList.size - 1)
//                }
//            }
//        }
    }

    private fun readChats() {
        progressBar.isVisible = true
        chatFragmentviewModel.readchats()
    }
}