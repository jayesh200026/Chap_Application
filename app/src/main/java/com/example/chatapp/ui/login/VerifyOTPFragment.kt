package com.example.chatapp.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.example.chatapp.viewmodels.VerifyOtpViewModel
import com.example.chatapp.viewmodels.VerifyOtpViewModelFactory
import com.google.firebase.auth.PhoneAuthProvider

class VerifyOTPFragment : Fragment() {
    lateinit var verifyOtp: Button
    lateinit var otp: EditText
    lateinit var verifyOtpViewModel: VerifyOtpViewModel
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_verify_otpfragment, container, false)
        verifyOtp = view.findViewById(R.id.verifyOtpbtn)
        otp = view.findViewById(R.id.etOtp)
        verifyOtpViewModel = ViewModelProvider(
            this,
            VerifyOtpViewModelFactory()
        )[VerifyOtpViewModel::class.java]
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        val verificationId = SharedPref.get(Constants.VERIFICATION)
        verifyOtp.setOnClickListener {
            if (otp.text.toString().trim().isEmpty() || otp.text.toString().trim().length != 6) {
                Toast.makeText(requireContext(), getString(R.string.invalidOTP), Toast.LENGTH_SHORT).show()
            } else {
                if (verificationId != null) {
                    val code = otp.text.toString().trim()
                    val credential = PhoneAuthProvider.getCredential(verificationId, code)
                    verifyOtpViewModel.signInWithCredentails(credential)
                }
            }
        }
        observe()
        return view
    }

    private fun observe() {
        verifyOtpViewModel.signInWithCrentialStatus.observe(viewLifecycleOwner) {
            if (it == Constants.NEW_USER) {
                sharedViewModel.setGotoSetProfilePage(true)
            } else if (it == Constants.EXISTING_USER) {
                val token = SharedPref.get(Constants.DEVICE_TOKEN)
                verifyOtpViewModel.updateDeviceToken(token)
            }
        }
        verifyOtpViewModel.updateDeviceTokenStatus.observe(viewLifecycleOwner) {
            if (it) {
                sharedViewModel.setGotoHomePageStatus(true)
            }
        }
    }
}