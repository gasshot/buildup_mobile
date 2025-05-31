package com.example.buildup.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // FastAPI 서버의 배포된 주소
    private const val BASE_URL = "https://exotic-broadly-eel.ngrok-free.app/"

    // Interceptor를 사용하여 요청 헤더에 ngrok-skip-browser-warning 추가
    private val interceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val modifiedRequest = originalRequest.newBuilder()
            .addHeader("ngrok-skip-browser-warning", "true")
            .build()
        chain.proceed(modifiedRequest)
    }

    // OkHttpClient 생성
    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    // Retrofit 인스턴스 생성
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // 커스텀 OkHttpClient 설정
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

