package com.example.chatapp.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.chatapp.R
import com.example.chatapp.service.model.Chat
import com.example.chatapp.viewmodels.HomeViewModel
import com.example.chatapp.viewmodels.HomeViewModelFactory
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var viewPager: ViewPager2
    lateinit var fragmentAdapter: FragmentAdapter
    private lateinit var tablayout: com.google.android.material.tabs.TabLayout
    var list = mutableListOf<Chat>()

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
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        tablayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewpager)
        initializetabLayout()
        observe()
        return view
    }

    private fun initializetabLayout() {
        var fragmentManager = requireActivity().supportFragmentManager

        fragmentAdapter = FragmentAdapter(fragmentManager, requireActivity().lifecycle)
        viewPager.adapter = fragmentAdapter
        tablayout.addTab(tablayout.newTab().setText("CHATS"))
        tablayout.addTab(tablayout.newTab().setText("GROUPS"))
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tablayout.selectTab(tablayout.getTabAt(position))
            }
        })
    }

    private fun observe() {
        homeViewModel.logoutStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGotoRequestOtpStatus(true)
            }
        }
    }
}