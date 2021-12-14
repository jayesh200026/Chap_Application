package com.example.chatapp.ui.chats.creategroup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.util.Constants
import com.example.chatapp.viewmodels.AddUserToGrpVM
import com.example.chatapp.viewmodels.AddUserToGrpVMFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AddUserToGrpFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: GroupChatUserAdapter
    lateinit var fab: FloatingActionButton
    lateinit var adduserToGrpVM: AddUserToGrpVM
    lateinit var backBtn: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_user_to_grp_fragment, container, false)
        fab = view.findViewById(R.id.seletUserFab)
        adduserToGrpVM = ViewModelProvider(
            this,
            AddUserToGrpVMFactory()
        )[AddUserToGrpVM::class.java]
        recyclerView = view.findViewById(R.id.user_list_recycler_view)
        backBtn = view.findViewById(R.id.user_list_back_button)
        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        adapter = GroupChatUserAdapter(adduserToGrpVM.userList, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        adduserToGrpVM.getUserListFromDb()
        fab.setOnClickListener {
            gotoSettingGrpNamePage()
        }
        observe()
        return view
    }

    private fun gotoSettingGrpNamePage() {
        val selectedList = adapter.getSelectedList()
        if (selectedList.size != 0) {
            val bundle = Bundle()
            bundle.putStringArrayList(
                Constants.COLUMN_PARTICIPANTS,
                selectedList as ArrayList<String>
            )
            val grpNameFragment = GroupNameFragment()
            grpNameFragment.arguments = bundle
            activity?.run {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.flFragment, grpNameFragment)
                    .commit()
            }
        }
    }

    private fun observe() {
        adduserToGrpVM.getUserListStatus.observe(viewLifecycleOwner) {
            if (it) {
                adapter.notifyDataSetChanged()
            }
        }
    }
}