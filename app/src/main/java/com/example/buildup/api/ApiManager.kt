package com.example.buildup.api

import com.example.buildup.data.CheckPWRequest
import com.example.buildup.data.CheckPWResponse
import com.example.buildup.data.JoinRequest
import com.example.buildup.data.JoinResponse
import com.example.buildup.data.LoginRequest
import com.example.buildup.data.LoginResponse
import com.example.buildup.data.ServerResponse
import com.example.buildup.data.UpdateNicknameRequest
import com.example.buildup.data.UpdatePWRequest
import com.example.buildup.data.UpdatePWResponse
import com.example.buildup.data.User


// 비동기 통신 임포트
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ApiManager {

    fun loginUser(loginRequest: LoginRequest, onResult: (Boolean, User?, String?) -> Unit) {
        RetrofitClient.instance.loginUser(loginRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.success == true) {
                            onResult(true, loginResponse.user, null)
                        } else {
                            onResult(false, null, loginResponse?.message ?: "알 수 없는 오류")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = "로그인 요청 실패: ${response.code()} - ${errorBody ?: "알 수 없는 오류"}"
                        onResult(false, null, errorMessage)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    onResult(false, null, "네트워크 오류: ${t.message}")
                }
            })
    }

    fun joinUser(
        joinRequest: JoinRequest,
        callback: (success: Boolean, message: String?) -> Unit
    ) {
        RetrofitClient.instance.joinUser(joinRequest)
            .enqueue(object : Callback<JoinResponse> {
                override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
                    if (response.isSuccessful) {
                        val joinResponse = response.body()
                        if (joinResponse?.success == true) {
                            callback(true, null)
                        } else {
                            callback(false, joinResponse?.message ?: "회원가입 실패")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = "서버 에러: ${response.code()} - ${errorBody ?: "Unknown error"}"
                        callback(false, errorMessage)
                    }
                }

                override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
                    callback(false, "네트워크 오류: ${t.message}")
                }
            })
    }

    fun checkPW(checkRequest: CheckPWRequest, callback: (success: Boolean) -> Unit) {
        RetrofitClient.instance.checkPW(checkRequest)
            .enqueue(object : Callback<CheckPWResponse> {
                override fun onResponse(call: Call<CheckPWResponse>, response: Response<CheckPWResponse>) {
                    callback(response.isSuccessful && response.body()?.success == true)
                }

                override fun onFailure(call: Call<CheckPWResponse>, t: Throwable) {
                    callback(false)
                }
            })
    }

    fun updatePassword(updatePWRequest: UpdatePWRequest, callback: (success: Boolean) -> Unit) {
        // RetrofitClient를 통해 서버의 updatePW 엔드포인트에 요청
        RetrofitClient.instance.updatePW(updatePWRequest)
            .enqueue(object : Callback<UpdatePWResponse> {

                /**
                 * 서버로부터의 응답이 성공적으로 도착한 경우 호출되는 메서드
                 *
                 * @param call 요청 객체
                 * @param response 서버로부터 받은 응답 객체
                 */
                override fun onResponse(call: Call<UpdatePWResponse>, response: Response<UpdatePWResponse>) {
                    // 응답이 성공적이고, 서버에서 success 플래그를 true로 반환한 경우
                    callback(response.isSuccessful && response.body()?.success == true)
                }

                /**
                 * 서버 요청이 실패했을 경우 호출되는 메서드
                 *
                 * @param call 요청 객체
                 * @param t 발생한 오류 (Throwable 객체)
                 */
                override fun onFailure(call: Call<UpdatePWResponse>, t: Throwable) {
                    // 요청이 실패했으므로 callback에 false 전달
                    callback(false)
                }
            })
    }

    fun updateNickname(updateNicknameRequest: UpdateNicknameRequest, callback: (success: Boolean) -> Unit) {
        // RetrofitClient를 통해 서버의 updatePW 엔드포인트에 요청
        RetrofitClient.instance.updatePW(updateNicknameRequest)
            .enqueue(object : Callback<ServerResponse> {

                /**
                 * 서버로부터의 응답이 성공적으로 도착한 경우 호출되는 메서드
                 *
                 * @param call 요청 객체
                 * @param response 서버로부터 받은 응답 객체
                 */
                override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                    // 응답이 성공적이고, 서버에서 success 플래그를 true로 반환한 경우
                    callback(response.isSuccessful && response.body()?.success == true)
                }

                /**
                 * 서버 요청이 실패했을 경우 호출되는 메서드
                 *
                 * @param call 요청 객체
                 * @param t 발생한 오류 (Throwable 객체)
                 */
                override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                    // 요청이 실패했으므로 callback에 false 전달
                    callback(false)
                }
            })
    }
}