package com.example.chatapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.util.Constants
import com.example.chatapp.viewmodels.VerifyOtpViewModel
import com.example.chatapp.viewmodels.VerifyOtpViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class VerifyOTPActivity : AppCompatActivity() {

    lateinit var verifyOtp: Button
    lateinit var otp: EditText
    lateinit var verifyOtpViewModel: VerifyOtpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otpactivity)
        verifyOtp = findViewById(R.id.verifyOtpbtn)
        otp = findViewById(R.id.etOtp)
        verifyOtpViewModel = ViewModelProvider(
            this,
            VerifyOtpViewModelFactory()
        )[VerifyOtpViewModel::class.java]
        val verificationId = intent.getStringExtra(Constants.VERIFICATION)
        verifyOtp.setOnClickListener {
            if (otp.text.toString().trim().isEmpty() || otp.text.toString().trim().length != 6) {
                Toast.makeText(this, "enter valid otp", Toast.LENGTH_SHORT).show()
            } else {
                if (verificationId != null) {
                    val code = otp.text.toString().trim()
                    val credential = PhoneAuthProvider.getCredential(verificationId, code)
//                    verifyOtpViewModel.signInWithCredentails(credential)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val intent =
                                    Intent(this@VerifyOTPActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.putExtra(Constants.LOGIN, "true")
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "enter valid otp", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}