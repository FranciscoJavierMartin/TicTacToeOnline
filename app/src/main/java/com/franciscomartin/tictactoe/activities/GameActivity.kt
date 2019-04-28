package com.franciscomartin.tictactoe.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.franciscomartin.tictactoe.R
import com.franciscomartin.tictactoe.commons.Constants
import com.franciscomartin.tictactoe.models.Game
import com.franciscomartin.tictactoe.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.dialog_game_over.*

class GameActivity : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user: FirebaseUser = firebaseAuth.currentUser!!
    private val uid: String = user.uid
    private lateinit var userPlayer1: User
    private lateinit var userPlayer2: User
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
        if (gameListener != null) {
            gameListener!!.remove()
        }
        super.onStop()
    }

    fun selectedCell(view: View) {
        if (!game.winnerID.isEmpty()) {
            Toast.makeText(this, R.string.game_game_has_end, Toast.LENGTH_LONG)
        } else {

            if (game.player1Turn && game.player1ID == uid) {
                updateGame(view.tag.toString())
            } else if (!game.player1Turn && game.player2ID == uid) {
                updateGame(view.tag.toString())
            } else {
                Toast.makeText(this, R.string.game_it_not_your_turn, Toast.LENGTH_LONG)
            }
        }
    }

    private fun initGameListener() {
        gameListener = firestore.collection(Constants.GAMES_COLLECTION)
            .document(gameID)
            .addSnapshotListener(this) { snapshot, firebaseFirestoreException ->

                if (firebaseFirestoreException != null) {
                    Toast.makeText(this, R.string.game_error_on_get_data, Toast.LENGTH_LONG)
                } else {

                    if (snapshot != null) {
                        val dataFromServer: Boolean = snapshot.metadata.hasPendingWrites()

                        if (snapshot.exists() && dataFromServer) {
                            game = snapshot.toObject(Game::class.java)!!

                            if (playerOneName.isEmpty() || playerTwoName.isEmpty()) {
                                getPlayerNames()
                            }

                            updateUI()

                        }

                        updatePlayersUI()

                    }

                }

            }
    }

    private fun getPlayerNames() {
        firestore.collection(Constants.USERS_COLLECTION)
            .document(game.player1ID)
            .get()
            .addOnSuccessListener(this) {
                userPlayer1 = it.toObject(User::class.java)!!
                playerOneName = it.get(Constants.USERS_FIELD_NAME).toString()
                textViewPlayer1.text = playerOneName
            }

        firestore.collection(Constants.USERS_COLLECTION)
            .document(game.player2ID)
            .get()
            .addOnSuccessListener(this) {
                userPlayer2 = it.toObject(User::class.java)!!
                playerTwoName = it.get(Constants.USERS_FIELD_NAME).toString()
                textViewPlayer2.text = playerTwoName
            }
    }

    private fun updateGame(cellNumber: String) {
        val cellPosition = Integer.parseInt(cellNumber)

        if (game.selectedCells.get(cellPosition) != 0) {
            Toast.makeText(this, R.string.game_select_an_empty_cell, Toast.LENGTH_LONG)
        } else {
            if (game.player1Turn) {
                this.cells[cellPosition].setImageResource(R.drawable.ic_player_one)
                game.selectedCells[cellPosition] = 1
            } else {
                this.cells[cellPosition].setImageResource(R.drawable.ic_player_two)
                game.selectedCells[cellPosition] = 2
            }

            if (this.existsSolution()) {
                game.winnerID = uid
            } else if(this.existsDraw()){
                game.winnerID = Constants.DRAW
            } else {
                changeTurn()
            }


            firestore.collection(Constants.GAMES_COLLECTION)
                .document(gameID)
                .set(game)
                .addOnSuccessListener(this) {

                }.addOnFailureListener(this) {
                    // TODO: Make something for tell to user that error happens
                }
        }

    }

    private fun changeTurn() {
        game.player1Turn = !game.player1Turn
    }

    private fun updateUI() {

        game.selectedCells.forEach { cell ->
            when (cell) {
                0 -> cells[cell].setImageResource(R.drawable.ic_empty_square)
                1 -> cells[cell].setImageResource(R.drawable.ic_player_one)
                2 -> cells[cell].setImageResource(R.drawable.ic_player_two)
            }
        }

    }

    private fun updatePlayersUI() {
        if (game.player1Turn) {
            textViewPlayer1.setTextColor(resources.getColor(R.color.colorPrimary))
            textViewPlayer2.setTextColor(resources.getColor(R.color.colorOtherPlaying))
        } else {
            textViewPlayer1.setTextColor(resources.getColor(R.color.colorOtherPlaying))
            textViewPlayer2.setTextColor(resources.getColor(R.color.colorAccent))
        }

        if(game.winnerID.isNotEmpty()){
            showDialogGameOver()
        }
    }

    private fun existsSolution(): Boolean =
        checkIfRowTokensBelongToTheSamePlayer(0, 1, 2) &&
                checkIfRowTokensBelongToTheSamePlayer(3, 4, 5) &&
                checkIfRowTokensBelongToTheSamePlayer(6, 7, 8) &&
                checkIfRowTokensBelongToTheSamePlayer(0, 3, 6) &&
                checkIfRowTokensBelongToTheSamePlayer(1, 4, 7) &&
                checkIfRowTokensBelongToTheSamePlayer(2, 5, 8) &&
                checkIfRowTokensBelongToTheSamePlayer(0, 4, 8) &&
                checkIfRowTokensBelongToTheSamePlayer(2, 4, 6)

    private fun checkIfRowTokensBelongToTheSamePlayer(p0: Int, p1: Int, p2: Int): Boolean =
        game.selectedCells[p0] == game.selectedCells[p1] &&
                game.selectedCells[p1] == game.selectedCells[p2] &&
                game.selectedCells[p2] != 0

    private fun existsDraw(): Boolean =
            game.selectedCells.filter { value -> value == 0 }.isEmpty()

    private fun showDialogGameOver(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_game_over,null)
        val playerName = if(game.player1ID == uid) playerOneName else playerTwoName

        if(game.winnerID == Constants.DRAW){
            updateScore(1)
            textViewInformation.text = getString(R.string.game_game_over_information_draw, playerName, Constants.DRAW_POINTS)
            textViewPoints.text = getString(R.string.game_game_over_dialog_one_points)
        } else if(game.winnerID == uid){
            updateScore(3)
            textViewInformation.text = getString(R.string.game_game_over_information_win, playerName, Constants.WIN_POINTS)
            textViewPoints.text = getString(R.string.game_game_over_dialog_more_points, Constants.WIN_POINTS)
        } else{
            updateScore(0)
            textViewInformation.text = getString(R.string.game_game_over_information_lose, playerName)
            textViewPoints.text = ""
            animationView.setAnimation("thumbs_down_animation.json")
        }

        animationView.playAnimation()

        builder.setTitle(R.string.game_game_over_dialog_title)
            .setCancelable(false)
            .setView(view)
            .setPositiveButton(R.string.game_game_over_dialog_possitive_button_text,{ _, _ ->
                finish()
            }).create()
            .show()

    }

    private fun updateScore(score: Int){
        val userToUpdate: User =
            if(game.player1ID == uid){
                userPlayer1.points = userPlayer1.points + score
                userPlayer1.gamesPlayed = userPlayer1.gamesPlayed + 1
                userPlayer1
            } else {
                userPlayer2.points = userPlayer2.points + score
                userPlayer2.gamesPlayed = userPlayer2.gamesPlayed + 1
                userPlayer2
            }

        firestore.collection(Constants.USERS_COLLECTION)
            .document(uid)
            .set(userToUpdate)
            .addOnSuccessListener(this){

            }.addOnFailureListener(this){

            }
    }
}
