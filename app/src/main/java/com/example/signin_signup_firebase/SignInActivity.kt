package com.example.signin_signup_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {

    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val tfUsername = findViewById<TextInputEditText>(R.id.tfUsername)
        val tfPassword = findViewById<TextInputEditText>(R.id.tfPassword)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        btnSignIn.setOnClickListener {
            val username = tfUsername.text.toString().trim()
            val password = tfPassword.text.toString().trim()

            // Validate if any field is empty
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate username (4 to 20 characters, only lowercase letters, digits, and underscores)
            val usernamePattern = "^[a-z0-9_]{4,20}$"
            if (!username.matches(Regex(usernamePattern))) {
                Toast.makeText(applicationContext, "Username must be between 4 and 20 characters long and can only contain lowercase letters, digits, and underscores.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate password (8 to 20 characters, at least 1 uppercase, 1 lowercase, 1 digit, and 1 special character)
            if (password.length < 8 || password.length > 20) {
                Toast.makeText(applicationContext, "Password must be between 8 and 20 characters long.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,20}$"
            if (!password.matches(Regex(passwordPattern))) {
                Toast.makeText(applicationContext, "Password must contain at least 1 uppercase, 1 lowercase, 1 digit, and 1 special character.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dbReference = FirebaseDatabase.getInstance().getReference("Users")

            dbReference.child(username).get().addOnSuccessListener {
                if (it.exists()) {
                    val storedPassword = it.child("password").value.toString()
                    if (password == storedPassword) {
                        Toast.makeText(applicationContext, "Sign in successful.", Toast.LENGTH_SHORT).show()

                        // Redirect to the DashboardActivity
                        val moveToDashboardActivity = Intent(applicationContext, DashboardActivity::class.java)

                        // Pass username to DashboardActivity
                        moveToDashboardActivity.putExtra("USERNAME", username)

                        // Display the DashboardActivity
                        startActivity(moveToDashboardActivity)

                        // Close the sign-in activity
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Username not found. Please sign up first.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Error checking username. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        tvSignUp.setOnClickListener {
            val moveToSignUpActivity = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(moveToSignUpActivity)
            finish()
        }
    }
}
