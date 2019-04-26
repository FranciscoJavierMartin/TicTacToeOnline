package com.franciscomartin.tictactoe.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.franciscomartin.tictactoe.R
import kotlinx.android.synthetic.main.activity_search_game.*

class SearchGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_game)

        progressBarSearchGames.setIndeterminate(true)
    }
}
