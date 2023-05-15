package com.example.investez

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PortfolioActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portfolio)

        db = FirebaseFirestore.getInstance()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        val btnUpdate = findViewById<Button>(R.id.port_save_button)

        val editName = findViewById<EditText>(R.id.edit_port_name)
        val spinnerInvestmentType = findViewById<Spinner>(R.id.port_investType)
        val editInvestmentAmount = findViewById<EditText>(R.id.port_invest_amount)
        val editProfitSoFar = findViewById<EditText>(R.id.port_profit_so_far)
        val editNotes = findViewById<EditText>(R.id.port_note)

        val clearButton = findViewById<Button>(R.id.btnClearField)

        val viewButton= findViewById<Button>(R.id.view_prev_portForlois)



        btnUpdate.setOnClickListener {
            val name = editName.text.toString().trim()
            val investmentType = spinnerInvestmentType.selectedItem.toString()
            val investmentAmountStr = editInvestmentAmount.text.toString().trim()
            val profitSoFarStr = editProfitSoFar.text.toString().trim()
            val notes = editNotes.text.toString().trim()
            val userId = currentUserId ?: ""

            // Required field validations
            if (name.isEmpty()) {
                editName.error = "Name is required"
                return@setOnClickListener
            }

            if (investmentAmountStr.isEmpty()) {
                editInvestmentAmount.error = "Investment amount is required"
                return@setOnClickListener
            }

            if (profitSoFarStr.isEmpty()) {
                editProfitSoFar.error = "Profit so far is required"
                return@setOnClickListener
            }

            val investmentAmount = investmentAmountStr.toDoubleOrNull()
            if (investmentAmount == null) {
                editInvestmentAmount.error = "Investment amount should be a number"
                return@setOnClickListener
            }

            val profitSoFar = profitSoFarStr.toDoubleOrNull()
            if (profitSoFar == null) {
                editProfitSoFar.error = "Profit so far should be a number"
                return@setOnClickListener
            }

            val portfolio = Portfolio(name, investmentType, investmentAmount, profitSoFar, notes, userId)

            db.collection("portfolios")
                .add(portfolio)
                .addOnSuccessListener { documentReference ->
                    val portfolioId = documentReference.id
                    // Save the portfolio ID inside the document
                    val updatedPortfolio = portfolio.copy(portfolioId = portfolioId)
                    db.collection("portfolios").document(portfolioId)
                        .set(updatedPortfolio)
                        .addOnSuccessListener {
                            Toast.makeText(this, "portfolio successfully saved!", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, ViewPortfoliosActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: No such document", Toast.LENGTH_SHORT).show()

                        }
                }
                .addOnFailureListener { e ->
                    // Handle errors
                }
        }

        //clearing the form
        clearButton.setOnClickListener {
            editName.setText("")
            spinnerInvestmentType.setSelection(0)
            editInvestmentAmount.setText("")
            editProfitSoFar.setText("")
            editNotes.setText("")
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
            val intent = Intent(this, ViewPortfoliosActivity::class.java)
            startActivity(intent)
        }

    }
}