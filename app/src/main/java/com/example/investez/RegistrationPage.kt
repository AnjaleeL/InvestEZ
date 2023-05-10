package com.example.investez

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationPage : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etUserName: EditText
    private lateinit var etUserEmail: EditText
    private lateinit var etUserAddress: EditText
    private lateinit var etUserPassword: EditText
    private lateinit var etUserConfirmPassword: EditText

    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)

        // inside onCreate() method initialize
        etUserName = findViewById(R.id.reg_user_name)
        etUserEmail = findViewById(R.id.reg_user_email)
        etUserAddress = findViewById(R.id.reg_user_address)
        etUserPassword = findViewById(R.id.reg_user_password)
        etUserConfirmPassword = findViewById(R.id.reg_user_confirm_password)
        btnRegister = findViewById(R.id.btn_register)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnLogin = findViewById<Button>(R.id.btn_login_reg)
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val name = etUserName.text.toString().trim()
            val email = etUserEmail.text.toString().trim()
            val address = etUserAddress.text.toString().trim()
            val password = etUserPassword.text.toString().trim()
            val confirmPassword = etUserConfirmPassword.text.toString().trim()

            if (validateInputs(name, email, address, password, confirmPassword)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            val userID = user?.uid ?: ""

                            val userMap = HashMap<String, Any>()
                            userMap["name"] = name
                            userMap["email"] = email
                            userMap["address"] = address
                            userMap["userID"] = userID
                            db.collection("users").document(userID)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "User registered successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)

                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Error registering user: " + e.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            Toast.makeText(
                                this,
                                "Error registering user: " + task.exception?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }


        }
    }

    private fun validateInputs(
        name: String,
        email: String,
        address: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (TextUtils.isEmpty(name)) {
            etUserName.error = "Please enter your name"
            etUserName.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(email)) {
            etUserEmail.error = "Please enter your email"
            etUserEmail.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(address)) {
            etUserAddress.error = "Please enter your address"
            etUserAddress.requestFocus()
            return false
        }

        if (TextUtils.isEmpty(password)) {
            etUserPassword.error = "Please enter your password"
            etUserPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            etUserPassword.error = "Password should be at least 6 characters long"
            etUserPassword.requestFocus()
            return false
        }


        if (TextUtils.isEmpty(confirmPassword)) {
            etUserConfirmPassword.error = "Please confirm your password"
            etUserConfirmPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            etUserConfirmPassword.error = "Passwords do not match"
            etUserConfirmPassword.requestFocus()
            return false
        }

        return true
    }
}


