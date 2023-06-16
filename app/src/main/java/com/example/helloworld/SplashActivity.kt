package com.example.helloworld

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGHT = 2000 // 两秒后进入系统
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val mainIntent = Intent(
                this@SplashActivity,
                MainActivity::class.java
            )
            this@SplashActivity.startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGHT.toLong())
    }
}