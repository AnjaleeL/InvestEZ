package com.example.investez

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FixedDepositListView : AppCompatActivity() {

    // Initialize Firebase authentication and Firestore, list of fixed investments, and add button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var fixedInvestmentList: MutableList<FixedInvestment>
    private lateinit var addButton: Button

    // On creation of activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fixed_deposit_list_view)

        // Initialize Firebase authentication and Firestore
        auth = Firebase.auth
        db = Firebase.firestore

        // Initialize list of fixed investments
        fixedInvestmentList = mutableListOf()
        addButton = findViewById(R.id.btnAddNewFixed)

        addButton.setOnClickListener {
            val intent = Intent(this, FixedDeposite::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerView and adapter
        val recyclerView = findViewById<RecyclerView>(R.id.rvFixedDeposit)
        val adapter = FixedInvestmentAdapter(this, fixedInvestmentList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //bottom navigation
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

        // Get current user's fixed investments from Firestore
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("fixedInvestments")
                .whereEqualTo("userID", currentUser.uid)
                .get()
                .addOnSuccessListener { result ->
                    // Add fixed investments to the list and update the adapter
                    for (document in result) {
                        val fixedInvestment = document.toObject(FixedInvestment::class.java)
                        fixedInvestment.fixedID = document.id
                        fixedInvestmentList.add(fixedInvestment)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error getting documents.", e)
                }
        } else {
            Log.w(TAG, "Current user is null.")
        }
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}