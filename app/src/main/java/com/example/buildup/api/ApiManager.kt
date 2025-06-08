package com.example.buildup.api

import com.example.buildup.data.AnalysisResponse
import com.example.buildup.data.CheckPWRequest
import com.example.buildup.data.CheckPWResponse
import com.example.buildup.data.JoinRequest
import com.example.buildup.data.JoinResponse
import com.example.buildup.data.LoginRequest
import com.example.buildup.data.LoginResponse
import com.example.buildup.data.PastAnalysisRequest
import com.example.buildup.data.PastAnalysisResponse
import com.example.buildup.data.ServerResponse
import com.example.buildup.data.SkinAdviceRequest
import com.example.buildup.data.SkinAdviceResponse
import com.example.buildup.data.UpdateNicknameRequest
import com.example.buildup.data.UpdatePWRequest
import com.example.buildup.data.UpdatePWResponse
import com.example.buildup.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


// 비동기 통신 임포트
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

object ApiManager {

    fun loginUser(loginRequest: LoginRequest, onResult: (Boolean, User?, String?) -> Unit) {
        RetrofitClient.instance.loginUser(loginRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.success == true) {
                            onResult(true, loginResponse.user, null)
                        } else {
                            onResult(false, null, loginResponse?.message ?: "알 수 없는 오류")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage =
                            "로그인 요청 실패: ${response.code()} - ${errorBody ?: "알 수 없는 오류"}"
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
                override fun onResponse(
                    call: Call<JoinResponse>,
                    response: Response<JoinResponse>
                ) {
                    if (response.isSuccessful) {
                        val joinResponse = response.body()
                        if (joinResponse?.success == true) {
                            callback(true, null)
                        } else {
                            callback(false, joinResponse?.message ?: "회원가입 실패")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage =
                            "서버 에러: ${response.code()} - ${errorBody ?: "Unknown error"}"
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
                override fun onResponse(
                    call: Call<CheckPWResponse>,
                    response: Response<CheckPWResponse>
                ) {
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
                override fun onResponse(
                    call: Call<UpdatePWResponse>,
                    response: Response<UpdatePWResponse>
                ) {
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

    fun updateNickname(
        updateNicknameRequest: UpdateNicknameRequest,
        callback: (success: Boolean) -> Unit
    ) {
        // RetrofitClient를 통해 서버의 updatePW 엔드포인트에 요청
        RetrofitClient.instance.updatePW(updateNicknameRequest)
            .enqueue(object : Callback<ServerResponse> {

                /**
                 * 서버로부터의 응답이 성공적으로 도착한 경우 호출되는 메서드
                 *
                 * @param call 요청 객체
                 * @param response 서버로부터 받은 응답 객체
                 */
                override fun onResponse(
                    call: Call<ServerResponse>,
                    response: Response<ServerResponse>
                ) {
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

    fun uploadAndAnalyzeImage(
        filePath: String,
        userId: String,
        callback: (Boolean, Map<String, Any>?, String?) -> Unit
    ) {
        val file = File(filePath)
        if (!file.exists()) {
            callback(false, null, "파일이 존재하지 않습니다.")
            return
        }

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val userIdBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

        RetrofitClient.instance.uploadImage(body, userIdBody)
            .enqueue(object : Callback<AnalysisResponse> {
                override fun onResponse(
                    call: Call<AnalysisResponse>,
                    response: Response<AnalysisResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body() // 서버에서 응답으로 받은 데이터
                        if (result != null) {
                            val resultMap = mapOf(
                                "personal_color_tone" to result.personalColorTone,
                                "skin_Analysis" to result.skinAnalysis,
                                "created_at" to result.createdAt,
                                "s3_url" to result.s3Url
                            )
                            callback(true, resultMap, null)
                        } else {
                            callback(false, null, "응답 데이터가 없습니다.")
                        }
                    } else {
                        callback(false, null, "HTTP 오류: ${response.code()} - ${response.errorBody()?.string() ?: "알 수 없는 오류"}")
                    }
                }

                override fun onFailure(call: Call<AnalysisResponse>, t: Throwable) {
                    callback(false, null, "네트워크 오류: ${t.message}")
                }
            })
    }

    fun fetchSkinAdvice(
        userId: String,
        predictedSkinType: String,
        personalColorTone: String,
        callback: (success: Boolean, advice: String?) -> Unit
    ) {
        val request = SkinAdviceRequest(userId, predictedSkinType, personalColorTone)

        RetrofitClient.instance.getSkinAdvice(request)
            .enqueue(object : Callback<SkinAdviceResponse> {
                override fun onResponse(
                    call: Call<SkinAdviceResponse>,
                    response: Response<SkinAdviceResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        callback(true, response.body()?.advice)
                    } else {
                        callback(false, null)
                    }
                }

                override fun onFailure(call: Call<SkinAdviceResponse>, t: Throwable) {
                    callback(false, null)
                }
            })
    }

    fun fetchAnalysisData(
        userId: String,
        callback: (success: Boolean, response: PastAnalysisResponse?) -> Unit
    ) {
        val request = PastAnalysisRequest(userId)

        RetrofitClient.instance.fetchAnalysisData(request)
            .enqueue(object : Callback<PastAnalysisResponse> {
                override fun onResponse(
                    call: Call<PastAnalysisResponse>,
                    response: Response<PastAnalysisResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        // 성공적으로 데이터를 받아온 경우
                        callback(true, response.body())
                    } else {
                        // 응답은 성공적이지만 데이터가 없는 경우
                        callback(false, null)
                    }
                }

                override fun onFailure(call: Call<PastAnalysisResponse>, t: Throwable) {
                    // 네트워크 또는 기타 요청 실패 처리
                    t.printStackTrace() // 로그 출력
                    callback(false, null)
                }
            })
    }


}
