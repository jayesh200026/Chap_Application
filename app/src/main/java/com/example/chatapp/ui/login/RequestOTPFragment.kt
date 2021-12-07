package com.example.chatapp.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.R
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.viewmodels.SharedViewModel
import com.example.chatapp.viewmodels.SharedViewModelFactory
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class RequestOTPFragment : Fragment() {
    lateinit var requestOtp: Button
    lateinit var phoneNumber: EditText
    lateinit var progressBar: ProgressBar
    lateinit var mAuth: FirebaseAuth
    lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var sharedViewModel: SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request_otpfragment, container, false)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()
        requestOtp = view.findViewById(R.id.requestOtpbtn)
        phoneNumber = view.findViewById(R.id.phoneNumber)
        progressBar = view.findViewById(R.id.progressBar)
        requestOtp.setOnClickListener{
            if(phoneNumber.text.toString().trim().isEmpty() ||
                phoneNumber.text.toString().trim().length != 10 ){
                Toast.makeText(requireContext(),"Check number", Toast.LENGTH_SHORT).show()
            }else{
                sendOTP()
            }
        }
        return view
    }

    private fun sendOTP() {
        progressBar.isVisible = true
        requestOtp.isVisible = false
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                //TODO
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressBar.isVisible = false
                requestOtp.isVisible = true
                Toast.makeText(requireContext(),"OOps",Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                progressBar.isVisible = false
                requestOtp.isVisible = true
                sharedViewModel.setGotoVerifyotpPageStatus(true)
                SharedPref.addString(Constants.VERIFICATION,verificationId)
            }
        }
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91"+phoneNumber.text.toString().trim())       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

}