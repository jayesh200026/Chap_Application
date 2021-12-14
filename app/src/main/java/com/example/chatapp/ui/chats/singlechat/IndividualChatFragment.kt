package com.example.chatapp.ui.chats.singlechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.service.model.*
import com.example.chatapp.ui.chats.PreviewImageFragment
import com.example.chatapp.util.Constants
import com.example.chatapp.util.GroupParticipants
import com.example.chatapp.util.ImageUri
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.IndividualChatViewModel
import com.example.chatapp.viewmodels.IndividualChatviewModelFactory
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.mikhaellopez.circularimageview.CircularImageView


class IndividualChatFragment : Fragment(), View.OnClickListener {
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
    lateinit var currentUser: User
    var list = mutableListOf<Chats>()
    var currentItem: Int = 0
    var totalCount: Int = 0
    var scrolledOutItems: Int = 0
    var offset: Long = Long.MAX_VALUE
    var isLoading: Boolean = false
    var textMessage = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_individual_chat, container, false)
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
        individualChatViewModel.getCurrentUser()
        profilePic.setOnClickListener(this)
        username.setOnClickListener(this)
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
                textMessage = message
                individualChatViewModel.sendMessage(
                    participant,
                    message,
                    Constants.MESSAGE_TYPE_TEXT
                )
                chatMessage.setText("")
            }
        }
        backBtn.setOnClickListener {
//            sharedViewModel.setGotoHomePageStatus(true)
            requireActivity().supportFragmentManager.popBackStack()
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
        layoutMangaer.reverseLayout = true
        recyclerView.layoutManager = layoutMangaer
        adapter = IndvlChatAdapter(list)
        recyclerView.adapter = adapter
        subscribeToListener()
        recycleViewScrollListener()
        observe()
        return view
    }

    private fun recycleViewScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                            loadNextTenChats()
                        }
                    }
                }
            }
        })
    }

    private fun loadNextTenChats() {
        val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
        individualChatViewModel.loadNextTenChats(offset, participant)
    }

    private fun subscribeToListener() {
        val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
        individualChatViewModel.subscribe(participant)
    }

    private fun observe() {
        individualChatViewModel.chatStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d("pagination", "${it.messageType}")
                list.add(0, it)
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
            }
            offset = list[list.size - 1].sentTime
        }
        individualChatViewModel.uploadMessageImageStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                textMessage = ""
                val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
                individualChatViewModel.sendMessage(
                    participant,
                    it.toString(),
                    Constants.MESSAGE_TYPE_IMAGE
                )
            }
        }
        individualChatViewModel.nextchatsStatus.observe(viewLifecycleOwner) {
            isLoading = false
            Log.d("pagination", it.size.toString())
            if (it.size != 0) {
                for (i in it) {
                    Log.d("pagination", i.toString())
                    offset = i.sentTime
                    list.add(i)
                    adapter.notifyItemInserted(list.size - 1)
                }
            } else {
                offset = 0L
            }
        }
        individualChatViewModel.sendMessageStatus.observe(viewLifecycleOwner) {
            if (it) {
                val token = SharedPref.get(Constants.PARTICIPANT_TOKEN)
                individualChatViewModel.sendPushNotification(
                    token,
                    currentUser.userName,
                    textMessage
                )
            }
        }
        individualChatViewModel.currentUserStatus.observe(viewLifecycleOwner) {
            currentUser = it
            SharedPref.addString(Constants.CURRENT_USER_USERNAME, it.userName)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("image", "Inside onActivityresult")
        if (requestCode == Constants.RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
            val selectedImagePath = data.data
            val imageUri = ImageUri(selectedImagePath)
            val bundle = Bundle()
            //val list = mutableListOf<UserIDToken>()
            val grpUsers = GroupParticipants(null)
            bundle.putSerializable(Constants.PARTICIPANT_LIST, grpUsers)
            bundle.putSerializable(Constants.SENDING_IMAGE_URI, imageUri)
            bundle.putString(Constants.COLUMN_PARTICIPANTS, participant)
            bundle.putString("IS_SINGLE", "true")
            val fragment = PreviewImageFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.chatProfilePic, R.id.participantName -> {
                val participant = SharedPref.get(Constants.COLUMN_PARTICIPANTS)
                val bundle = Bundle()

                bundle.putString(Constants.COLUMN_PARTICIPANTS, participant)
                val participantPreviewFragment = ParticipantDetailsFragment()
                participantPreviewFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.flFragment, participantPreviewFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}