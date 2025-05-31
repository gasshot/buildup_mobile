package com.example.buildup.data

data class JoinRequest(
    val user_id: String,
    val user_pw: String,
    val user_nickname: String,
    val user_email: String,
    val user_sex: String,
    val user_birthdate: String
)

