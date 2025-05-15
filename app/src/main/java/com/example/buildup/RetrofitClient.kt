package com.example.buildup

// RetrofitClient.kt

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    //private const val BASE_URL = "http://127.0.0.1:8000" // 여기에 FastAPI 서버 주소를 입력하세요
    private const val BASE_URL = "http://10.0.2.2:8000" // 실제 서버 주소로 변경
    //private const val BASE_URL = "https://10.0.2.2:8000" // 개발용
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}