package com.example.buildup.data

data class PastAnalysisResponse(
    val success: Boolean,
    val message: String,
    val data: List<Map<String, Any>>? = null // 데이터가 없을 수도 있음
)
