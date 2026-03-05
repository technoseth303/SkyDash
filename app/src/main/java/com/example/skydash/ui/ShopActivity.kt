package com.example.skydash.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skydash.databinding.ActivityShopBinding

class ShopActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("skydash", MODE_PRIVATE)
        val editor = prefs.edit()

        fun refresh() {
            val coins = prefs.getInt("coins", 0)
            val spd = prefs.getInt("up_speed", 0)
            val mag = prefs.getInt("up_magnet", 0)
            val sh = prefs.getInt("up_shield", 0)

            binding.tvBalance.text =
                "Coins: $coins\nSpeed Lv $spd  |  Magnet Lv $mag  |  Shield Lv $sh"

            binding.btnSpeed.text = "Speed Boost (Lv $spd) - ${100 + spd * 50}"
            binding.btnMagnet.text = "Coin Magnet (Lv $mag) - ${100 + mag * 50}"
            binding.btnShield.text = "Shield (Lv $sh) - ${150 + sh * 75}"
        }

        fun buy(key: String, base: Int, inc: Int) {
            var coins = prefs.getInt("coins", 0)
            var lv = prefs.getInt(key, 0)
            val cost = base + lv * inc

            if (coins >= cost && lv < 10) {
                coins -= cost
                lv++
                editor.putInt("coins", coins)
                editor.putInt(key, lv)
                editor.apply()
                refresh()
            }
        }

        refresh()

        binding.btnSpeed.setOnClickListener { buy("up_speed", 100, 50) }
        binding.btnMagnet.setOnClickListener { buy("up_magnet", 100, 50) }
        binding.btnShield.setOnClickListener { buy("up_shield", 150, 75) }
        binding.btnBack.setOnClickListener { finish() }
    }
}
