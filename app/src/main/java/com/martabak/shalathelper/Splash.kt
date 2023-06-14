package com.martabak.shalathelper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("zaky", "splash launched")
        var intent : Intent = Intent(this@Splash, MainActivity::class.java)
        // only go to main activity after 1,5 sec
        Handler(Looper.getMainLooper()).postDelayed(
            Runnable {
                startActivity(intent)
                finish()
                     }, 1500
        )


    }
}