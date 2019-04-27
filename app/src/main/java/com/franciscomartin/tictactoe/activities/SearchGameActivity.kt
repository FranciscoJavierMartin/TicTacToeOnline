package com.franciscomartin.tictactoe.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.franciscomartin.tictactoe.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search_game.*

class SearchGameActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user: FirebaseUser? = firebaseAuth.currentUser
    private val uid: String? = user?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_game)

        this.changeMenuVisibility(true)

        buttonPlay.setOnClickListener{
            this.changeMenuVisibility(false)
        }

        buttonRanking.setOnClickListener {

        }


    }

    override fun onResume() {
        super.onResume()
        this.changeMenuVisibility(true)
    }

    private fun changeMenuVisibility(showMenu: Boolean){
        if(showMenu){
            layoutProgressBar.visibility = View.GONE
            layoutGameMenu.visibility = View.VISIBLE
        } else {
            layoutProgressBar.visibility = View.VISIBLE
            layoutGameMenu.visibility = View.GONE
        }
    }

}
