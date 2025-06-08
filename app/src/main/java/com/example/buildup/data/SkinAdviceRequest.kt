package com.example.buildup.data

data class SkinAdviceRequest(
    val user_id: String,
    val predicted_skin_type: String,
    val personal_color_tone: String
)
