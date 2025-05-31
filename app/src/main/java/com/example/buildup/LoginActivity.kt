package com.example.buildup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.activities.join.Join1Activity
import com.example.buildup.api.RetrofitClient
import com.example.buildup.data.KakaoLoginRequest
import com.example.buildup.data.LoginRequest
import com.example.buildup.data.LoginResponse
import com.example.buildup.data.User
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

// 비동기 통신 임포트
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: User 데이터 클래스 정의 (예시)
// data class User(val id: String?, val name: String?, val role: String?)
// TODO: LoginResponse 데이터 클래스 정의 (예시)
// data class LoginResponse(val success: Boolean, val message: String?, val user: User?)
// TODO: LoginRequest 데이터 클래스 정의 (예시)
// data class LoginRequest(val userId: String, val userPw: String)


class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity" // TAG 상수 정의

    private lateinit var welcomeText: TextView
    private lateinit var buttonStart: TextView
    private lateinit var editTextUserId: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonEditorLogin: TextView
    private lateinit var backgroundImageView: ImageView

    private lateinit var buttonLoginGoogle: Button
    private lateinit var buttonLoginKakao: Button


    private val images = listOf(
        R.drawable.edit_jwy4,
        R.drawable.edit_jwy5,
        R.drawable.edit_jwy6
    )
    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private val IMAGE_ROTATION_DELAY = 5000L // 5초
    private val ANIMATION_DURATION = 3200L // 페이드 애니메이션 지속 시간 (3.2초)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 🌟🌟🌟 바로 여기에 scopes를 정의하고 사용합니다! 🌟🌟🌟
        val scopes = listOf("account_email", "gender", "birthday", "birthyear") // 필요한 동의 항목 지정

        // UI 요소 찾기
        welcomeText = findViewById(R.id.welcomeText)
        buttonStart = findViewById(R.id.signupText)
        editTextUserId = findViewById(R.id.editTextUserId)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonEditorLogin = findViewById(R.id.editorLogin)

        buttonLoginGoogle = findViewById(R.id.buttonLoginGoogle)
        buttonLoginKakao = findViewById(R.id.buttonLoginKakao)

        // View 연결
        backgroundImageView = findViewById<ImageView>(R.id.backgroundImageView)

        // 5초마다 이미지 변경
        startImageRotationWithAnimation()

        // 개발 중 키 해시 확인용 코드 (배포 시 주석 처리 또는 삭제)
        // val keyHash = Utility.getKeyHash(this)
        // Log.i(TAG, "keyHash: $keyHash")


        // --- 일반 회원가입 버튼 이벤트 ---
        buttonStart.setOnClickListener {
            val intent = Intent(this@LoginActivity, Join1Activity::class.java)
            startActivity(intent)
            finish()
        }

        // --- 에디터 자동 로그인 버튼 이벤트 (개발/테스트용, 배포 시 제거) ---
        buttonEditorLogin.setOnClickListener {
            val userId = "kanginoh" // 하드코딩된 ID (개발용)
            val userPw = "12345" // 하드코딩된 PW (개발용)

            val loginRequest = LoginRequest(userId, userPw)
            performLogin(loginRequest)
        }

        // --- 일반 로그인 버튼 이벤트 ---
        buttonLogin.setOnClickListener {
            val userId = editTextUserId.text.toString().trim()
            val userPw = editTextPassword.text.toString().trim()

            if (userId.isEmpty() || userPw.isEmpty()) {
                Toast.makeText(this@LoginActivity, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(userId, userPw)
            performLogin(loginRequest)
        }


        // --- 소셜 로그인 버튼 이벤트 설정 ---
        buttonLoginGoogle.setOnClickListener {
            // TODO: 구글 로그인 로직 구현
            Toast.makeText(this, "구글 로그인 버튼 클릭", Toast.LENGTH_SHORT).show()
            // 여기에 GoogleSignInClient를 사용하여 로그인 흐름 시작 코드 추가
            // val signInIntent = mGoogleSignInClient.signInIntent
            // startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        buttonLoginKakao.setOnClickListener {
            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오계정으로 로그인 실패", error)
                    Toast.makeText(this@LoginActivity, "카카오계정 로그인 실패", Toast.LENGTH_SHORT).show()
                } else if (token != null) {
                    Log.i(TAG, "카카오계정으로 로그인 성공: ${token.accessToken}")
                    // TODO: 카카오 로그인 성공 후 사용자 정보 요청 및 백엔드 전달
                    getKakaoUserInfoAndProceed(token.accessToken)
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) { // context 대신 this
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error -> // context 대신 this
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            Toast.makeText(this@LoginActivity, "카카오톡 로그인 취소", Toast.LENGTH_SHORT).show()
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        Toast.makeText(this@LoginActivity, "카카오톡 로그인 실패, 카카오계정으로 시도", Toast.LENGTH_SHORT).show()
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoLoginCallback) // context 대신 this
                    } else if (token != null) {
                        Log.i(TAG, "카카오톡으로 로그인 성공: ${token.accessToken}")
                        // TODO: 카카오 로그인 성공 후 사용자 정보 요청 및 백엔드 전달
                        getKakaoUserInfoAndProceed(token.accessToken)
                    }
                }
            } else {
                Toast.makeText(this@LoginActivity, "카카오톡 미설치, 카카오계정으로 로그인", Toast.LENGTH_SHORT).show()
                UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoLoginCallback) // context 대신 this
            }
        }
    } // end of onCreate


    // --- 이미지 로테이션 애니메이션 ---
    private fun startImageRotationWithAnimation() {
        handler.post(object : Runnable {
            override fun run() {
                // 현재 이미지를 페이드 아웃
                backgroundImageView.animate()
                    .alpha(0f) // 투명도를 0으로
                    .setDuration(ANIMATION_DURATION / 2) // 페이드 아웃 시간 (총 시간의 절반)
                    .withEndAction {
                        // 페이드 아웃이 끝난 후 다음 이미지로 변경하고 페이드 인
                        currentIndex = (currentIndex + 1) % images.size
                        backgroundImageView.setImageResource(images[currentIndex])

                        backgroundImageView.animate()
                            .alpha(1f) // 투명도를 1 (불투명)으로
                            .setDuration(ANIMATION_DURATION / 2) // 페이드 인 시간
                            .start()
                    }
                    .start()

                // 다음 이미지 전환을 위한 딜레이 (애니메이션 시간 고려)
                handler.postDelayed(this, IMAGE_ROTATION_DELAY)
            }
        })
    }

    // --- 일반/에디터 로그인 처리 공통 함수 ---
    private fun performLogin(loginRequest: LoginRequest) {
        RetrofitClient.instance.loginUser(loginRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.success == true) {
                            val user = loginResponse.user
                            handleLoginSuccess(user)
                        } else {
                            Toast.makeText(this@LoginActivity, "로그인 실패: ${loginResponse?.message ?: "알 수 없는 오류"}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = "로그인 요청 실패: ${response.code()} - ${errorBody ?: "알 수 없는 오류"}"
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // --- 로그인 성공 후 공통 처리 함수 ---
    private fun handleLoginSuccess(user: User?) {
        Toast.makeText(this@LoginActivity, "${user?.name ?: "사용자"}님, 로그인 성공!", Toast.LENGTH_SHORT).show()

        // SharedPreferences에 사용자 정보 저장
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", user?.id)
        editor.putString("userName", user?.name)
        editor.putString("userRole", user?.role)
        editor.apply()

        // 다음 화면으로 이동
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // --- 카카오 로그인 성공 후 사용자 정보 요청 및 백엔드 전달 함수 ---
    private fun getKakaoUserInfoAndProceed(accessToken: String) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "카카오 사용자 정보 요청 실패", error)
                Toast.makeText(this@LoginActivity, "카카오 사용자 정보 가져오기 실패", Toast.LENGTH_SHORT).show()
            } else if (user != null) {
                Log.i(TAG, "카카오 사용자 정보 요청 성공: ${user.id} ${user.kakaoAccount?.email} ${user.kakaoAccount?.profile?.nickname}")

                // TODO: 여기에 카카오 사용자 정보 (user.id, user.kakaoAccount?.email, user.kakaoAccount?.profile?.nickname 등)
                // TODO: 와 함께 accessToken을 백엔드로 전달하는 API 호출 로직 구현

                val kakaoId = user.id.toString()
                val email = user.kakaoAccount?.email
                val nickname = user.kakaoAccount?.profile?.nickname

//                if(kakaoId == "DB안에 존재한다면"){
//                    // 로그인 처리 하고 메인페이지로 이동
//
//                    // SharedPreferences에 사용자 정보 저장
//                    val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
//                    val editor = sharedPreferences.edit()
//                    editor.putString("userId", kakaoId)
//                    editor.putString("userName", nickname)
//                    editor.putString("userEmail", email)
//                    editor.apply()
//
//                    // 다음 화면으로 이동
//                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
                

                val gender = user.kakaoAccount?.gender // "male", "female" 또는 null

                // 생년월일 변환 (YYYY-MM-DD 형식)
                var formattedBirthdate: String? = null
                val birthyear = user.kakaoAccount?.birthyear // "YYYY" 형식 또는 null
                val birthday = user.kakaoAccount?.birthday // "MMdd" 형식 또는 null

                if (birthyear != null && birthday != null && birthday.length == 4) {
                    try {
                        val month = birthday.substring(0, 2)
                        val day = birthday.substring(2, 4)
                        formattedBirthdate = "$birthyear-$month-$day"
                    } catch (e: Exception) {
                        Log.e(TAG, "생년월일 형식 변환 오류: $e")
                        formattedBirthdate = null
                    }
                }

                // 🌟🌟🌟 여기부터 추가하시면 됩니다 🌟🌟🌟
                // Logcat으로 변수 값 출력
                Log.d(TAG, "--- 카카오 로그인 변수 테스트 ---")
                Log.d(TAG, "kakaoId: $kakaoId")
                Log.d(TAG, "email: $email")
                Log.d(TAG, "nickname: $nickname")
                Log.d(TAG, "gender: $gender")
                Log.d(TAG, "birthyear (원본): $birthyear")
                Log.d(TAG, "birthday (원본): $birthday")
                Log.d(TAG, "formattedBirthdate (YYYY-MM-DD): $formattedBirthdate")
                Log.d(TAG, "--------------------------")
                // 🌟🌟🌟 여기까지 추가하시면 됩니다 🌟🌟🌟


//                // --- 필수 정보 체크 및 추가 정보 입력 화면으로 이동 로직 ---
//                // 백엔드에서 성별과 생년월일을 필수로 요구하는 경우
//                if (gender == null || formattedBirthdate == null) {
//                    val intent = Intent(this@LoginActivity, AdditionalInfoActivity::class.java).apply {
//                        putExtra("kakaoId", kakaoId)
//                        putExtra("email", email)
//                        putExtra("nickname", nickname)
//                        putExtra("accessToken", accessToken)
//                        // 현재까지 얻은 정보도 함께 넘겨주기 (AdditionalInfoActivity에서 마저 입력받을 수 있도록)
//                        putExtra("gender_from_kakao", gender) // 카카오에서 받은 성별 (null 가능)
//                        putExtra("birthday_from_kakao", formattedBirthdate) // 카카오에서 변환된 생년월일 (null 가능)
//                    }
//                    startActivity(intent)
//                    finish() // LoginActivity 종료
//                    return@me // 함수 종료
//                }
//
//                // --- 모든 필수 정보가 확보되었으므로 백엔드 요청 ---
//                val kakaoLoginRequest = KakaoLoginRequest(
//                    kakaoId = kakaoId,
//                    email = email,
//                    nickname = nickname,
//                    accessToken = accessToken,
//                    user_sex = gender,           // "male", "female"
//                    user_birthdate = formattedBirthdate // "YYYY-MM-DD"
//                )

                // RetrofitClient.instance.kakaoLogin(kakaoLoginRequest).enqueue(...)

                // 백엔드 처리 후, handleLoginSuccess(User(id, name, role)) 호출하여 다음 단계로 진행
                // 임시로 바로 성공 처리 (실제로는 백엔드 응답에 따라)
                // handleLoginSuccess(User(user.id.toString(), user.kakaoAccount?.profile?.nickname, "user"))
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // 핸들러 작업 중단
        handler.removeCallbacksAndMessages(null)
    }
}
