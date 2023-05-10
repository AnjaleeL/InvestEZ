package com.example.investez

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class StockActivity : AppCompatActivity() {

    private lateinit var companySpinner: Spinner
    private lateinit var priceTextView: TextView
    private lateinit var investAmountEditText: EditText
    private lateinit var resultTextView: TextView
    private lateinit var calculateButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)

        companySpinner = findViewById(R.id.company_spinner)
        priceTextView = findViewById(R.id.price_text_view)
        investAmountEditText = findViewById(R.id.invest_amount_edit_text)
        resultTextView = findViewById(R.id.result_text_view)
        calculateButton = findViewById(R.id.btncalculateAmount)

        companySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (parent.getItemAtPosition(position)) {
                    "Abans Finance PLC" -> priceTextView.text = "222.60"
                    "Arpico Insurance Ltd" -> priceTextView.text = "119.50"
                    "Cargills" -> priceTextView.text = "237"
                    "John Keels" -> priceTextView.text = "135.50"
                    "Hemas" -> priceTextView.text = "662.10"
                    "Kingsbury" -> priceTextView.text = "119.40"
                    "Sampath Bank" -> priceTextView.text = "246.70"
                    "Sri Lanka Telecom" -> priceTextView.text = "186.80"
                    "Tokyo Cement" -> priceTextView.text = "146.30"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        calculateButton.setOnClickListener {
            val investAmount = investAmountEditText.text.toString().toDoubleOrNull()
            val price = priceTextView.text.toString().toDoubleOrNull()
            if (investAmount != null && price != null) {
                val result = (investAmount / price).toInt()
                resultTextView.text = "Amount of shares you can buy: $result"
            } else {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            }
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