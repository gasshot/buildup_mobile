package com.example.buildup


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

// 비동기 통신 임포트
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {


    //Android 에뮬레이터 또는 실제 기기에서 127.0.0.1:8000으로 접근하려고 하면, 해당 에뮬레이터 또는 실제 기기 자신의 8000번 포트에 연결을 시도하게 됩니다. 따라서 개발 PC에서 실행 중인 FastAPI 서버에는 연결되지 않습니다.

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
        R.drawable.edit_jwy4, // 교체할 이미지 목록
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

        buttonStart.setOnClickListener {

            // 다음 화면으로 이동(새 액티비트 시작)
            val intent = Intent(this@LoginActivity, JoinActivity::class.java)
            startActivity(intent)
            finish()
        }


        // 버튼 이벤트들


        buttonEditorLogin.setOnClickListener {
            // 다음 화면으로 이동(새 액티비트 시작)
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        buttonLogin.setOnClickListener { // 로그인 버튼
            val userId = editTextUserId.text.toString()
            val userPw = editTextPassword.text.toString()

            val loginRequest = LoginRequest(userId, userPw)

            RetrofitClient.instance.loginUser(loginRequest)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse?.success == true) {
                                val user = loginResponse.user
                                //welcomeText.text = "${user?.name}님, ${user?.role}으로 로그인 성공!"
                                //Toast.makeText(this@MainActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                                // SharedPreferences에 사용자 정보 저장
                                val sharedPreferences =
                                    getSharedPreferences("user_info", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("userId", user?.id)
                                editor.putString("userName", user?.name)
                                editor.putString("userRole", user?.role)
                                editor.apply() // 비동기 저장 (editor.commit()은 동기 저장)

                                // 다음 화면으로 이동(새 액티비트 시작)
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                //welcomeText.text = "로그인 실패: ${loginResponse?.message}"
                                Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val errorMessage =
                                "로그인 요청 실패: ${response.code()} - ${errorBody ?: "알 수 없는 오류"}"
                            //welcomeText.text = errorMessage
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        //welcomeText.text = "네트워크 오류: ${t.message}"
                        Toast.makeText(this@LoginActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                })
        }
        
        
        // 소셜 로그인 버튼 이벤트 설정
        buttonLoginGoogle.setOnClickListener {
            // 구글 로그인 로직
        }
        buttonLoginKakao.setOnClickListener {
            // 카카오 로그인 로직
        }

    }

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

    //ㅋㅋ
    override fun onDestroy() {
        super.onDestroy()
        // 핸들러 작업 중단
        handler.removeCallbacksAndMessages(null)
    }
}

//설명:
//
//import retrofit2.Retrofit: Retrofit 클래스를 가져옵니다.
//import retrofit2.converter.gson.GsonConverterFactory: Gson을 사용하여 서버 응답을 Java/Kotlin 객체로 변환하는 Converter Factory를 가져옵니다.
//companion object: 클래스의 인스턴스 생성 없이 접근할 수 있는 객체를 정의합니다. apiService 인스턴스를 앱 실행 중에 한 번만 생성하도록 lazy 속성을 사용했습니다.
//private const val BASE_URL = "YOUR_FASTAPI_SERVER_URL": FastAPI 서버의 기본 URL을 저장하는 상수입니다. 반드시 실제 서버 주소로 변경해야 합니다. (예: http://192.168.0.100:8000 또는 로컬 개발 시 에뮬레이터의 경우 http://10.0.2.2:8000).
//val apiService: ApiService by lazy { ... }: ApiService 타입의 apiService 프로퍼티를 정의하고, lazy를 사용하여 처음 접근될 때 Retrofit 인스턴스를 생성합니다.
//Retrofit.Builder(): Retrofit 인스턴스를 생성하기 위한 빌더를 생성합니다.
//.baseUrl(BASE_URL): 서버의 기본 URL을 설정합니다. 모든 API 요청은 이 URL을 기준으로 상대 경로로 만들어집니다.
//.addConverterFactory(GsonConverterFactory.create()): 응답 데이터를 Gson을 사용하여 파싱하도록 Gson Converter Factory를 추가합니다. 서버가 JSON 형태의 데이터를 반환할 때 필요합니다.
//.build(): 설정된 옵션으로 Retrofit 인스턴스를 생성합니다.
//.create(ApiService::class.java): 생성된 Retrofit 인스턴스를 사용하여 ApiService 인터페이스의 구현체를 만듭니다. 이제 apiService 객체를 통해 ApiService에 정의된 함수들을 호출하여 서버와 통신할 수 있습니다.