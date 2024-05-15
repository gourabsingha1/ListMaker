package com.example.listmaker.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.example.listmaker.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDarkTheme()

        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this, HomeActivity::class.java).also { intent ->
                startActivity(intent)
                finishAffinity()
            }
        }, 1000)
    }


    // Set dark theme
    private fun setDarkTheme() {
        val sharedPreferences = getSharedPreferences("THEME", Context.MODE_PRIVATE)
        val darkTheme = sharedPreferences.getBoolean("darkTheme", false)
        if(darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}