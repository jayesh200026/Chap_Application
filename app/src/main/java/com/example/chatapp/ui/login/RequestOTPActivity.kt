package com.example.chatapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.chatapp.R
import com.example.chatapp.util.Constants
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class RequestOTPActivity : AppCompatActivity() {
    lateinit var requestOtp: Button
    lateinit var phoneNumber: EditText
    lateinit var progressBar: ProgressBar
    lateinit var mAuth: FirebaseAuth
    lateinit var mCallback:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_otpactivity)
        mAuth = FirebaseAuth.getInstance()
        requestOtp = findViewById(R.id.requestOtpbtn)
        phoneNumber = findViewById(R.id.phoneNumber)
        progressBar = findViewById(R.id.progressBar)
        requestOtp.setOnClickListener{
            if(phoneNumber.text.toString().trim().isEmpty() ||
                phoneNumber.text.toString().trim().length != 10 ){
                Toast.makeText(this,"Check number",Toast.LENGTH_SHORT).show()
            }else{
                sendOTP()
            }
        }
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
                Toast.makeText(this@RequestOTPActivity,"OOps",Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                progressBar.isVisible = false
                requestOtp.isVisible = true
                val intent = Intent(this@RequestOTPActivity,VerifyOTPActivity::class.java)
                intent.putExtra(Constants.VERIFICATION,verificationId)
                startActivity(intent)
            }
        }
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91"+phoneNumber.text.toString().trim())       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}