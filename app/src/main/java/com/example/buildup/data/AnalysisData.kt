package com.example.buildup.data

// 데이터 모델
data class AnalysisData(
    val id: String,
    val name: String, // 시:분:초
    val date: String, // 연월일
    val scores: Map<String, Float>
)
