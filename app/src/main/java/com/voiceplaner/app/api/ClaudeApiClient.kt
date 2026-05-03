package com.voiceplaner.app.api

import com.voiceplaner.app.BuildConfig
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

interface ClaudeService {
    @POST("v1/messages")
    suspend fun sendMessage(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Body request: ClaudeRequest
    ): ClaudeResponse
}

@Singleton
class ClaudeApiClient @Inject constructor() {

    private val service: ClaudeService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClaudeService::class.java)
    }

    suspend fun parseTransaction(speechText: String): ParsedTransaction? = withContext(Dispatchers.IO) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

        val prompt = """
오늘 날짜: $today
어제 날짜: $yesterday

사용자가 말한 내용: "$speechText"

위 내용을 가계부 항목으로 파싱해서 반드시 아래 JSON 형식으로만 응답해. 다른 설명 없이 JSON만:
{
  "date": "YYYY-MM-DD",
  "type": "expense 또는 income",
  "amount": 숫자만,
  "description": "간결한 내역 설명",
  "category": "식비, 교통, 쇼핑, 의료, 문화, 기타 중 하나"
}

규칙:
- 지출/쓰다/먹다/샀다/결제 등 → expense
- 받다/입금/월급/용돈/벌다 등 → income
- "오늘" → $today, "어제" → $yesterday
- 금액은 숫자만 (원 단위)
        """.trimIndent()

        try {
            val response = service.sendMessage(
                apiKey = BuildConfig.CLAUDE_API_KEY,
                request = ClaudeRequest(messages = listOf(Message("user", prompt)))
            )
            val json = response.content.firstOrNull()?.text?.trim() ?: return@withContext null
            Gson().fromJson(json, ParsedTransaction::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
