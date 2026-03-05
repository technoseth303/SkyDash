package com.example.skydash.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skydash.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("skydash", MODE_PRIVATE)
        val editor = prefs.edit()

        binding.swMusic.isChecked = prefs.getBoolean("music", true)
        binding.swSfx.isChecked = prefs.getBoolean("sfx", true)

        binding.swMusic.setOnCheckedChangeListener { _, checked ->
            editor.putBoolean("music", checked).apply()
        }

        binding.swSfx.setOnCheckedChangeListener { _, checked ->
            editor.putBoolean("sfx", checked).apply()
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}
