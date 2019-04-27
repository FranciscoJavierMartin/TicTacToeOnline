package com.franciscomartin.tictactoe.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.franciscomartin.tictactoe.R
import com.franciscomartin.tictactoe.commons.Constants
import com.franciscomartin.tictactoe.goToActivity
import com.franciscomartin.tictactoe.models.Game
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_search_game.*

class SearchGameActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user: FirebaseUser = firebaseAuth.currentUser!!
    private val uid: String = user.uid
    private lateinit var gameID: String
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_game)

        this.changeMenuVisibility(true)

        buttonPlay.setOnClickListener{
            this.playGame()
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
            buttonRanking.isEnabled = true
            buttonPlay.isEnabled = true
        } else {
            layoutProgressBar.visibility = View.VISIBLE
            layoutGameMenu.visibility = View.GONE
            buttonRanking.isEnabled = false
            buttonPlay.isEnabled = false
        }
    }

    private fun playGame(){
        this.changeMenuVisibility(false)

        textViewLoadingMessage.text = getString(R.string.search_game_search_available_game)
        firestore.collection(Constants.GAMES_COLLECTION)
            .whereEqualTo( Constants.GAMES_FIELD_PLAYER2_ID, "")
            .get()
            .addOnCompleteListener {

                if(it.result?.size() == 0){
                    createNewGame()
                } else {
                    val docGame = it.result?.documents!!.first()
                    gameID = docGame.id
                    val game = docGame.toObject(Game::class.java)
                    game!!.player2ID = uid

                    firestore.collection(Constants.GAMES_COLLECTION)
                        .document(gameID)
                        .set(game)
                        .addOnSuccessListener {
                            this.playAnimationAndStartGame()
                        }.addOnFailureListener {
                            changeMenuVisibility(true)
                            Toast.makeText(this, R.string.search_game_error_to_enter_on_a_game, Toast.LENGTH_LONG)
                        }
                }
            }
    }

    private fun createNewGame(){
        textViewLoadingMessage.text = getString(R.string.search_game_creating_new_game)
        val newGame = Game(uid)

        firestore.collection(Constants.GAMES_COLLECTION)
            .add(newGame)
            .addOnSuccessListener {
                gameID = it.id
                waitToStartGame()
            }.addOnFailureListener {
                changeMenuVisibility(true)
                Toast.makeText(this, R.string.search_game_error_on_creating_game, Toast.LENGTH_LONG)
            }
    }

    private fun startGame(){

        if(listenerRegistration != null){
            listenerRegistration!!.remove()
        }

        goToActivity<GameActivity>{
            putExtra(Constants.SEARCH_GAME_EXTRA_GAMEID, gameID)
        }
    }

    private fun waitToStartGame(){
        textViewLoadingMessage.text = getString(R.string.search_game_waiting_another_player)

        listenerRegistration = firestore.collection(Constants.GAMES_COLLECTION)
            .document(gameID)
            .addSnapshotListener { documentSnapshot, _ ->
                if(documentSnapshot!!.get(Constants.GAMES_FIELD_PLAYER2_ID)!! == ""){
                    this.playAnimationAndStartGame()
                }
            }
    }

    private fun playAnimationAndStartGame(){
        textViewLoadingMessage.text = getString(R.string.search_game_start_game)
        animationView.repeatCount = 0
        animationView.setAnimation("checked_animation.json")
        animationView.playAnimation()

        Handler().postDelayed({
            startGame()
        }, 1500)
    }

}
