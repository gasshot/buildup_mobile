package com.example.buildup

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


// 비동기 통신 임포트
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinActivity : AppCompatActivity() {

    private lateinit var editTextJoinId: EditText
    private lateinit var editTextJoinPw: EditText
    private lateinit var editTextJoinPwConfirm: EditText
    private lateinit var editTextNickname: EditText
    private lateinit var editTextJoinEmail: EditText

    private lateinit var buttonJoin: Button
    private lateinit var buttonReturn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_join)

        // UI 요소 찾기
        editTextJoinId = findViewById(R.id.editTextJoinId)
        editTextJoinPw = findViewById(R.id.editTextJoinPw)
        editTextJoinPwConfirm = findViewById(R.id.editTextJoinPwConfirm)
        editTextNickname = findViewById(R.id.editTextNickname)
        editTextJoinEmail = findViewById(R.id.editTextJoinEmail)

        buttonJoin = findViewById(R.id.buttonJoin)
        buttonReturn = findViewById(R.id.buttonReturn)

        // (회원가입 버튼 클릭 리스너)

        buttonJoin.setOnClickListener {
            // 회원가입 버튼 (activity_main.xml에 추가해야 함)
            val userId = editTextJoinId.text.toString()
            val userPw = editTextJoinPw.text.toString()
            val userPwAgain = editTextJoinPwConfirm.text.toString()
            val nickname = editTextNickname.text.toString()
            val email = editTextJoinEmail.text.toString()

            // 입력 값 유효성 검사
            if (userId.isBlank() || userPw.isBlank() || nickname.isBlank() || email.isBlank()) {
                Toast.makeText(this@JoinActivity, getString(R.string.error_all_fields_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userPw != userPwAgain) {
                Toast.makeText(this@JoinActivity, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this@JoinActivity, getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            if (userPw.length < 8 || !userPw.matches(".*[!@#\$%^&*()].*".toRegex())) {
//                Toast.makeText(this@JoinActivity, getString(R.string.error_password_strength), Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            // 로딩 표시
            val progressDialog = ProgressDialog(this@JoinActivity)
            progressDialog.setMessage(getString(R.string.loading_message))
            progressDialog.setCancelable(false)
            progressDialog.show()

            val joinRequest = JoinRequest(userId, userPw, nickname, email)

            // Retrofit 통신
            RetrofitClient.instance.joinUser(joinRequest).enqueue(object : Callback<JoinResponse> {
                override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
                    progressDialog.dismiss()
                    if (response.isSuccessful) {
                        val joinResponse = response.body()
                        if (joinResponse?.success == true) {
                            // 회원가입 성공
                            Toast.makeText(this@JoinActivity, getString(R.string.success_join), Toast.LENGTH_SHORT).show()

                            // 다음 화면으로 이동(메인화면)
                            val intent = Intent(this@JoinActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
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
        }

        buttonReturn.setOnClickListener{
            // 다음 화면으로 이동(메인화면)
            val intent = Intent(this@JoinActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}