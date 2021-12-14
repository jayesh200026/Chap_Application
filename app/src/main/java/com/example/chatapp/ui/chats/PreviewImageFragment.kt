package com.example.chatapp.ui.chats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.service.model.UserIDToken
import com.example.chatapp.util.Constants
import com.example.chatapp.util.GroupParticipants
import com.example.chatapp.util.ImageUri
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.PreviewImageViewModel
import com.example.chatapp.viewmodels.PreviewimageVMFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PreviewImageFragment : Fragment() {
    lateinit var image: ImageView
    lateinit var send: FloatingActionButton
    lateinit var previewImageViewModel: PreviewImageViewModel
    lateinit var progresBar: ProgressBar
    //var grpUsers:GroupParticipants? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.image_preview_page, container, false)
        image = view.findViewById(R.id.previedImage)
        send = view.findViewById(R.id.chatsendBtn)
        progresBar = view.findViewById(R.id.previemPB)
        val imageUri = arguments?.getSerializable(Constants.SENDING_IMAGE_URI) as ImageUri
         val grpUsers = arguments?.getSerializable(Constants.PARTICIPANT_LIST) as GroupParticipants

        val list = mutableListOf<UserIDToken>()
        if( grpUsers.list != null) {
            for (i in grpUsers.list) {
                Log.d("user", i.toString())
                list.add(i)
            }
        }
        Log.d("list", list.toString())
        val participant = arguments?.getString(Constants.COLUMN_PARTICIPANTS)
        val isSingle = arguments?.getString("IS_SINGLE")
        previewImageViewModel = ViewModelProvider(
            this,
            PreviewimageVMFactory()
        )[PreviewImageViewModel::class.java]
        imageUri?.let {
            Glide.with(view)
                .load(it.uri)
                .into(image)
        }
        send.setOnClickListener {
            progresBar.isVisible = true
            if (participant != null) {
                if (isSingle == "true") {
                    previewImageViewModel.uploadMessageImage(imageUri.uri)
                } else if (isSingle == "false") {
                    previewImageViewModel.uploadMessageImageTogrp(imageUri.uri)
                }
            }
        }
        previewImageViewModel.uploadMessageImageStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                previewImageViewModel.sendMessage(
                    participant,
                    it.toString(),
                    Constants.MESSAGE_TYPE_IMAGE
                )
            }
        }
        previewImageViewModel.addingMewImageMessageStatus.observe(viewLifecycleOwner) {
            if (it) {
                progresBar.isVisible = false
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        previewImageViewModel.uploadGrpMessageImageStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                previewImageViewModel.sendGrpMessage(
                    participant,
                    it.toString(),
                    Constants.MESSAGE_TYPE_IMAGE, list
                )
            }
        }
        previewImageViewModel.addingMewGrpImageMessageStatus.observe(viewLifecycleOwner) {
            if (it) {
                //previewImageViewModel.sendGrpNotification(list)
                progresBar.isVisible = false
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        return view
    }

}