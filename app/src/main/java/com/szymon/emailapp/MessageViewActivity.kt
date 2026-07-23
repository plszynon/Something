package com.szymon.emailapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MessageViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_view)

        findViewById<TextView>(R.id.subjectText).text = intent.getStringExtra("subject") ?: ""
        findViewById<TextView>(R.id.fromText).text = "From: " + (intent.getStringExtra("from") ?: "")
        findViewById<TextView>(R.id.dateText).text = intent.getStringExtra("date") ?: ""
        findViewById<TextView>(R.id.bodyText).text = intent.getStringExtra("body") ?: ""
    }
}
