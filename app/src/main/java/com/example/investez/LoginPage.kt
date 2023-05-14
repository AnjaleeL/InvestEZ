package com.example.investez

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        // Initializing Firebase
        FirebaseApp.initializeApp(this)


        // Initialize UI elements
        emailEditText = findViewById(R.id.login_edit_email)
        passwordEditText = findViewById(R.id.login_edit_password)
        loginButton = findViewById(R.id.logn_button)
        signUpButton = findViewById(R.id.btnSignUp_login)

        // Set click listener on login button
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Call signInWithEmailAndPassword method
            // and handle completion using addOnCompleteListener
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login successful, go to main activity
                        Toast.makeText(this, "Login Successful.",
                            Toast.LENGTH_LONG).show()
                        val user = auth.currentUser
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        // Login failed, show error message
                        Toast.makeText(this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Set click listener on signup button
        signUpButton.setOnClickListener {
            val intent = Intent(this, RegistrationPage::class.java)
            startActivity(intent)
        }
    }
}