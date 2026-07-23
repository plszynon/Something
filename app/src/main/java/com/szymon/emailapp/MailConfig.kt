package com.szymon.emailapp

import java.util.Properties
import javax.mail.PasswordAuthentication
import javax.mail.Session

/**
 * Holds the current user's mail credentials/config for the lifetime of the app process,
 * and builds JavaMail Session objects for IMAP and SMTP.
 */
object MailConfig {
    var email: String = ""
    var password: String = ""
    var imapHost: String = ""
    var imapPort: String = "993"
    var smtpHost: String = ""
    var smtpPort: String = "587"

    fun imapSession(): Session {
        val props = Properties()
        props["mail.store.protocol"] = "imaps"
        props["mail.imaps.host"] = imapHost
        props["mail.imaps.port"] = imapPort
        props["mail.imaps.ssl.enable"] = "true"
        return Session.getInstance(props)
    }

    fun smtpSession(): Session {
        val props = Properties()
        props["mail.smtp.host"] = smtpHost
        props["mail.smtp.port"] = smtpPort
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        return Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(email, password)
            }
        })
    }
}
