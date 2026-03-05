package com.example.skydash.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skydash.databinding.ActivityGameBinding
import com.example.skydash.R

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPause.setOnClickListener {
            val v = findViewById<GameView>(R.id.gameView)
            v.togglePause()
            binding.btnPause.text = if (v.isPaused) "Resume" else "Pause"
        }
    }

    override fun onPause() {
        super.onPause()
        findViewById<GameView>(R.id.gameView).onHostPause()
    }

    override fun onResume() {
        super.onResume()
        findViewById<GameView>(R.id.gameView).onHostResume()
    }
}
