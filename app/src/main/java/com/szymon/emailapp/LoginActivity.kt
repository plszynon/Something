package com.szymon.emailapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val imapHostInput = findViewById<EditText>(R.id.imapHostInput)
        val imapPortInput = findViewById<EditText>(R.id.imapPortInput)
        val smtpHostInput = findViewById<EditText>(R.id.smtpHostInput)
        val smtpPortInput = findViewById<EditText>(R.id.smtpPortInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val statusText = findViewById<TextView>(R.id.statusText)

        loginButton.setOnClickListener {
            MailConfig.email = emailInput.text.toString().trim()
            MailConfig.password = passwordInput.text.toString()
            MailConfig.imapHost = imapHostInput.text.toString().trim()
            MailConfig.imapPort = imapPortInput.text.toString().trim()
            MailConfig.smtpHost = smtpHostInput.text.toString().trim()
            MailConfig.smtpPort = smtpPortInput.text.toString().trim()

            if (MailConfig.email.isEmpty() || MailConfig.password.isEmpty() ||
                MailConfig.imapHost.isEmpty() || MailConfig.smtpHost.isEmpty()) {
                statusText.text = "Please fill in all fields"
                return@setOnClickListener
            }

            statusText.text = "Connecting..."
            loginButton.isEnabled = false

            lifecycleScope.launch {
                val error = withContext(Dispatchers.IO) {
                    try {
                        val store = MailConfig.imapSession().getStore("imaps")
                        store.connect(MailConfig.imapHost, MailConfig.email, MailConfig.password)
                        store.close()
                        null
                    } catch (e: Exception) {
                        e.message ?: "Connection failed"
                    }
                }

                loginButton.isEnabled = true
                if (error == null) {
                    startActivity(Intent(this@LoginActivity, InboxActivity::class.java))
                } else {
                    statusText.text = "Login failed: $error"
                }
            }
        }
    }
}
