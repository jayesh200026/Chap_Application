package com.example.chatapp.ui.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.service.model.GroupDetails
import com.example.chatapp.ui.OnItemClickListener
import com.example.chatapp.ui.chats.creategroup.AddUserToGrpFragment
import com.example.chatapp.ui.chats.groupchat.GroupChatPageFragment
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.GroupVPViewModel
import com.example.chatapp.viewmodels.GroupVPViewModelFactory
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GroupFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    private lateinit var adapter: GroupAdapter
    lateinit var sharedViewModel: SharedViewModel
    lateinit var groupVpViewModel: GroupVPViewModel
    lateinit var addFab: FloatingActionButton
    var grpList = mutableListOf<GroupDetails>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.rvChats)
        progressBar = view.findViewById(R.id.chatPB)
        addFab = view.findViewById(R.id.group_fragment_floating_button)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = GroupAdapter(grpList)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListner(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                SharedPref.addString(Constants.GROUP_ID, grpList[position].groupId)
                SharedPref.addString(Constants.GROUP_NAME, grpList[position].groupName)
                requireActivity().supportFragmentManager.beginTransaction().add(
                    R.id.flFragment,
                    GroupChatPageFragment()
                )
                    .addToBackStack(null)
                    .commit()
            }
        })
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        groupVpViewModel = ViewModelProvider(
            this,
            GroupVPViewModelFactory()
        )[GroupVPViewModel::class.java]
        addFab.setOnClickListener {
            gotoSelectUserForGroupPage()
        }
        getgroups()
        observe()
        return view
    }

    private fun gotoSelectUserForGroupPage() {
        activity?.run {
            supportFragmentManager.beginTransaction()
                .add(R.id.flFragment, AddUserToGrpFragment()).addToBackStack(null).commit()
        }
    }

    private fun observe() {
        groupVpViewModel.grpListStatus.observe(viewLifecycleOwner) {
            grpList.clear()
            for (i in it) {
                grpList.add(i)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun getgroups() {
        groupVpViewModel.getGroups()
    }
}