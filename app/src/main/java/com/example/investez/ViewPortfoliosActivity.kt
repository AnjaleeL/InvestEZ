package com.example.investez

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.investez.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ViewPortfoliosActivity : AppCompatActivity() {

    private lateinit var rvPortfolios: RecyclerView
    private lateinit var portfolioAdapter: PortfolioAdapter
    private lateinit var tvTotalProfit: TextView
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_portfolios)

        val portfoliosList = mutableListOf<Portfolio>()

        // Initialize RecyclerView
        tvTotalProfit = findViewById(R.id.tvTotalProfit_amount)
        addButton = findViewById(R.id.btnaddNewPortfolio)
        rvPortfolios = findViewById(R.id.rvPortfolios)
        rvPortfolios.layoutManager = LinearLayoutManager(this)
        portfolioAdapter = PortfolioAdapter(portfoliosList)
        rvPortfolios.adapter = portfolioAdapter


        // Fetch portfolios from Firestore
        val currentUser = Firebase.auth.currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            val db = Firebase.firestore
            val portfoliosRef = db.collection("portfolios").whereEqualTo("userId", userId)
            portfoliosRef.get()
                .addOnSuccessListener { documents ->
                    val portfolios = mutableListOf<Portfolio>()
                    var totalProfit = 0.0 // Initialize total profit
                    for (document in documents) {
                        val portfolio = document.toObject(Portfolio::class.java)
                        portfolio.portfolioId = document.id
                        portfolios.add(portfolio)
                        totalProfit += portfolio.profitSoFar // Add profitSoFar to total profit
                    }
                    portfolioAdapter.setPortfolios(portfolios)

                    tvTotalProfit.text = "Total profit: ${totalProfit.toString()}"
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting portfolios: $exception")
                }
        }
        addButton.setOnClickListener {
            val intent = Intent(this, PortfolioActivity::class.java)
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