package com.example.investez

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class RealStateActivity : AppCompatActivity() {

    private lateinit var citySpinner: Spinner
    private lateinit var typeSpinner: Spinner
    private lateinit var calculateButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_state)

        citySpinner = findViewById(R.id.city_spinner)
        typeSpinner = findViewById(R.id.type_spinner)
        calculateButton = findViewById(R.id.btnLiveRateState)
        resultTextView = findViewById(R.id.today_state_rate)

        calculateButton.setOnClickListener {
            val city = citySpinner.selectedItem.toString()
            val type = typeSpinner.selectedItem.toString()

            val value = when (city) {
                "Anuradhapura" -> when (type) {
                    "Urban" -> "475,000 LKR"
                    "Rural" -> "201,000.00 LKR"
                    else -> "0"
                }
                "Colombo" -> when (type) {
                    "Urban" -> "477,000 LKR"
                    "Rural" -> "807,000.00 LKR"
                    else -> "0"
                }
                "Negombo" -> when (type) {
                    "Urban" -> "805,000 LKR"
                    "Rural" -> "607,000.00 LKR"
                    else -> "0"
                }
                "Gampaha" -> when (type) {
                    "Urban" -> "1,762,500.00 LKR"
                    "Rural" -> "690,000.00 LKR"
                    else -> "0"
                }
                "Kandy" -> when (type) {
                    "Urban" -> "355,000.00 LKR"
                    "Rural" -> "150,000.00 LKR"
                    else -> "0"
                }
                "Kurunegala" -> when (type) {
                    "Urban" -> "875,000.00 LKR"
                    "Rural" -> "105,000.00 LKR"
                    else -> "0"
                }
                "Jaffna" -> when (type) {
                    "Urban" -> "600,000.00 LKR"
                    "Rural" -> "100,000.00 LKR"
                    else -> "0"
                }
                "Galle" -> when (type) {
                    "Urban" -> "825,000.00 LKR"
                    "Rural" -> "230,000.00 LKR"
                    else -> "0"
                }
                else -> "0"
            }

            resultTextView.text = value
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