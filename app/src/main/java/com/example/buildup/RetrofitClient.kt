package com.example.buildup

// RetrofitClient.kt

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // FastAPI 서버의 배포된 주소
    //private const val BASE_URL = "https://buildup-emuz.onrender.com"
    private const val BASE_URL ="http://54.206.9.203"
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
