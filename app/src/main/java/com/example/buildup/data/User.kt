package com.example.buildup.data

data class User(
    val id: String?,
    val nickname: String?,
    val email: String?,
    val sex: String?,
    val birthdate: String?,
    val joinDate: String?,
    val role: String?,
    val empID: String // 서버 코드에는 항상 ""으로 반환되므로 String 타입으로 정의
)

