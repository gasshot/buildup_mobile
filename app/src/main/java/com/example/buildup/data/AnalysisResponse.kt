package com.example.buildup.data

import com.google.gson.annotations.SerializedName

data class AnalysisResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("s3_url") val s3Url: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("personal_color_tone") val personalColorTone: String,
    @SerializedName("skin_analysis") val skinAnalysis: Map<String, Any>, // 피부 분석 결과는 맵으로 처리
    @SerializedName("db_timestamp") val dbTimestamp: String,
    @SerializedName("requester") val requester: String
)

