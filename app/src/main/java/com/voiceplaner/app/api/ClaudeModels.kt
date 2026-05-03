package com.voiceplaner.app.api

data class ParsedTransaction(
    val date: String,
    val type: String,
    val amount: Long,
    val description: String,
    val category: String
)
