package com.example.investez

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class UserProfile : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etUserName: EditText
    private lateinit var etUserEmail: EditText
    private lateinit var etUserAddress: EditText
    private lateinit var userProfileImage: ImageView
    private lateinit var fileUpload: Button
    private val IMAGE_PICK_REQUEST_CODE = 123
    private var selectedImageUri: Uri? = null
    private lateinit var logoutButton: Button
    private lateinit var deleteButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val nav = findViewById<BottomNavigationView>(R.id.nav)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etUserName = findViewById(R.id.user_profile_edit_name)
        etUserEmail = findViewById(R.id.user_profile_edit_email)
        etUserAddress = findViewById(R.id.user_profile_edit_address)
        userProfileImage = findViewById(R.id.imageView3)
        fileUpload = findViewById(R.id.btnSelect_profile_picture)
        logoutButton = findViewById(R.id.logOutButton)
        deleteButton = findViewById(R.id.btnDeleter)

        // Get the current user's ID
        val userID = mAuth.currentUser?.uid ?: ""

        // Get the current user's data from Firestore
        db.collection("users").document(userID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Extract the user's name, email, address, and profile image URL from the document
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""
                    val address = document.getString("address") ?: ""
                    val profileImageUrl = document.getString("profileImageUrl")

                    // Set the EditText fields to the user's data
                    etUserName.setText(name)
                    etUserEmail.setText(email)
                    etUserAddress.setText(address)

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
                Toast.makeText(this, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        fileUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
        }

        // set click listener for logout button
        logoutButton.setOnClickListener {
            // sign out user and navigate to login page
            mAuth.signOut()
            val intent = Intent(this, WelcomePage::class.java)
            startActivity(intent)
            finish()
        }

        val btnUpdate = findViewById<Button>(R.id.btnUpdateUserProfile)
        btnUpdate.setOnClickListener {
            // Get the new name and address values from the EditText fields
            val newName = etUserName.text.toString().trim()
            val newAddress = etUserAddress.text.toString().trim()

            // Check if an image was selected
            if (selectedImageUri != null) {
                // Upload the selected image to Firebase Storage
                val storageRef = FirebaseStorage.getInstance().reference.child("images/${userID}/profile.jpg")
                storageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        // Get the download URL for the uploaded image
                        storageRef.downloadUrl
                            .addOnSuccessListener { downloadUri ->
                                // Update the user's data in Firestore with the download URL
                                val userMap = hashMapOf(
                                    "name" to newName,
                                    "address" to newAddress,
                                    "profileImageUrl" to downloadUri.toString()
                                )
                                db.collection("users").document(userID)
                                    .set(userMap, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "User data updated successfully", Toast.LENGTH_LONG).show()
                                        // Display the updated profile image in the ImageView
                                        Glide.with(this).load(downloadUri).into(userProfileImage)
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error updating user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Create a HashMap to store the new user data
                val userMap = HashMap<String, Any>()
                userMap["name"] = newName
                userMap["address"] = newAddress

                // Update the user's data in Firestore
                db.collection("users").document(userID)
                    .update(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "User data updated successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating user data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }



        // set click listener for logout button
        deleteButton.setOnClickListener {
            // Delete user from Firestore
            FirebaseFirestore.getInstance().collection("users")
                .document(userID)
                .delete()
                .addOnSuccessListener {
                    // User deleted from Firestore, now delete from Authentication
                    Firebase.auth.currentUser?.delete()
                        ?.addOnSuccessListener {
                            // User deleted from Authentication, now sign out and redirect to welcome page
                            Firebase.auth.signOut()
                            val intent = Intent(this, WelcomePage::class.java)
                            startActivity(intent)
                        }
                        ?.addOnFailureListener { e ->
                            // Handle any errors
                            Log.e("DeleteUser", "Error deleting user from Authentication: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    // Handle any errors
                    Log.e("DeleteUser", "Error deleting user from Firestore: ${e.message}")
                }
        }





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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                // Set the selected image URI to the variable
                selectedImageUri = uri
                userProfileImage.setImageURI(selectedImageUri)
            }
        }
    }
}