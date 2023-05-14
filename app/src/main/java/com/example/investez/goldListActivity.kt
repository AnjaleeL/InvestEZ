package com.example.investez

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class goldListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var goldAdapter: GoldAdapter
    private lateinit var goldList: MutableList<Gold>
    private lateinit var goldRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_list)

        // Initializing RecyclerView
        goldRecyclerView = findViewById(R.id.rvGold)
        goldRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Firebase authentication and Firestore
        auth = Firebase.auth
        db = Firebase.firestore

        // Initialize goldList and goldAdapter
        goldList = mutableListOf()
        goldAdapter = GoldAdapter(this, goldList)
        goldRecyclerView.adapter = goldAdapter

        // Get current user ID
        val userID = auth.currentUser?.uid

        // Retrieve all gold documents for current user
        if (userID != null) {
            db.collection("gold")
                .whereEqualTo("userID", userID)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val amountOfGold = document.getDouble("amountOfGold")!!
                        val investAmount = document.getDouble("investAmount")!!
                        val goldID = document.getString("goldID")
                        val goldRate = document.getString("goldRate")
                        val goldWeight = document.getString("goldWeight")
                        val userID = document.getString("userID")
                        if (amountOfGold != null && goldID != null && goldRate != null && goldWeight != null && userID != null) {
                            val gold = Gold(amountOfGold,investAmount, goldID, goldRate, goldWeight, userID)
                            goldList.add(gold)
                        }
                    }
                    goldAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }
        }

        val addNew = findViewById<Button>(R.id.btnAddNewGold)
        addNew.setOnClickListener {
            val intent = Intent(this, GoldMainPage::class.java)
            startActivity(intent)
        }
        //navbar
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