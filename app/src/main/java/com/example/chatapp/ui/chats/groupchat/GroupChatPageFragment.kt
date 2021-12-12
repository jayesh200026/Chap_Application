package com.example.chatapp.ui.chats.groupchat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.example.chatapp.ui.chats.PreviewImageFragment
import com.example.chatapp.util.Constants
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
    val list = mutableListOf<GroupChat>()
    var groupId = SharedPref.get(Constants.GROUP_ID)
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
                groupChatViewModel.sendMessage(groupId, message, Constants.MESSAGE_TYPE_TEXT)
                chatMessage.setText("")
            }
        }
        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        getAllChatsOfGroup(groupId)
        observe()
        return view
    }

    private fun observe() {
        list.clear()
        groupChatViewModel.groupChatMessageStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                list.add(0, it)
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
            }
        }
        groupChatViewModel.uploadMessageImageStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                groupChatViewModel.sendMessage(groupId, it.toString(), Constants.MESSAGE_TYPE_IMAGE)
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
            val bundle = Bundle()
            bundle.putSerializable(Constants.SENDING_IMAGE_URI, imageUri)
            bundle.putString(Constants.COLUMN_PARTICIPANTS, participant)
            bundle.putString("IS_SINGLE","false")
            val fragment = PreviewImageFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}