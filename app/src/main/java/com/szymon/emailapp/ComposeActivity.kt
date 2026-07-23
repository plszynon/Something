package com.szymon.emailapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.mail.Message
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class ComposeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        val toInput = findViewById<EditText>(R.id.toInput)
        val subjectInput = findViewById<EditText>(R.id.subjectInput)
        val bodyInput = findViewById<EditText>(R.id.bodyInput)
        val sendButton = findViewById<Button>(R.id.sendButton)
        val statusText = findViewById<TextView>(R.id.statusText)

        sendButton.setOnClickListener {
            val to = toInput.text.toString().trim()
            val subject = subjectInput.text.toString()
            val body = bodyInput.text.toString()

            if (to.isEmpty()) {
                statusText.text = "Recipient required"
                return@setOnClickListener
            }

            sendButton.isEnabled = false
            statusText.text = "Sending..."

            lifecycleScope.launch {
                val error = withContext(Dispatchers.IO) {
                    try {
                        val session = MailConfig.smtpSession()
                        val message = MimeMessage(session)
                        message.setFrom(InternetAddress(MailConfig.email))
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                        message.subject = subject
                        message.setText(body)
                        Transport.send(message)
                        null
                    } catch (e: Exception) {
                        e.message ?: "Send failed"
                    }
                }

                sendButton.isEnabled = true
                if (error == null) {
                    statusText.text = "Sent!"
                    finish()
                } else {
                    statusText.text = "Error: $error"
                }
            }
        }
    }
}
