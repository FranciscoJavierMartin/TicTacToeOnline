package com.franciscomartin.tictactoe.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.franciscomartin.tictactoe.R
import com.franciscomartin.tictactoe.goToActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        changeLoginFormVisibility(true)


        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (email.isEmpty()) {
                // TODO: Replace with a valid regexp to validate an email
                editTextEmail.error = getString(R.string.login_email_error_empty)
            } else if (password.isEmpty()) {
                editTextPassword.error = getString(R.string.login_password_error_empty)
            } else if (password.length < 6){
                editTextPassword.error = getString(R.string.login_password_error_length)
            } else {
                // TODO: Realize authentication on Firebase auth
                changeLoginFormVisibility(false)
                loginUser(email, password)
            }


        }

        buttonRegister.setOnClickListener {
            goToActivity<RegisterActivity>()
        }

    }

    override fun onStart() {
        super.onStart()
        updateUI(firebaseAuth.currentUser, false)
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

    private fun loginUser(email: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    updateUI(firebaseAuth.currentUser)
                } else {
                    Log.w("TAG", "signInError: ", it.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?, tryLogin: Boolean = true){
        if(user!= null){
            goToActivity<SearchGameActivity> ()
        } else {
            changeLoginFormVisibility(true)
            if(tryLogin){
                editTextPassword.error = getString(R.string.register_error_on_register)
            }
        }
    }
}
