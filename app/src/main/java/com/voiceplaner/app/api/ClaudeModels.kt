package com.voiceplaner.app.api

import com.google.gson.annotations.SerializedName

data class ClaudeRequest(
    val model: String = "claude-haiku-4-5-20251001",
    @SerializedName("max_tokens") val maxTokens: Int = 512,
    val messages: List<Message>
)

data class Message(val role: String, val content: String)

data class ClaudeResponse(val content: List<ContentBlock>)

data class ContentBlock(val text: String)

data class ParsedTransaction(
    val date: String,
    val type: String,
    val amount: Long,
    val description: String,
    val category: String
)
