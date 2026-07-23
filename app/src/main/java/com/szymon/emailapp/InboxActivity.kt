package com.szymon.emailapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.mail.Folder
import javax.mail.internet.InternetAddress

class InboxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)

        val list = findViewById<RecyclerView>(R.id.emailList)
        val emptyText = findViewById<TextView>(R.id.emptyText)
        val fab = findViewById<FloatingActionButton>(R.id.composeFab)
        list.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            startActivity(Intent(this, ComposeActivity::class.java))
        }

        loadInbox(list, emptyText)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun loadInbox(list: RecyclerView, emptyText: TextView) {
        emptyText.text = "Loading inbox..."
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val store = MailConfig.imapSession().getStore("imaps")
                    store.connect(MailConfig.imapHost, MailConfig.email, MailConfig.password)
                    val folder = store.getFolder("INBOX")
                    folder.open(Folder.READ_ONLY)

                    val count = folder.messageCount
                    val start = maxOf(1, count - 29) // most recent 30 messages
                    val messages = folder.getMessages(start, count)

                    val items = messages.reversed().mapIndexed { idx, msg ->
                        val fromAddr = (msg.from?.firstOrNull() as? InternetAddress)?.address
                            ?: msg.from?.firstOrNull()?.toString() ?: "Unknown sender"
                        val subject = msg.subject ?: "(no subject)"
                        val date = msg.sentDate?.toString() ?: ""
                        val body = try {
                            extractBody(msg)
                        } catch (e: Exception) {
                            "(could not load body)"
                        }
                        EmailItem(msg.messageNumber, fromAddr, subject, date, body)
                    }

                    folder.close(false)
                    store.close()
                    Pair(items, null as String?)
                } catch (e: Exception) {
                    Pair(emptyList<EmailItem>(), e.message ?: "Failed to load inbox")
                }
            }

            val (items, error) = result
            if (error != null) {
                emptyText.text = "Error: $error"
                emptyText.visibility = TextView.VISIBLE
            } else if (items.isEmpty()) {
                emptyText.text = "No messages"
                emptyText.visibility = TextView.VISIBLE
            } else {
                emptyText.visibility = TextView.GONE
                list.adapter = EmailAdapter(items) { item ->
                    val intent = Intent(this@InboxActivity, MessageViewActivity::class.java)
                    intent.putExtra("from", item.from)
                    intent.putExtra("subject", item.subject)
                    intent.putExtra("date", item.date)
                    intent.putExtra("body", item.body)
                    startActivity(intent)
                }
            }
        }
    }

    private fun extractBody(part: javax.mail.Part): String {
        if (part.isMimeType("text/plain")) {
            return part.content as String
        }
        if (part.isMimeType("multipart/*")) {
            val mp = part.content as javax.mail.Multipart
            for (i in 0 until mp.count) {
                val bodyPart = mp.getBodyPart(i)
                if (bodyPart.isMimeType("text/plain")) {
                    return bodyPart.content as String
                }
            }
            // fallback: first part
            if (mp.count > 0) return extractBody(mp.getBodyPart(0))
        }
        return part.content?.toString() ?: ""
    }
}
