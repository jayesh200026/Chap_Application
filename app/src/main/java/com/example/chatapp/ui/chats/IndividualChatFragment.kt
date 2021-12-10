package com.example.chatapp.ui.chats

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
    lateinit var sharedViewModel: SharedViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: IndvlChatAdapter
    lateinit var username: TextView
    lateinit var profilePic: CircularImageView
    lateinit var backBtn: ImageView
    lateinit var sendBtn: ImageButton
    lateinit var chatMessage: EditText
    lateinit var getImage: ImageView
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
        chatMessage = view.findViewById(R.id.chatET)
        sendBtn = view.findViewById(R.id.chatsendBtn)
        getImage = view.findViewById(R.id.sendPhoto)
        getImage.setOnClickListener {
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
        sendBtn.setOnClickListener {
            val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
            val message = chatMessage.text.toString()
            if (message.isNotEmpty()) {
                individualChatViewModel.sendMessage(
                    participant,
                    message,
                    Constants.MESSAGE_TYPE_TEXT
                )
                chatMessage.setText("")
            }
        }
        backBtn.setOnClickListener {
            sharedViewModel.setGotoHomePageStatus(true)
        }
        val name = SharedPref.get(Constants.COLUMN_NAME)
        username.text = name
        val pic = SharedPref.get(Constants.COLUMN_URI)
        pic?.let {
            if (it.isNotEmpty()) {
                Glide.with(requireContext())
                    .load(it)
                    .centerInside()
                    .into(profilePic)
            }
        }
        val layoutMangaer = LinearLayoutManager(requireContext())
        //layoutMangaer.stackFromEnd=  true
        layoutMangaer.reverseLayout = true
        recyclerView.layoutManager = layoutMangaer
        adapter = IndvlChatAdapter(list)
        recyclerView.adapter = adapter
        val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
        subscribeToListener(participant)
        observe()
        return view
    }

    private fun subscribeToListener(participant: String?) {
        individualChatViewModel.subscribe(participant)

    }

    private fun observe() {
        individualChatViewModel.chatStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                list.add(0, it)
                //adapter.notifyItemRangeInserted(list.size-1,1)
                //adapter.notifyDataSetChanged()
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
                //adapter.notifyItemInserted(list.size-1)
            }
        }
        individualChatViewModel.uploadMessageImageStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
                individualChatViewModel.sendMessage(
                    participant,
                    it.toString(),
                    Constants.MESSAGE_TYPE_IMAGE
                )
            }
        }
    }

    private fun getAllChatsBetweenTwoUsers() {
        val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
        individualChatViewModel.getAllChats(participant)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("image", "Inside onActivityresult")
        if (requestCode == Constants.RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            val selectedImagePath = data.data
            individualChatViewModel.uploadMessageImage(selectedImagePath)
        }
    }
}