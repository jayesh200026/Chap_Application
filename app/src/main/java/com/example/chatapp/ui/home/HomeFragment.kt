package com.example.chatapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.viewmodels.HomeViewModel
import com.example.chatapp.viewmodels.HomeViewModelFactory
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory

class HomeFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var options: ImageView
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory()
        )[HomeViewModel::class.java]
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        observe()
        options = view.findViewById(R.id.options)
        options.setOnClickListener {
            showMenu(view)
        }
        return view
    }

    private fun observe() {
        homeViewModel.logoutStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGotoRequestOtpStatus(true)
            }
        }
    }

    private fun showMenu(view: View?) {
        val popupMenu = PopupMenu(requireContext(), options)
        popupMenu.menuInflater.inflate(R.menu.usermenu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.settings -> {

            }
            R.id.profile -> {
                sharedViewModel.setGotoEditProfilePage(true)
            }
            R.id.logoutMenu -> {
                homeViewModel.logout()
            }
        }
        return true
    }
}