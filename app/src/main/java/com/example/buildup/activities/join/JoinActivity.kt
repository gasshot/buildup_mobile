package com.example.buildup.activities.join

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.buildup.data.JoinRequest
import com.example.buildup.LoginActivity
import com.example.buildup.R
import com.example.buildup.api.ApiManager
import com.example.buildup.databinding.ActivityJoinBinding

class JoinActivity : AppCompatActivity() {

    // 뷰 바인딩을 위한 변수 선언
    private lateinit var binding : ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 객체 생성 (레이아웃 inflation)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        // 바인딩된 root 뷰를 화면에 표시
        setContentView(binding.root)

        // 이전 액티비티(회원가입 폼 등)에서 전달받은 사용자 입력 데이터
        val id = intent.getStringExtra("user_id")
        val pw = intent.getStringExtra("user_pw")
        val email = intent.getStringExtra("user_email")
        val nickname = intent.getStringExtra("user_nickname")
        val sex = intent.getStringExtra("user_sex")
        val birthdate = intent.getStringExtra("user_birthdate")

        // null 안정성을 위해 toString() 사용해 기본값 처리 (null이면 "null" 문자열 됨)
        // 필요하다면 기본값을 따로 지정하는 것도 좋음
        val userId = id.toString()
        val userPw = pw.toString()
        val userNickname = nickname.toString()
        val userEmail = email.toString()
        val userSex = sex.toString()
        val userBirthdate = birthdate.toString()

        // ProgressDialog 생성: 서버와 통신 중임을 사용자에게 알리기 위한 로딩 UI
        val progressDialog = ProgressDialog(this@JoinActivity)
        progressDialog.setMessage(getString(R.string.loading_message))  // "로딩중..." 메시지
        progressDialog.setCancelable(false)  // 사용자가 취소하지 못하도록 설정
        progressDialog.show()  // 다이얼로그 표시

        // API 호출에 사용할 요청 데이터 객체 생성
        val joinRequest = JoinRequest(userId, userPw, userNickname, userEmail, userSex, userBirthdate)

        // ApiManager의 joinUser 메서드 호출, 콜백을 통해 결과 처리
        ApiManager.joinUser(joinRequest) { success, message ->
            // 통신이 끝났으니 로딩 UI는 닫음
            progressDialog.dismiss()

            if (success) {
                // 회원가입 성공 시 사용자에게 안내 토스트 출력
                Toast.makeText(this@JoinActivity, getString(R.string.success_join), Toast.LENGTH_SHORT).show()
                // 필요 시 여기서 다음 동작(예: 로그인 화면 이동 등) 추가 가능
            } else {
                // 실패 시 서버에서 전달된 메시지 또는 기본 에러 메시지 표시
                Toast.makeText(this@JoinActivity, message ?: getString(R.string.error_join_failed), Toast.LENGTH_SHORT).show()
            }
        }

        // '다음' 버튼 클릭 시 로그인 화면으로 이동하는 리스너 등록
        binding.buttonNextToLogin.setOnClickListener {
            val intent = Intent(this@JoinActivity, LoginActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }
}
