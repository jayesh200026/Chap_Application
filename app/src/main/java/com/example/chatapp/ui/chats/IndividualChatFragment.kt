package com.example.chatapp.ui.chats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.service.model.Chats
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.IndividualChatViewModel
import com.example.chatapp.viewmodels.IndividualChatviewModelFactory
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.mikhaellopez.circularimageview.CircularImageView


class IndividualChatFragment : Fragment() {
    lateinit var individualChatViewModel: IndividualChatViewModel
    lateinit var sharedViewModel:SharedViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: IndvlChatAdapter
    lateinit var username: TextView
    lateinit var profilePic: CircularImageView
    lateinit var backBtn: ImageView
    var list = mutableListOf<Chats>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_individual_chat, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        individualChatViewModel = ViewModelProvider(
            this,
            IndividualChatviewModelFactory()
        )[IndividualChatViewModel::class.java]
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        recyclerView = view.findViewById(R.id.rvIndlChat)
        username = view.findViewById(R.id.participantName)
        profilePic = view.findViewById(R.id.chatProfilePic)
        backBtn = view.findViewById(R.id.chatBackBtn)
        backBtn.setOnClickListener {
            sharedViewModel.setGotoHomePageStatus(true)
        }
        val name = SharedPref.get(Constants.COLUMN_NAME)
        username.text = name
        val pic = SharedPref.get(Constants.COLUMN_URI)
        pic?.let {
            if(it.isNotEmpty()){
                Glide.with(requireContext())
                    .load(it)
                    .centerInside()
                    .into(profilePic)
            }
        }
        val layoutMangaer = LinearLayoutManager(requireContext())
        layoutMangaer.stackFromEnd=  true
        recyclerView.layoutManager = layoutMangaer
        adapter = IndvlChatAdapter(list)
        recyclerView.adapter = adapter
        getAllChatsBetweenTwoUsers()
        observe()
        return view
    }

    private fun observe() {
        val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
        individualChatViewModel.readAllChatsStatsus.observe(viewLifecycleOwner){
            list.clear()
            for(i in 0..it.size-1){
                Log.d("chats",it[i].participants.toString())
                Log.d("chats","list is"+it[i].messages.toString())
                Log.d("chats","participants="+participant)
                if(participant in it[i].participants){

                    for(chats in it[i].messages){
                        Log.d("chatsid",chats.messageId)
                        list.add(chats)
                        adapter.notifyItemInserted(list.size - 1)
                    }
                }
            }
//            for(chats in it){
//                if(chats.senderId.equals(participant) || chats.receiverId.equals(participant)){
//                    list.add(chats)
//                    adapter.notifyItemInserted(list.size - 1)
//                }
//
//            }
        }
    }

    private fun getAllChatsBetweenTwoUsers() {
        val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
        individualChatViewModel.getAllChats(participant)
    }

}