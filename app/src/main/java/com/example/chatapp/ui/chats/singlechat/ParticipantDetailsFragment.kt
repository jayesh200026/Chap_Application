package com.example.chatapp.ui.chats.singlechat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.util.Constants
import com.example.chatapp.viewmodels.ParticipantDetailVm
import com.example.chatapp.viewmodels.ParticipantDetailsVMFactory

class ParticipantDetailsFragment : Fragment() {
    lateinit var backBtn: ImageView
    lateinit var profile: ImageView
    lateinit var name: TextView
    lateinit var status: TextView
    lateinit var participantDetailVm: ParticipantDetailVm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_participant_details, container, false)
        participantDetailVm = ViewModelProvider(
            this,
            ParticipantDetailsVMFactory()
        )[ParticipantDetailVm::class.java]
        val participantId = arguments?.getString(Constants.COLUMN_PARTICIPANTS)
        backBtn = view.findViewById(R.id.BackBtn)
        profile = view.findViewById(R.id.profilePhoto)
        name = view.findViewById(R.id.name_tv)
        status = view.findViewById(R.id.status_tv)
        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        participantDetailVm.getParticipantDetails(participantId)
        participantDetailVm.getParticipantDetailsStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                name.text = it.userName
                status.text = it.status
                if (it.uri != "") {
                    Glide.with(requireContext())
                        .load(it.uri)
                        .centerInside()
                        .into(profile)
                }
            }
        }
        return view
    }
}