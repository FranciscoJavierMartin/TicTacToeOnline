package com.franciscomartin.tictactoe.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.franciscomartin.tictactoe.R
import com.franciscomartin.tictactoe.goToActivity
import com.franciscomartin.tictactoe.commons.Constants
import com.franciscomartin.tictactoe.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (name.isEmpty()) {
                editTextName.error = getString(R.string.register_name_error_empty)
            } else if (name.length < 3 || name.length > 15) {
                editTextName.error = getString(R.string.register_name_error_length)
            } else if (email.isEmpty()) {
                // TODO: Replace with a valid regexp to validate an email
                editTextEmail.error = getString(R.string.register_email_error_empty)
            } else if (password.isEmpty()) {
                editTextPassword.error = getString(R.string.register_password_error_empty)
            } else if (password.length < 6){
                editTextPassword.error = getString(R.string.register_password_error_length)
            } else {
                // TODO: Realize authentication on Firebase auth
                createUser(name, email, password)
            }
        }
    }

    private fun createUser(name:String, email:String, password: String){

        this.changeRegisterFormVisibility(false)

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    val user = firebaseAuth.currentUser
                    updateUI(user, name)
                } else {
                    Toast.makeText(this, R.string.register_error_on_register, Toast.LENGTH_SHORT)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?, name: String){
        if(user!= null){
            firestore.collection(Constants.USERS_COLLECTION)
                .document(user.uid)
                .set(User(name,0,0))
                .addOnSuccessListener {
                    changeRegisterFormVisibility(true)
                    finish()
                    goToActivity<SearchGameActivity> ()
                }

        } else {
            changeRegisterFormVisibility(true)
            editTextPassword.error = getString(R.string.register_error_on_register)
        }
    }

    private fun changeRegisterFormVisibility(showForm: Boolean){
        if(showForm){
            progressBarRegister.visibility = View.GONE
            formRegister.visibility = View.VISIBLE
        } else {
            progressBarRegister.visibility = View.VISIBLE
            formRegister.visibility = View.GONE
        }

    }
}
