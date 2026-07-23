package com.szymon.emailapp

import java.io.Serializable

data class EmailItem(
    val messageNumber: Int,
    val from: String,
    val subject: String,
    val date: String,
    val body: String
) : Serializable
