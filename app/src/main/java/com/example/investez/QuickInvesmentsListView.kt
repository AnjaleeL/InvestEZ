package com.example.investez

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuickInvesmentsListView : AppCompatActivity() {

    private lateinit var addButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var investmentAdapter: InvestmentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_invesments_list_view)

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.rvQuickInvestmentLView)
        investmentAdapter = InvestmentAdapter(mutableListOf())
        recyclerView.adapter = investmentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Retrieve investments for current user
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val investments = mutableListOf<Investment>()

        addButton = findViewById(R.id.btnAddNew_investment)

        addButton.setOnClickListener {
            val intent = Intent(this, QuickInvesments::class.java)
            startActivity(intent)
        }

        db.collection("investments")
            .whereEqualTo("userId", currentUser?.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val investment = Investment(
                        id = document.id,
                        result = document.getString("result") ?: "",
                        asset = document.getString("asset") ?: "",
                        market = document.getString("market") ?: "",
                        userId = document.getString("userId") ?: "",
                        paymentRequestID = document.getString("paymentRequestID") ?: "",
                        userName = document.getString("userName") ?: "",
                        userEmail = document.getString("userEmail") ?: ""
                    )
                    investments.add(investment)
                }

                // Update the RecyclerView adapter with the new data
                investmentAdapter.updateData(investments)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting investments: ${e.message}", Toast.LENGTH_SHORT).show()
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