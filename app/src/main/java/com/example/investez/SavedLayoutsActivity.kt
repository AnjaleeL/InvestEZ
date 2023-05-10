package com.example.investez

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SavedLayoutsActivity : AppCompatActivity() {

    private lateinit var portfolioId: String
    private lateinit var portName: TextView
    private lateinit var portInvestType: Spinner
    private lateinit var portInvestAmount: EditText
    private lateinit var portProfitSoFar: EditText
    private lateinit var portNotes: EditText

    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saved_layouts)

        // Retrieve portfolio ID from Intent
        portfolioId = intent.getStringExtra("portfolioId") ?: ""

        // Initialize views
        portName = findViewById(R.id.port_edit_name)
        portInvestType = findViewById(R.id.port_edit_investType)
        portInvestAmount = findViewById(R.id.port_invest_amount)
        portProfitSoFar = findViewById(R.id.port_edit_profit_soFar)
        portNotes = findViewById(R.id.port_edit_notes)

        updateButton = findViewById(R.id.btnEditPortfolio)
        deleteButton = findViewById(R.id.btnDeletePortfolio)

        // Query Firestore for portfolio details
        val db = Firebase.firestore
        val portfolioRef = db.collection("portfolios").document(portfolioId)
        portfolioRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val portfolio = document.toObject(Portfolio::class.java)
                    // Set portfolio details to views
                    portName.text = portfolio?.name
                    portInvestType.setSelection(getInvestTypeIndex(portfolio?.investmentType))
                    portInvestAmount.setText(portfolio?.investmentAmount.toString())
                    portProfitSoFar.setText(portfolio?.profitSoFar.toString())
                    portNotes.setText(portfolio?.notes)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting portfolio: $exception")
            }

        // Set up the Update button click listener
        updateButton.setOnClickListener {
            val portfolioName = portName.text.toString()
            val investmentType = portInvestType.selectedItem.toString()
            val investmentAmount = portInvestAmount.text.toString().toDouble()
            val profitSoFar = portProfitSoFar.text.toString().toDouble()
            val notes = portNotes.text.toString()

            // Update portfolio details in Firestore
            db.collection("portfolios").document(portfolioId)
                .update(
                    "portfolioName", portfolioName,
                    "investmentType", investmentType,
                    "investmentAmount", investmentAmount,
                    "profitSoFar", profitSoFar,
                    "notes", notes
                )
                .addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Portfolio updated successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this, ViewPortfoliosActivity::class.java)
                    intent.putExtra("portfolioId", portfolioId)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error updating portfolio", e)
                }
        }

        // Set up the Delete button click listener
        deleteButton.setOnClickListener {
            // Delete portfolio from Firestore
            db.collection("portfolios").document(portfolioId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Portfolio deleted successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this, ViewPortfoliosActivity::class.java)
                    startActivity(intent)
                    finish() // Go back to ViewPortfoliosActivity
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "Error deleting portfolio", e)
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

    companion object {
        private const val TAG = "SavedLayoutsActivity"
    }

    private fun getInvestTypeIndex(investType: String?): Int {
        return when (investType) {
            "Fixed Deposits" -> 0
            "Gold" -> 1
            "Stock Market" -> 2
            "Real-Estate" -> 3
            "Bonds" -> 4
            else -> 0
        }
    }
}