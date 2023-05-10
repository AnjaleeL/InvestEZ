package com.example.investez

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView

class Dashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val btnGold = findViewById<Button>(R.id.btnGold)
        btnGold.setOnClickListener {
            val intent = Intent(this, GoldMainPage::class.java)
            startActivity(intent)
        }

        val btnReal = findViewById<Button>(R.id.btnReal)
        btnReal.setOnClickListener {
            val intent = Intent(this, RealStateActivity::class.java)
            startActivity(intent)
        }

        val btnStock = findViewById<Button>(R.id.btnStock)
        btnStock.setOnClickListener {
            val intent = Intent(this, StockActivity::class.java)
            startActivity(intent)
        }

        val btnFd = findViewById<Button>(R.id.btnFd)
        btnFd.setOnClickListener {
            val intent = Intent(this, FixedDeposite::class.java)
            startActivity(intent)
        }

        val nav = findViewById<BottomNavigationView>(R.id.nav)
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // handle home click
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.profile -> {
                    // handle profile click
                    val intent = Intent(this, UserProfile::class.java)
                    startActivity(intent)
                    true
                }
                R.id.notify -> {
                    // handle setting click
                    val intent = Intent(this, QuickInvesments::class.java)
                    startActivity(intent)
                    true

                }
                else -> false
            }
        }
    }
}