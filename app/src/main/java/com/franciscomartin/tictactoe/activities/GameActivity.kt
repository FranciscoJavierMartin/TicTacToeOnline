package com.franciscomartin.tictactoe.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.franciscomartin.tictactoe.R
import com.franciscomartin.tictactoe.commons.Constants
import com.franciscomartin.tictactoe.models.Game
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user: FirebaseUser = firebaseAuth.currentUser!!
    private val uid: String = user.uid
    private lateinit var gameID: String
    private lateinit var playerOneName: String
    private lateinit var playerTwoName: String
    private lateinit var game: Game
    private var gameListener: ListenerRegistration? = null

    private val cells = arrayOf(
        imageView0,
        imageView1,
        imageView2,
        imageView3,
        imageView4,
        imageView5,
        imageView6,
        imageView7,
        imageView8
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameID = intent.extras.getString(Constants.SEARCH_GAME_EXTRA_GAMEID)

    }

    override fun onStart() {
        super.onStart()
        initGameListener()
    }

    override fun onStop() {
        if(gameListener != null){
            gameListener!!.remove()
        }
        super.onStop()
    }

    fun selectedCell(view: View){
        if(!game.winnerID.isEmpty()){
            Toast.makeText(this, R.string.game_game_has_end, Toast.LENGTH_LONG)
        } else {

            if(game.player1Turn && game.player1ID == uid) {
                updateGame(view.tag.toString())
            } else if(!game.player1Turn && game.player2ID == uid){
                updateGame(view.tag.toString())
            } else {
                Toast.makeText(this, R.string.game_it_not_your_turn, Toast.LENGTH_LONG)
            }
        }
    }

    private fun initGameListener(){
        gameListener = firestore.collection(Constants.GAMES_COLLECTION)
            .document(gameID)
            .addSnapshotListener(this){ snapshot, firebaseFirestoreException ->

                if(firebaseFirestoreException != null){
                    Toast.makeText(this, R.string.game_error_on_get_data, Toast.LENGTH_LONG)
                } else {

                    if(snapshot != null){
                        val dataFromServer: Boolean = snapshot.metadata.hasPendingWrites()

                        if(snapshot.exists() && dataFromServer){
                            game = snapshot.toObject(Game::class.java)!!

                            if(playerOneName.isEmpty() || playerTwoName.isEmpty()){
                                getPlayerNames()
                            }

                        }

                    }

                }

            }
    }

    private fun getPlayerNames(){
        firestore.collection(Constants.USERS_COLLECTION)
            .document(game.player1ID)
            .get()
            .addOnSuccessListener(this){
                playerOneName = it.get(Constants.USERS_FIELD_NAME).toString()
                textViewPlayer1.text = playerOneName
            }

        firestore.collection(Constants.USERS_COLLECTION)
            .document(game.player2ID)
            .get()
            .addOnSuccessListener(this){
                playerTwoName = it.get(Constants.USERS_FIELD_NAME).toString()
                textViewPlayer2.text = playerTwoName
            }
    }

    private fun updateGame(cellNumber: String){
        val cellPosition = Integer.parseInt(cellNumber)

        if(game.selectedCells.get(cellPosition) != 0){
            Toast.makeText(this, R.string.game_select_an_empty_cell, Toast.LENGTH_LONG)
        } else {
            if(game.player1Turn){
                this.cells[cellPosition].setImageResource(R.drawable.ic_player_one)
                game.selectedCells[cellPosition] =  1
            } else {
                this.cells[cellPosition].setImageResource(R.drawable.ic_player_two)
                game.selectedCells[cellPosition] =  2
            }
        }

    }

}
