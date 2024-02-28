package com.example.loginapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginapplication.databinding.ActivitySignupBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference // to create connection to the database like refer data items.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.signupButton.setOnClickListener {
            val signupUsername = binding.username.text.toString()
            val signupPassword = binding.password.text.toString()
            val signupPhone = binding.phone.text.toString()
            val signupEmail = binding.email.text.toString()

            if (signupUsername.isNotEmpty() && signupPassword.isNotEmpty()) {
                signupUser(signupUsername, signupPassword, signupPhone, signupEmail)
            } else {
                Toast.makeText(this@SignupActivity, "All fields are mandatory", Toast.LENGTH_SHORT)
                    .show()

            }
        }

        binding.textView.setOnClickListener { //login redirect
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun signupUser(username: String, password: String, phoneNumber: String, email: String) {
        databaseReference.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(datasnapshot: DataSnapshot) { //dataSnapshot --> object that represent a specific location in firebase database
                    if (!datasnapshot.exists()) {
                        //create new user
                        val id = databaseReference.push().key //unique random string
                        val userData = UserData(id, username, password, phoneNumber, email)
                        databaseReference.child(id!!).setValue(userData)
                        Toast.makeText(this@SignupActivity, "Signup Successful", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@SignupActivity,
                            "User already exists",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@SignupActivity,
                        "Database Error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })
    }
}