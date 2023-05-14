package com.example.investez

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal
import java.math.RoundingMode

class FixedDeposite : AppCompatActivity() {

    private lateinit var bankSpinner: Spinner
    private lateinit var timeSpinner: Spinner
    private lateinit var investAmountEditText: EditText
    private lateinit var calculateButton: Button
    private lateinit var viewButton: Button
    private lateinit var fixedID: String

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val banks = arrayOf("BOC", "Sampath Bank", "NSB", "Commercial Bank", "Union Bank")
    private val timePlans = arrayOf("6 Months", "Annual")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fixed_deposite2)

        // Get references to UI elements
        bankSpinner = findViewById(R.id.fixed_bank_spin)
        timeSpinner = findViewById(R.id.fixed_time_plan)
        investAmountEditText = findViewById(R.id.inputAmountFixed)
        calculateButton = findViewById(R.id.btnfixed_calculate)
        viewButton = findViewById(R.id.view_prev_fixed_depo)

        // Initialize Firebase authentication and database instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // check if there is a fixedID passed from FixedDepositListView
        fixedID = intent.getStringExtra("fixedID") ?: ""

        if (fixedID.isNotEmpty()) {
            // if there is a fixedID, fetch the details from Firestore and populate the form
            db.collection("fixedInvestments").document(fixedID)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val fixedInvestment = documentSnapshot.toObject(FixedInvestment::class.java)
                        if (fixedInvestment != null) {
                           // bankSpinner.setSelection((bankSpinner.adapter as ArrayAdapter<String>).getPosition(fixedInvestment.bank))
                            bankSpinner.setSelection(getBankIndex(fixedInvestment.bank))
                            timeSpinner.setSelection(getTimePlanIndex(fixedInvestment.timePlan))
                            investAmountEditText.setText(fixedInvestment.investAmount.toString())
                          //  etMaturity.setText(fixedInvestment.maturity.toString())
                           // etProfit.setText(fixedInvestment.profit.toString())
                        }
                    } else {
                        Log.d(TAG, "No such document")
                        Toast.makeText(this, "Error: No such document", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                    Toast.makeText(this, "Error: Failed to fetch document", Toast.LENGTH_SHORT).show()
                }
        }

        // Set up the spinner adapters
        val bankAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, banks)
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bankSpinner.adapter = bankAdapter

        val timeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timePlans)
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner.adapter = timeAdapter

        // Set up the click listener for the calculate button
        calculateButton.setOnClickListener {
            calculateMaturity()
        }

        viewButton.setOnClickListener {
            val intent = Intent(this, FixedDepositListView::class.java)
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

    private fun calculateMaturity() {
        val investAmount = investAmountEditText.text.toString().toBigDecimalOrNull()
        val bank = bankSpinner.selectedItem.toString()
        val timePlan = timeSpinner.selectedItem.toString()

        if (investAmount == null || investAmount.compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(this, "Please enter a valid invest amount", Toast.LENGTH_SHORT).show()
            return
        }

        val maturity = when (bank) {
            "BOC" -> when (timePlan) {
                "Annual" -> investAmount.toDouble() * 1.12
                "6 Months" -> investAmount.toDouble() * 1.1025
                else -> 0
            }
            "Sampath Bank" -> when (timePlan) {
                "Annual" -> investAmount.toDouble() * 1.1575
                "6 Months" -> investAmount.toDouble() * 1.13
                else -> 0
            }
            "NSB" -> when (timePlan) {
                "Annual" -> investAmount.toDouble() * 1.1475
                "6 Months" -> investAmount.toDouble() * 1.12
                else -> 0
            }
            "Commercial Bank" -> when (timePlan) {
                "Annual" -> investAmount.toDouble() * 1.15
                "6 Months" -> investAmount.toDouble() * 1.13
                else -> 0
            }
            "Union Bank" -> when (timePlan) {
                "Annual" -> investAmount.toDouble() * 1.13
                "6 Months" -> investAmount.toDouble() * 1.1125
                else -> 0
            }
            else -> 0
        }

        val maturityFormatted = String.format("%.2f", maturity)

        // Create the intent for the fixed invest page and pass the values as extras
        val intent = Intent(this, FixedProfitActivity::class.java).apply {
            putExtra("bank", bank)
            putExtra("timePlan", timePlan)
            putExtra("investAmount", investAmount.toString())
            putExtra("maturity", maturityFormatted)
            putExtra("fixedID", fixedID)

        }

        // Start the fixed invest page activity
        startActivity(intent)
    }

    private fun getBankIndex(bank: String?): Int {
        if (bank != null) {
            for (i in banks.indices) {
                if (banks[i] == bank) {
                    return i
                }
            }
        }
        return 0 // default to first bank if not found or bank is null
    }

    private fun getTimePlanIndex(time: String?): Int {
        if (time != null) {
            for (i in timePlans.indices) {
                if (timePlans[i] == time) {
                    return i
                }
            }
        }
        return 0 // default to first bank if not found or bank is null
    }

}