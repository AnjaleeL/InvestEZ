package com.example.investez

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.math.BigDecimal

class FixedProfitActivity : AppCompatActivity() {

    private lateinit var fixedID: String
    private lateinit var buttonUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fixed_profit)

        val intent = intent
        val bank = intent.getStringExtra("bank")
        val timePlan = intent.getStringExtra("timePlan")
        val investAmountStr = intent.getStringExtra("investAmount")
        val investAmount = investAmountStr?.toDoubleOrNull() ?: 0.0
        val maturityStr = intent.getStringExtra("maturity")
        fixedID = intent.getStringExtra("fixedID") ?: ""
        val maturity = maturityStr?.toDoubleOrNull() ?: 0.0
        buttonUpdate = findViewById(R.id.btnfixedUpadate)
        val saveButton = findViewById<Button>(R.id.btnfixed_prof_Save)

        if (fixedID.isNotEmpty()) {
            saveButton.visibility = View.GONE
            buttonUpdate.visibility = View.VISIBLE
        } else {
            saveButton.visibility = View.VISIBLE
            buttonUpdate.visibility = View.GONE
        }


        val profit = maturity - investAmount
        val maturityDouble = maturity.toDouble()
        val investAmountDouble = investAmount.toDouble()


        val profitText = findViewById<TextView>(R.id.fxed_prof_text)
        profitText.text = profit.toString()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid


        saveButton.setOnClickListener {
            val db = Firebase.firestore
            val fixedInvestment = hashMapOf(
                "userID" to userID,
                "bank" to bank,
                "timePlan" to timePlan,
                "investAmount" to investAmountDouble,
                "maturity" to maturityDouble,
                "profit" to profit,
                "fixedID" to ""
            )
            db.collection("fixedInvestments")
                .add(fixedInvestment)
                .addOnSuccessListener { documentReference ->
                    val fixedID = documentReference.id
                    db.collection("fixedInvestments")
                        .document(fixedID)
                        .update("fixedID", fixedID)
                        .addOnSuccessListener {
                            Log.d(TAG, "Fixed investment added with ID: $fixedID")
                            Toast.makeText(this, "Investment save successful", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, FixedDepositListView::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating document", e)
                            Toast.makeText(this, "Investment save failed", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this, "Investment save failed", Toast.LENGTH_SHORT).show()
                }
        }

        buttonUpdate.setOnClickListener {
            val db = Firebase.firestore
            val fixedInvestment = hashMapOf(
                "userID" to userID,
                "bank" to bank,
                "timePlan" to timePlan,
                "investAmount" to investAmountDouble,
                "maturity" to maturityDouble,
                "profit" to profit,
                "fixedID" to ""
            )
            db.collection("fixedInvestments")
                .document(fixedID)
                .set(fixedInvestment)
                .addOnSuccessListener {
                    Log.d(TAG, "Fixed investment updated with ID: $fixedID")
                    Toast.makeText(this, "Investment update successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, FixedDepositListView::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating document", e)
                    Toast.makeText(this, "Investment update failed", Toast.LENGTH_SHORT).show()
                }
        }
        // Set up bottom navigation view
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