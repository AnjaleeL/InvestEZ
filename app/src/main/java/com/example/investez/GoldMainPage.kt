package com.example.investez

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GoldMainPage : AppCompatActivity() {
    var myGlobalSpinValue = 0
    private lateinit var goldSaveButton: Button
    private lateinit var viewButton: Button
    private lateinit var updateButton: Button
    private lateinit var goldID: String
    private val goldWeightArray = arrayOf("21 Carat", "22 Carat", "24 Carat")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gold_main_page)

        val spinner = findViewById<Spinner>(R.id.gold_weight)
        val textView = findViewById<TextView>(R.id.today_gold_rate)
        val goldAmount = findViewById<TextView>(R.id.amount_of_gold)
        val investAmountEditText = findViewById<EditText>(R.id.gold_invest_amount)
        goldSaveButton = findViewById(R.id.gold_save_button)
        viewButton = findViewById(R.id.view_prev_gold)
        updateButton = findViewById(R.id.gold_update_button)

        // check if there is a fixedID passed from FixedDepositListView
        goldID = intent.getStringExtra("goldID") ?: ""

        // Create a Firestore instance
        val db = FirebaseFirestore.getInstance()

        if (goldID.isNotEmpty()) {
            goldSaveButton.visibility = View.GONE
            updateButton.visibility = View.VISIBLE
        } else {
            goldSaveButton.visibility = View.VISIBLE
            updateButton.visibility = View.GONE
        }



        if (goldID.isNotEmpty()) {
            // if there is a fixedID, fetch the details from Firestore and populate the form
            db.collection("gold").document(goldID)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val gold = documentSnapshot.toObject(Gold::class.java)
                        if (gold != null) {
                            // bankSpinner.setSelection((bankSpinner.adapter as ArrayAdapter<String>).getPosition(fixedInvestment.bank))
                            spinner.setSelection(getWeightIndex(gold.goldWeight))
                            textView.setText(gold.goldRate.toString())
                            investAmountEditText.setText(gold.investAmount.toString())
                            goldAmount.setText(gold.amountOfGold.toString())
                        }
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                        Toast.makeText(this, "Error: No such document", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                    Toast.makeText(this, "Error: Failed to fetch document", Toast.LENGTH_SHORT).show()
                }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        val rate = 20230.00 // rate for 21 Carat
                        myGlobalSpinValue = 20230
                        val amount = investAmountEditText.text.toString()
                        if (!TextUtils.isEmpty(amount)) {
                            val amount = amount.toDouble()
                            // Do the calculation with the amount
                            val quantity = amount / rate
                            goldAmount.text = String.format("%.2f", quantity)
                        } else {
                            goldAmount.text = "0"
                        }
                        textView.text = "Rs. $rate" // 21 Carat
                    }
                    1 -> {
                        val rate = 21190.00 // rate for 22 Carat
                        myGlobalSpinValue = 21190
                        val amount = investAmountEditText.text.toString()
                        if (!TextUtils.isEmpty(amount)) {
                            val amount = amount.toDouble()
                            // Do the calculation with the amount
                            val quantity = amount / rate
                            goldAmount.text = String.format("%.2f", quantity)
                        } else {
                            goldAmount.text = "0"
                        }
                        textView.text = "Rs. $rate" // 22 Carat
                    }
                    2 -> {
                        val rate = 23110.00 // rate for 24 Carat
                        myGlobalSpinValue = 23110
                        val amount = investAmountEditText.text.toString()
                        if (!TextUtils.isEmpty(amount)) {
                            val amount = amount.toDouble()
                            // Do the calculation with the amount
                            val quantity = amount / rate
                            goldAmount.text = String.format("%.2f", quantity)
                        } else {
                            goldAmount.text = "0"
                        }
                        textView.text = "Rs. $rate" // 24 Carat
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        investAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // Do something after the text has changed
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do something before the text has changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something while the text is changing
                val amount = investAmountEditText.text.toString()
                if (!TextUtils.isEmpty(amount)) {
                    val amount = amount.toDouble()
                    // Do the calculation with the amount
                    val quantity = amount / myGlobalSpinValue
                    goldAmount.text = String.format("%.2f", quantity)
                } else {
                    goldAmount.text = "0 G"
                }
            }
        })

        //save function
        goldSaveButton.setOnClickListener {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val goldWeight = spinner.selectedItem.toString()
            val goldRate = textView.text.toString()
            val amountOfGold = String.format("%.2f", goldAmount.text.toString().toDouble()).toDouble()
            val amount = investAmountEditText.text.toString()

            if (amount.isNotEmpty()) {
            val totamount = amount.toDouble()
            val goldData: MutableMap<String, Any?> = mutableMapOf(
                "userID" to userID,
                "goldWeight" to goldWeight,
                "goldRate" to goldRate,
                "amountOfGold" to amountOfGold,
                "investAmount" to totamount
            )

            db.collection("gold").add(goldData)
                .addOnSuccessListener { documentReference ->
                    // Get the new document ID
                    val goldID = documentReference.id
                    // Save the gold ID to the data
                    goldData["goldID"] = goldID
                    // Update the document with the gold ID
                    documentReference.update(goldData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Record added successfully!", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, goldListActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show()
                }
            }else {
                Toast.makeText(this, "Please enter an investment amount", Toast.LENGTH_LONG).show()
            }
        }

        //update function
        updateButton.setOnClickListener {
            val db = Firebase.firestore
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val goldWeight = spinner.selectedItem.toString()
            val goldRate = textView.text.toString()
            val amountOfGold = String.format("%.2f", goldAmount.text.toString().toDouble()).toDouble()
            val amount = investAmountEditText.text.toString()
            val totamount = amount.toDouble()

            val goldInvestment = hashMapOf(
                "userID" to userID,
                "goldWeight" to goldWeight,
                "goldRate" to goldRate,
                "amountOfGold" to amountOfGold,
                "investAmount" to totamount,
                "goldID" to goldID
            )
            db.collection("gold")
                .document(goldID)
                .set(goldInvestment)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Fixed investment updated with ID: $goldID")
                    Toast.makeText(this, "Gold Investment update successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, goldListActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error updating document", e)
                    Toast.makeText(this, "Gold Investment update failed", Toast.LENGTH_SHORT).show()
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
            val intent = Intent(this, goldListActivity::class.java)
            startActivity(intent)
        }

    }
    private fun getWeightIndex(time: String?): Int {
        if (time != null) {
            for (i in goldWeightArray.indices) {
                if (goldWeightArray[i] == time) {
                    return i
                }
            }
        }
        return 0 // default to first bank if not found or bank is null
    }
}