package com.example.listmaker.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.listmaker.R
import com.example.listmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back press
        binding.ivSettingsBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
        }

        // Set dark theme
        val sharedPreferences = getSharedPreferences("THEME", Context.MODE_PRIVATE)
        val darkTheme = sharedPreferences.getBoolean("darkTheme", false)
        if(darkTheme) {
            binding.switchDarkTheme.isChecked = true
        }
        binding.switchDarkTheme.setOnClickListener {
            if(darkTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putBoolean("darkTheme", false).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putBoolean("darkTheme", true).apply()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
    }
}