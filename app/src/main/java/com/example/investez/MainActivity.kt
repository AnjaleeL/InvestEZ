package com.example.investez

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {
    private lateinit var etUserName: TextView
    private lateinit var btnPortFolio: ImageButton
    private lateinit var btninvesment: ImageButton
    private lateinit var quickInvestButton: ImageButton
    private lateinit var userProfileImage: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        etUserName = findViewById(R.id.textView_name)
        btnPortFolio = findViewById(R.id.button_portfolio)
        btninvesment = findViewById(R.id.ibMyPlans)
        quickInvestButton = findViewById(R.id.quickinvestImage)
        userProfileImage = findViewById(R.id.imageViewMain)

        val user = FirebaseAuth.getInstance().currentUser
        val userID = user?.uid ?: ""
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name")
                    val profileImageUrl = documentSnapshot.getString("profileImageUrl")

                    // set the name to the EditText field
                    etUserName.setText(name)

                    // If the user has a profile image, retrieve it and display it in the ImageView
                    if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                        val storageRef = FirebaseStorage.getInstance().reference.child("images/${userID}/profile.jpg")
                        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            userProfileImage.setImageBitmap(bitmap)
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "Error fetching profile image: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error fetching user data: " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        btnPortFolio.setOnClickListener {
            val intent = Intent(this, PortfolioActivity::class.java)
            startActivity(intent)
        }

        btninvesment.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }

        quickInvestButton.setOnClickListener {
            val intent = Intent(this, QuickInvesments::class.java)
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