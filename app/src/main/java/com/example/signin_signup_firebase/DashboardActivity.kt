package com.example.signin_signup_firebase

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var dbReference: DatabaseReference
    private lateinit var tfName: TextView
    private lateinit var tfUsername: TextView
    private lateinit var tfEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tfName = findViewById(R.id.tfName)
        tfUsername = findViewById(R.id.tfUsername)
        tfEmail = findViewById(R.id.tfEmail)

        // Get username from the intent
        val username = intent.getStringExtra("USERNAME")

        if (username != null) {
            dbReference = FirebaseDatabase.getInstance().getReference("Users").child(username)

            dbReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            tfName.text = "Name: ${user.name}"
                            tfUsername.text = "Username: ${user.username}"
                            tfEmail.text = "Email: ${user.email}"
                        }
                    } else {
                        // Handle case where user does not exist
                        tfName.text = "User not found."
                        tfUsername.text = ""
                        tfEmail.text = ""
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors
                    tfName.text = "Error: ${error.message}"
                }
            })
        }

    }
}


//{
//    "rules": {
//    ".read": true,
//    ".write": true,
//    "Users": {
//    "$username": {
//    ".validate": "newData.exists() && newData.hasChildren(['name', 'username', 'email', 'password'])",
//
//    "name": {
//    ".validate": "newData.isString() && newData.val().length >= 2 && newData.val().length <= 50 && newData.val().matches(/^[a-zA-Z ]+$/)"
//},
//
//    "username": {
//    ".validate": "newData.isString() && newData.val().length >= 4 && newData.val().length <= 20 && newData.val().matches(/^[a-z0-9_]+$/)"
//},
//
//    "email": {
//    ".validate": "newData.isString() && newData.val().matches(/^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$/)"
//},
//
//    "password": {
//    ".validate": "newData.isString() && newData.val().length >= 8 && newData.val().length <= 20"
//}
//}
//}
//}
//}