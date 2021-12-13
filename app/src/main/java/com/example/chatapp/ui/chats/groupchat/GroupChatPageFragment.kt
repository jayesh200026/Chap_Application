package com.example.chatapp.ui.chats.groupchat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.GroupChat
import com.example.chatapp.service.model.UserIDToken
import com.example.chatapp.service.model.UserWithToken
import com.example.chatapp.ui.chats.PreviewImageFragment
import com.example.chatapp.util.Constants
import com.example.chatapp.util.GroupParticipants
import com.example.chatapp.util.ImageUri
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.GroupChatViewModel
import com.example.chatapp.viewmodels.GroupChatViewModelFactory
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.mikhaellopez.circularimageview.CircularImageView


class GroupChatPageFragment : Fragment() {
    lateinit var sharedViewModel: SharedViewModel
    lateinit var groupChatViewModel: GroupChatViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: GroupChatAdapter
    lateinit var groupName: TextView
    lateinit var profilePic: CircularImageView
    lateinit var backBtn: ImageView
    lateinit var sendBtn: ImageButton
    lateinit var chatMessage: EditText
    lateinit var imageMsg: ImageView
    var offset: Long=Long.MAX_VALUE
    val list = mutableListOf<GroupChat>()
    var participantList = mutableListOf<UserIDToken>()
    var groupId = SharedPref.get(Constants.GROUP_ID)
    var currentItem: Int = 0
    var totalCount: Int = 0
    var scrolledOutItems: Int = 0
    var textMessage: String = ""
    var isLoading: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group, container, false)
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
        imageMsg = view.findViewById(R.id.sendPhoto)
        imageMsg.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                Constants.RC_SELECT_IMAGE
            )
        }
        val layoutMangaer = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutMangaer
        layoutMangaer.reverseLayout = true
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
            if (message.isNotEmpty()) {
                textMessage = message
                groupChatViewModel.sendMessage(groupId, message, Constants.MESSAGE_TYPE_TEXT)
                chatMessage.setText("")
            }
        }
        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        getAllChatsOfGroup(groupId)
        getAllPartipantsDetails(groupId)
        observe()
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItem = (recyclerView.layoutManager as LinearLayoutManager).childCount
                totalCount = (recyclerView.layoutManager as LinearLayoutManager).itemCount
                scrolledOutItems = (recyclerView.layoutManager as LinearLayoutManager)
                    .findFirstVisibleItemPosition()
                if (!isLoading) {
                    if ((currentItem + scrolledOutItems) == totalCount && scrolledOutItems >= 0) {
                        isLoading = true
                        if (offset != 0L) {
                            Log.d("pagination", "scrolled")
                            loadNextTenGrpChats()
                        }
                    }
                }
            }
        })
        return view
    }

    private fun getAllPartipantsDetails(groupId: String?) {
        groupChatViewModel.getParticipantsDetails(groupId)
    }

    private fun loadNextTenGrpChats() {
        groupChatViewModel.loadNextTenGrpChats(groupId,offset)
    }

    private fun observe() {
        groupChatViewModel.groupChatMessageStatus.observe(viewLifecycleOwner) {
            //isLoading = false
            if (it != null) {
                list.add(0, it)
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
            }
            offset = list[list.size-1].sentTime
        }
        groupChatViewModel.uploadMessageImageStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                groupChatViewModel.sendMessage(groupId, it.toString(), Constants.MESSAGE_TYPE_IMAGE)
            }
        }
        groupChatViewModel.nextTenMessagesStatus.observe(viewLifecycleOwner){
            isLoading = false
            if(it.size != 0){
                for(i in it){
                    list.add(i)
                    offset = i.sentTime
                    adapter.notifyItemInserted(list.size - 1)
                }
            }
            else{
                offset = 0L
            }
        }
        groupChatViewModel.participantsDetails.observe(viewLifecycleOwner){
            participantList.addAll(it)
        }
        groupChatViewModel.sendNewMessageStatus.observe(viewLifecycleOwner){
            val grpName = SharedPref.get(Constants.GROUP_NAME)
            if(it && grpName != null){
                Log.d("participant list",participantList.toString())
                groupChatViewModel.sendPushNotification(grpName,participantList,textMessage)
            }
        }

    }

    private fun getAllChatsOfGroup(groupId: String?) {
        groupChatViewModel.getAllChats(groupId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            val participant = groupId
            val selectedImagePath = data.data
            val imageUri = ImageUri(selectedImagePath)
            val grpUsers = GroupParticipants(participantList)
            Log.d("qwe",participantList.size.toString())
            val bundle = Bundle()
            bundle.putSerializable(Constants.SENDING_IMAGE_URI, imageUri)
            bundle.putString(Constants.COLUMN_PARTICIPANTS, participant)
            bundle.putString("IS_SINGLE","false")
            bundle.putSerializable(Constants.PARTICIPANT_LIST,grpUsers)
            val fragment = PreviewImageFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}