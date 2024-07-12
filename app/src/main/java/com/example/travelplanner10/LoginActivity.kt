package com.example.travelplanner10

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Aggiungo questa activity al ActivityCollector
        ActivityCollector.addActivity(this)

        dbHelper = UserDatabaseHelper(this)

        // Esempio di inserimento di un utente (da rimuovere dopo aver inserito utenti reali)
        //dbHelper.insertUser("admin", "admin")


        val loginButton: Button = findViewById(R.id.login_button)
        val usernameEditText: EditText = findViewById(R.id.et_username)
        val passwordEditText: EditText = findViewById(R.id.et_password)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Codice per verificare le credenziali di login
            val isSuccess = dbHelper.checkUser(username, password)

            if (isSuccess) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }
}
