package com.franciscomartin.tictactoe.app

import android.app.Application
import com.google.firebase.FirebaseApp

public class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}