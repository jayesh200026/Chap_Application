package com.example.chatapp.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.service.model.User
import com.example.chatapp.viewmodels.AddUserDetailViewModel
import com.example.chatapp.viewmodels.AddUserDetailViewModelFactory
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.mikhaellopez.circularimageview.CircularImageView


class AddUserDetailFragment : Fragment() {
    lateinit var saveBtn: Button
    lateinit var userName: EditText
    lateinit var userStatus: EditText
    lateinit var profile: CircularImageView
    lateinit var getImage: ActivityResultLauncher<String>
    lateinit var addUserDetailViewModel: AddUserDetailViewModel
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_user_detail, container, false)
        saveBtn = view.findViewById(R.id.saveDetailsBtn)
        userName = view.findViewById(R.id.setUsername)
        userStatus = view.findViewById(R.id.setStatus)
        profile = view.findViewById(R.id.setuserProfile)
        addUserDetailViewModel = ViewModelProvider(
            this,
            AddUserDetailViewModelFactory()
        )[AddUserDetailViewModel::class.java]
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                if (it != null) {
                    profile.setImageURI(it)
                    addUserDetailViewModel.addProfilePic(it)
                }
            }
        )
        profile.setOnClickListener {
            getImage.launch("image/*")
        }
        saveBtn.setOnClickListener {
            if (userName.text.toString().isNotEmpty() && userStatus.text.toString().isNotEmpty()) {
                val user = User(userName.text.toString(), userStatus.text.toString())
                addUserDetailViewModel.addUserDetails(user)
            }
        }
        observe()
        return view
    }

    private fun observe() {
        addUserDetailViewModel.addUserDetailsStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            }
            else{
                Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

}