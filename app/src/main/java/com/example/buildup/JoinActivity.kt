package com.example.buildup

import android.content.Intent
import android.os.Bundle
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
        editTextNickname = findViewById(R.id.editTextNickname)
        editTextJoinEmail = findViewById(R.id.editTextJoinEmail)

        buttonJoin = findViewById(R.id.buttonJoin)
        buttonReturn = findViewById(R.id.buttonReturn)

        // (회원가입 버튼 클릭 리스너)

        buttonJoin.setOnClickListener { // 회원가입 버튼 (activity_main.xml에 추가해야 함)
            val userId = editTextJoinId.text.toString() // activity_main.xml에 추가해야 함
            val userPw = editTextJoinPw.text.toString() // activity_main.xml에 추가해야 함
            val nickname = editTextNickname.text.toString() // activity_main.xml에 추가해야 함
            val email = editTextJoinEmail.text.toString() // activity_main.xml에 추가해야 함

            val joinRequest = JoinRequest(userId, userPw, nickname, email)

            RetrofitClient.instance.joinUser(joinRequest).enqueue(object : Callback<JoinResponse> {
                override fun onResponse(call: Call<JoinResponse>, response: Response<JoinResponse>) {
                    if (response.isSuccessful) {
                        val joinResponse = response.body()
                        if (joinResponse?.success == true) {

                            // 다음 화면으로 이동(메인화면)
                            val intent = Intent(this@JoinActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@JoinActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = "회원가입 요청 실패: ${response.code()} - ${errorBody ?: "알 수 없는 오류"}"
                        Toast.makeText(this@JoinActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<JoinResponse>, t: Throwable) {
                    Toast.makeText(this@JoinActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
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