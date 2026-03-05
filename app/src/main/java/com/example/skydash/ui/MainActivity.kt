package com.example.skydash.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skydash.databinding.ActivityMainBinding
import com.example.skydash.game.GameActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("skydash", MODE_PRIVATE)
        val high = prefs.getInt("highScore", 0)
        val coinBal = prefs.getInt("coins", 0)

        binding.tvHighScore.text = "High Score: $high"
        binding.tvCoins.text = "Coins: $coinBal"

        binding.btnPlay.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        binding.btnShop.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
