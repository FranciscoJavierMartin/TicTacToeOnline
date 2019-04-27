package com.franciscomartin.tictactoe.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.franciscomartin.tictactoe.R

import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

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

    }

}
