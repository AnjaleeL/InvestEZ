package com.example.investez

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuickInvestmentViewPage : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var editEmail: EditText
    private lateinit var editName: EditText
    private lateinit var editAsset: EditText
    private lateinit var editResult: EditText
    private lateinit var investmentId: String

    private var paymentRequestID: String? = null
    private var market: String? = null
    private var userId: String? = null
    private var asset: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_investment_view_page)

        // Initialize the EditText fields
        editEmail = findViewById(R.id.edit_email)
        editName = findViewById(R.id.edit_name)
        editResult = findViewById(R.id.edit_result)

        investmentId = intent.getStringExtra("investmentId").toString()

        db = FirebaseFirestore.getInstance()


        if (investmentId != null) {
            db.collection("investments").document(investmentId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val investment = document.toObject(Investment::class.java)

                        market = investment?.market ?: ""
                        paymentRequestID = investment?.paymentRequestID ?: ""
                        userId = investment?.userId ?: ""
                        asset = investment?.asset ?: ""
                        // Set the EditText fields with the investment data
                        editEmail.setText(investment?.userEmail)
                        editName.setText(investment?.userName)
                        //editAsset.setText(investment?.asset)
                        editResult.setText(investment?.result)
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
        val updateButton: Button = findViewById(R.id.btnEdit_qss)
        updateButton.setOnClickListener {
            val userEmail = editEmail.text.toString()
            val userName = editName.text.toString()
           // val asset = editAsset.text.toString()
            val result = editResult.text.toString()

            if (investmentId != null) {
                val investment = Investment(investmentId, result, asset!!, market!!, userId!!, paymentRequestID!!, userName, userEmail)

                db.collection("investments").document(investmentId)
                    .update(
                        "userEmail", investment.userEmail,
                        "userName", investment.userName,
                    )
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                        Toast.makeText(this, "Investment Update successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, QuickInvesmentsListView::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                    }
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
        val deleteButton = findViewById<Button>(R.id.btnDeleteQsss)
        deleteButton.setOnClickListener {
            deleteInvestment()
        }
    }

    private fun deleteInvestment() {
        db.collection("investments").document(investmentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Investment deleted successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, QuickInvesments::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting investment: ${e.message}", Toast.LENGTH_SHORT).show()
            }


    }

}