package com.example.buildup.api

import com.example.buildup.data.AnalysisResponse
import com.example.buildup.data.LoginResponse
import com.example.buildup.data.CheckIDRequest
import com.example.buildup.data.CheckIDResponse
import com.example.buildup.data.CheckPWRequest
import com.example.buildup.data.CheckPWResponse
import com.example.buildup.data.JoinRequest
import com.example.buildup.data.JoinResponse
import com.example.buildup.data.LoginRequest
import com.example.buildup.data.ServerResponse
import com.example.buildup.data.UpdateNicknameRequest
import com.example.buildup.data.UpdatePWRequest
import com.example.buildup.data.UpdatePWResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @GET("/test") // FastAPI 서버의 '/test' 엔드포인트에 GET 요청을 보냅니다.
    fun testConnection(): Call<String> // 서버로부터 String 형태의 응답을 받을 것으로 예상합니다.

    @POST("/user/login") // 로그인 엔드포인트 (POST 방식)
    fun loginUser(@Body loginData: LoginRequest): Call<LoginResponse> // 요청 데이터와 응답 데이터 타입 정의

    @POST("/user/join") // 회원가입 엔드포인트 (POST 방식)
    fun joinUser(@Body joinData: JoinRequest): Call<JoinResponse> // 요청 데이터와 응답 데이터 타입 정의

    @POST("/user/check-id") // 아이디 확인 엔드포인트 (POST 방식)
    fun checkID(@Body checkData: CheckIDRequest): Call<CheckIDResponse> // 요청 데이터와 응답 데이터 타입 정의

    @POST("/user/check-pw") // 비밀번호 확인 엔드포인트 (POST 방식)
    fun checkPW(@Body checkData: CheckPWRequest): Call<CheckPWResponse> // 요청 데이터와 응답 데이터 타입 정의

    @POST("/user/update-pw") // 비밀번호 수정 엔드포인트 (POST 방식)
    fun updatePW(@Body checkData: UpdatePWRequest): Call<UpdatePWResponse> // 요청 데이터와 응답 데이터 타입 정의

    @POST("/user/update-nickname") // 닉네임 수정 엔드포인트 (POST 방식)
    fun updatePW(@Body checkData: UpdateNicknameRequest): Call<ServerResponse> // 요청 데이터와 응답 데이터 타입 정의

    @Multipart
    @POST("/images/upload-and-analyze")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("user_id") userId: RequestBody
    ): Call<AnalysisResponse>
}