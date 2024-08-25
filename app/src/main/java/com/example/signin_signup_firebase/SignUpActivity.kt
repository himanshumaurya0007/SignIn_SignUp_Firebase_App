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

class SignUpActivity : AppCompatActivity() {

    private lateinit var dbReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val tfName = findViewById<TextInputEditText>(R.id.tfName)
        val tfUsername = findViewById<TextInputEditText>(R.id.tfUsername)
        val tfEmail = findViewById<TextInputEditText>(R.id.tfEmail)
        val tfPassword = findViewById<TextInputEditText>(R.id.tfPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)

        btnSignUp.setOnClickListener {
            val name = tfName.text.toString().trim()
            val username = tfUsername.text.toString().trim()
            val email = tfEmail.text.toString().trim()
            val password = tfPassword.text.toString().trim()

            // Validate if any field is empty
            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate name (2 to 50 characters, only letters)
            if (name.length < 2 || name.length > 50 || !name.matches(Regex("^[a-zA-Z ]+$"))) {
                Toast.makeText(applicationContext, "Name must be between 2 and 50 characters long and contain only letters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate username (4 to 20 characters, only lowercase letters, digits, and underscores)
            val usernamePattern = "^[a-z0-9_]+$"
            if (!username.matches(Regex(usernamePattern)) || username.length < 4 || username.length > 20) {
                Toast.makeText(applicationContext, "Username must be between 4 and 20 characters long and can only contain lowercase letters, digits, and underscores.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(applicationContext, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
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

            val user = User(name, username, email, password)

            dbReference = FirebaseDatabase.getInstance().getReference("Users") 
            
            dbReference.child(username).get().addOnSuccessListener {
                if (it.exists()) {
                    Toast.makeText(applicationContext, "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show()
                } else {
                    // Proceed to save the user
                    dbReference.child(username).setValue(user).addOnSuccessListener {
                        Toast.makeText(
                            applicationContext,
                            "Congratulations! Your registration is successful.\nYou can Sign In now.",
                            Toast.LENGTH_SHORT
                        ).show()
                        tfName.text?.clear()
                        tfUsername.text?.clear()
                        tfEmail.text?.clear()
                        tfPassword.text?.clear()
                        tfName.requestFocus()
                    }.addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "Sorry! Your registration is unsuccessful.\nPlease, try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Error checking username availability.", Toast.LENGTH_SHORT).show()
            }
        }

        tvSignIn.setOnClickListener{
            val moveToSignInActivity = Intent(applicationContext, SignInActivity::class.java)
            startActivity(moveToSignInActivity)
        }

    }
}