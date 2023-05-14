package com.example.investez

import android.content.Intent // imports
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuickInvesments : AppCompatActivity() {

    private lateinit var marketSpinner: Spinner
    private lateinit var assetSpinner: Spinner
    private lateinit var filterButton: Button
    private lateinit var saveButton: Button
    private lateinit var viewButton: Button
    private lateinit var resultTextView: TextView

    private val stockMarketAssets = arrayOf("Central Bank Bonds", "Lanka IOC PLC Shares", "Bogawantalawa Tea Estates Shares")
    private val realEstateAssets = arrayOf("Colombo Metro Area 1 perch", "Marata Rural Area 2 perch")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_invesments)

        marketSpinner = findViewById(R.id.market_spinner)
        assetSpinner = findViewById(R.id.asset_spinner)
        filterButton = findViewById(R.id.filter_button)
        resultTextView = findViewById(R.id.result_text_view)
        saveButton = findViewById(R.id.btn_request_save)
        viewButton = findViewById(R.id.view_prev_quick_inves)

        marketSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (parent.getItemAtPosition(position)) {
                    "Stock Market" -> {
                        val adapter = ArrayAdapter(this@QuickInvesments, android.R.layout.simple_spinner_item, stockMarketAssets)
                        assetSpinner.adapter = adapter
                    }
                    "Real Estate" -> {
                        val adapter = ArrayAdapter(this@QuickInvesments, android.R.layout.simple_spinner_item, realEstateAssets)
                        assetSpinner.adapter = adapter
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        filterButton.setOnClickListener {
            val market = marketSpinner.selectedItem.toString()
            val asset = assetSpinner.selectedItem.toString()
            val result = when (market) {
                "Stock Market" -> {
                    when (asset) {
                        "Central Bank Bonds" -> "125,000.00 LKR"     // central bank bonds
                        "Lanka IOC PLC Shares" -> "50,010.00 LKR"
                        "Bogawantalawa Tea Estates Shares" -> "100,000.00 LKR"
                        else -> "Invalid Asset"
                    }
                }
                "Real Estate" -> {
                    when (asset) {
                        "Colombo Metro Area 1 perch" -> "807,000.00 LKR"
                        "Marata Rural Area 2 perch" -> "500,000.00 LKR"
                        else -> "Invalid Asset"
                    }
                }
                else -> "Invalid Market"
            }
            resultTextView.text = asset + " - " + result
        }

        saveButton.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()

            val result = resultTextView.text.toString()
            val asset = assetSpinner.selectedItem.toString()
            val market = marketSpinner.selectedItem.toString()

            if (asset.isNotBlank() && market.isNotBlank() && result.isNotBlank()) {
                // get current user's name and email from users table
                db.collection("users").document(currentUser?.uid ?: "")
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val userName = document.getString("name") ?: ""
                            val userEmail = document.getString("email") ?: ""

                            val investment = hashMapOf(
                                "result" to result,
                                "asset" to asset,
                                "market" to market,
                                "userId" to currentUser?.uid,
                                "userName" to userName, // add user name to investment data
                                "userEmail" to userEmail // add user email to investment data
                            )

                            val documentRef = db.collection("investments").document()
                            val paymentRequestID = documentRef.id // generate payment request ID
                            investment["paymentRequestID"] = paymentRequestID // add payment request ID to investment data

                            documentRef.set(investment)
                                .addOnSuccessListener {
                                    // update payment request ID in the document
                                    documentRef.update("paymentRequestID", paymentRequestID)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Investment saved!", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this, QuickInvesmentsListView::class.java)
                                            startActivity(intent)
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Error updating payment request ID: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error saving investment: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Error getting user information", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error getting user information: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please choose a request", Toast.LENGTH_SHORT).show()
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

        viewButton.setOnClickListener {
            val intent = Intent(this, QuickInvesmentsListView::class.java)
            startActivity(intent)
        }

    }
}