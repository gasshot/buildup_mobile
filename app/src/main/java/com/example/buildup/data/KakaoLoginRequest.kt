package com.example.buildup.data

import com.kakao.sdk.user.model.Gender

data class KakaoLoginRequest(
    val kakaoId: String,          // 카카오 회원번호 (필수)
    val email: String?,           // 이메일 (동의 시 null 아님)
    val nickname: String?,        // 닉네임 (동의 시 null 아님)
    val accessToken: String,       // 카카오 OAuth 토큰, 백엔드에서 유효성 검증용
    val user_sex: Gender,
    val user_birthdate: String
    // 필요한 경우 추가 필드 (예: 프로필 이미지 URL 등)
)
