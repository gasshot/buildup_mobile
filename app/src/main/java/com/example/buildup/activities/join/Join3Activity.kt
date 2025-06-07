package com.example.buildup.activities.join

import android.content.Intent
import android.graphics.Color // Color 클래스 추가
import android.os.Bundle
import android.text.Editable // TextWatcher 사용을 위해 추가
import android.text.TextWatcher // TextWatcher 사용을 위해 추가
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.LoginActivity
import com.example.buildup.R
import com.example.buildup.databinding.ActivityJoin3Binding

class Join3Activity : AppCompatActivity() {
    private lateinit var binding: ActivityJoin3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_join2) // 이 줄은 제거해야 합니다.

        // 뷰 바인딩 객체 생성 및 setContentView에 적용
        binding = ActivityJoin3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        // 이전 액티비티에서 받은 데이터
        val id = intent.getStringExtra("user_id")
        val pw1 = intent.getStringExtra("user_pw")
        val email = intent.getStringExtra("user_email")

        // 로그인 화면으로 돌아가기 리스너
        binding.buttonReturnToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료

        }

        // 닉네임 입력 필드 변경 리스너 (TextWatcher 추가)
        binding.editTextNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 시 UI를 기본 상태로 되돌림
                binding.editTextNickname.setBackgroundResource(R.drawable.edittext_box) // 기본 테두리
                binding.editTextNickname.setTextColor(Color.BLACK) // 기본 텍스트 색상
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.buttonNextTo4.setOnClickListener {
            // "다음" 버튼 클릭 시 최신 닉네임 값 가져오기
            val nickname = binding.editTextNickname.text.toString().trim() // 앞뒤 공백 제거

            // 1. 닉네임 입력 여부 확인
            if (nickname.isEmpty()) {
                Toast.makeText(
                    this@Join3Activity,
                    getString(R.string.error_nickname_empty), // 새로운 문자열 리소스 추가 필요
                    Toast.LENGTH_SHORT
                ).show()
                binding.editTextNickname.setBackgroundResource(R.drawable.edittext_error_box)
                binding.editTextNickname.setTextColor(Color.RED)
                return@setOnClickListener
            }

            // 2. 닉네임 길이 및 한글 유효성 검사 (한글 6글자 이내)
            // 정규식 설명: ^[가-힣]*$ -> 문자열 시작(^)부터 끝($)까지 한글(가-힣)만 허용
            // nickname.length > 6 -> 6글자 초과 여부 확인
            if (nickname.length > 6 || !nickname.matches("^[가-힣]*$".toRegex())) {
                Toast.makeText(
                    this@Join3Activity,
                    getString(R.string.error_nickname_invalid), // 새로운 문자열 리소스 추가 필요
                    Toast.LENGTH_SHORT
                ).show()
                binding.editTextNickname.setBackgroundResource(R.drawable.edittext_error_box)
                binding.editTextNickname.setTextColor(Color.RED)
                return@setOnClickListener
            } else {
                // 유효성 검사 통과 시 UI 초기화 (이전에 오류가 있었을 경우)
                binding.editTextNickname.setBackgroundResource(R.drawable.edittext_box) // 기본 테두리
                binding.editTextNickname.setTextColor(Color.BLACK) // 기본 텍스트 색상
            }


            // 모든 유효성 검사 통과 시 다음 액티비티로 이동
            val intent = Intent(this, Join4Activity::class.java)
            intent.putExtra("user_id", id)
            intent.putExtra("user_pw", pw1)
            intent.putExtra("user_email", email)
            intent.putExtra("user_nickname", nickname) // 최신 닉네임 값 전달
            startActivity(intent)
        }
    }
}