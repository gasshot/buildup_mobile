package com.example.buildup.activities.join

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.example.buildup.data.JoinRequest
import com.example.buildup.data.JoinResponse
import com.example.buildup.LoginActivity
import com.example.buildup.R
import com.example.buildup.api.RetrofitClient
import com.example.buildup.databinding.ActivityJoinBinding


// 비동기 통신 임포트
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinActivity : AppCompatActivity() {

    private lateinit var binding : ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_join)

        // UI 요소 찾기

        // 뷰 바인딩 객체 생성 및 setContentView에 적용
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전 액티비티에서 받은 데이터
        val id = intent.getStringExtra("user_id")
        val pw = intent.getStringExtra("user_pw")
        val email = intent.getStringExtra("user_email")
        val nickname = intent.getStringExtra("user_nickname")
        val sex = intent.getStringExtra("user_sex")
        val birthdate = intent.getStringExtra("user_birthdate")

        // 회원가입 버튼 (activity_main.xml에 추가해야 함)
        val userId = id.toString()
        val userPw = pw.toString()
        val userNickname = nickname.toString()
        val userEmail = email.toString()
        val userSex = sex.toString()
        val userBirthdate = birthdate.toString()

//            // 입력 값 유효성 검사
//            if (userId.isBlank() || userPw.isBlank() || nickname.isBlank() || userEmail.isBlank()) {
//                Toast.makeText(this@JoinActivity, getString(R.string.error_all_fields_required), Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }


        // 로딩 표시
        val progressDialog = ProgressDialog(this@JoinActivity)
        progressDialog.setMessage(getString(R.string.loading_message))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val joinRequest = JoinRequest(userId, userPw, userNickname, userEmail, userSex, userBirthdate)

        // Retrofit 통신
        RetrofitClient.instance.joinUser(joinRequest).enqueue(object : Callback<JoinResponse> {
            override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    val joinResponse = response.body()
                    if (joinResponse?.success == true) {
                        // 회원가입 성공
                        //Toast.makeText(this@JoinActivity, getString(R.string.success_join), Toast.LENGTH_SHORT).show()

//                        // 다음 화면으로 이동(메인화면)
//                        val intent = Intent(this@JoinActivity, LoginActivity::class.java)
//                        startActivity(intent)
//                        finish()
                    } else {
                        // 서버 응답 실패
                        Toast.makeText(this@JoinActivity, getString(R.string.error_join_failed), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 서버 에러 메시지 표시
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = getString(R.string.error_join_request_failed, response.code(), errorBody ?: "Unknown error")
                    Toast.makeText(this@JoinActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
                progressDialog.dismiss()
                // 네트워크 오류
                Log.e("JoinActivity", "Network error: ${t.message}")
                Toast.makeText(this@JoinActivity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
            }
        })

        binding.buttonNextToLogin.setOnClickListener{
            // 다음 화면으로 이동(메인화면)
            val intent = Intent(this@JoinActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}