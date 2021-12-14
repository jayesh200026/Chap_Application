package com.example.chatapp.ui.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.ui.OnItemClickListener
import com.example.chatapp.ui.chats.singlechat.IndividualChatFragment
import com.example.chatapp.ui.viewpager.ChatAdapter
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.DisplayAllUSerVM
import com.example.chatapp.viewmodels.DisplayAllUserVMFactory


class DisplayAllUserFragment : Fragment() {
    lateinit var recycleView: RecyclerView
    lateinit var adapter: ChatAdapter
    lateinit var displayAllUserVM: DisplayAllUSerVM
    lateinit var backBtn: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_all_user, container, false)
        recycleView = view.findViewById(R.id.allUsers)
        backBtn = view.findViewById(R.id.user_list_back_button)
        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        displayAllUserVM = ViewModelProvider(
            this,
            DisplayAllUserVMFactory()
        )[DisplayAllUSerVM::class.java]
        adapter = ChatAdapter(requireContext(), displayAllUserVM.userList)
        recycleView.layoutManager = LinearLayoutManager(requireContext())
        recycleView.setHasFixedSize(true)
        recycleView.adapter = adapter
        adapter.setOnItemClickListner(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                SharedPref.addString(
                    Constants.COLUMN_PARTICIPANTS,
                    displayAllUserVM.userList[position].uid
                )
                SharedPref.addString(
                    Constants.COLUMN_URI,
                    displayAllUserVM.userList[position].image
                )
                SharedPref.addString(
                    Constants.COLUMN_NAME,
                    displayAllUserVM.userList[position].name
                )
                SharedPref.addString(
                    Constants.PARTICIPANT_TOKEN,
                    displayAllUserVM.userList[position].token
                )
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.flFragment, IndividualChatFragment())
                    .addToBackStack(null)
                    .commit()
            }
        })
        displayAllUserVM.getUserListFromDb()
        observe()
        return view
    }

    private fun observe() {
        displayAllUserVM.getUserListStatus.observe(viewLifecycleOwner) {
            if (it) {
                adapter.notifyDataSetChanged()
            }
        }
    }
}