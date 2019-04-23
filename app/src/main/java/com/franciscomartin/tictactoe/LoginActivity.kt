package com.franciscomartin.tictactoe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        changeLoginFormVisibility(true)


        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            changeLoginFormVisibility(false)

        }
    }

    private fun changeLoginFormVisibility(showForm: Boolean){
        if(showForm){
            progressBarLogin.visibility = View.GONE
            formLogin.visibility = View.VISIBLE
        } else {
            progressBarLogin.visibility = View.VISIBLE
            formLogin.visibility = View.GONE
        }

    }
}
