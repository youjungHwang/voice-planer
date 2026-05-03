package com.voiceplaner.app.api

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalTransactionParser {

    fun parse(text: String): ParsedTransaction? {
        val amount = extractAmount(text) ?: return null
        return ParsedTransaction(
            date = extractDate(text),
            type = extractType(text),
            amount = amount,
            description = extractDescription(text),
            category = extractCategory(text)
        )
    }

    private fun extractAmount(text: String): Long? {
        val normalized = text.replace(",", "").replace(" ", "")

        // 숫자+원 직접 패턴 (예: 36200원)
        Regex("(\\d+)원").find(normalized)
            ?.groupValues?.get(1)?.toLongOrNull()?.let { return it }

        // 만/천/백 패턴 (예: 3만6천원, 5만원)
        var amount = 0L
        Regex("(\\d+)만").find(normalized)?.groupValues?.get(1)?.toLongOrNull()?.let { amount += it * 10000 }
        Regex("(\\d+)천").find(normalized)?.groupValues?.get(1)?.toLongOrNull()?.let { amount += it * 1000 }
        Regex("(\\d+)백").find(normalized)?.groupValues?.get(1)?.toLongOrNull()?.let { amount += it * 100 }
        if (amount > 0) return amount

        return null
    }

    private fun extractType(text: String): String {
        val incomeKeywords = listOf("받았", "입금", "월급", "용돈", "벌었", "들어왔", "생겼", "입급")
        if (incomeKeywords.any { text.contains(it) }) return "income"
        return "expense"
    }

    private fun extractDate(text: String): String {
        val today = LocalDate.now()
        return when {
            text.contains("그저께") || text.contains("그제") ->
                today.minusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE)
            text.contains("어제") ->
                today.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
            else ->
                today.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }

    private fun extractCategory(text: String): String {
        val categories = mapOf(
            "식비" to listOf("밥", "점심", "저녁", "아침", "먹", "식사", "카페", "커피", "피자", "치킨", "햄버거", "편의점", "음료", "배달", "음식"),
            "교통" to listOf("지하철", "버스", "택시", "기름", "주유", "교통", "기차", "ktx", "비행기"),
            "쇼핑" to listOf("샀", "구매", "쇼핑", "옷", "신발", "가방", "화장품", "쿠팡"),
            "의료" to listOf("병원", "약국", "약", "치과", "진료", "의료"),
            "문화" to listOf("영화", "공연", "책", "게임", "넷플릭스", "콘서트", "전시")
        )
        for ((category, keywords) in categories) {
            if (keywords.any { text.contains(it) }) return category
        }
        return "기타"
    }

    private fun extractDescription(text: String): String =
        text.replace(Regex("\\d+원"), "")
            .replace(Regex("\\d+만\\d*천?\\d*백?"), "")
            .replace("오늘", "").replace("어제", "").replace("그제", "").replace("그저께", "")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(30)
            .ifBlank { if (extractType(text) == "income") "수입" else "지출" }
}
