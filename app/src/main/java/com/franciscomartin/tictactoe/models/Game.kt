package com.franciscomartin.tictactoe.models

import java.util.*

data class Game (val player1ID: String){

    var player2ID: String = ""
    var selectedCells: MutableList<Int>  = mutableListOf(0,0,0, 0,0,0, 0,0,0)
    var player1Turn: Boolean = true
    var winnerID:String = ""
    val created: Date = Date()
    var surrenderID: String = ""
    
}