package com.example.chatapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity :AppCompatActivity(){
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        Login_button.setOnClickListener {
            val email =  Email_edittext.text.toString()
            val password =Password_edittext.text.toString()
            Log.d("Login", "Attemped Login: $email $password")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
//                .addOnCompleteListener()

        }
        back_to_home.setOnClickListener {
            finish()
        }



    }

}
