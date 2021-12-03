package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatapp.ui.home.HomeFragment
import com.example.chatapp.ui.login.RequestOTPActivity
import com.example.chatapp.util.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginStatus = intent.getStringExtra(Constants.LOGIN)
        if(loginStatus.equals("true")){
            gotoHomePage()
        }
        else {
            val intent = Intent(this@MainActivity, RequestOTPActivity::class.java)
            startActivity(intent)
        }
    }

    private fun gotoHomePage() {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,HomeFragment())
            commit()
        }
    }
}