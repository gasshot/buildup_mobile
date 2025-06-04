package com.example.buildup.data

data class UpdatePWRequest(
    val user_id: String,
    val user_pw: String,
    val user_new_pw: String
)
