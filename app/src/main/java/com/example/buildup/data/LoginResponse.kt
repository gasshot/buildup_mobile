package com.example.buildup.data

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
)