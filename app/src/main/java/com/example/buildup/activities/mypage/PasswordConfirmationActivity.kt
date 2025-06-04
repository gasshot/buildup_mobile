package com.example.buildup.activities.mypage

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.MypageActivity
import com.example.buildup.R
import com.example.buildup.api.ApiManager
import com.example.buildup.data.CheckPWRequest
import com.example.buildup.databinding.ActivityPasswordConfirmationBinding


class PasswordConfirmationActivity : AppCompatActivity() {

    private var currentUserId: String? = null
    private lateinit var binding: ActivityPasswordConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences에서 사용자 ID 가져오기
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getString("userId", null)

        if (currentUserId == null) {
            Toast.makeText(this, "사용자 정보를 찾을 수 없습니다. 로그인이 필요합니다.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.buttonConfirm.setOnClickListener {
            val enteredPassword = binding.editTextPassword.text.toString()

            if (enteredPassword.isEmpty()) {
                // 유효성 검사 실패 시 UI 오류 표시
                binding.editTextPassword.setBackgroundResource(R.drawable.edittext_error_box) // 오류 테두리
                binding.editTextPassword.setTextColor(Color.RED) // 텍스트 색상 변경
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener // 유효성 검사 실패 시 다음 코드 실행 방지
            }

            val checkRequest = CheckPWRequest(currentUserId!!, enteredPassword)
            performCheckPW(checkRequest)
        }

        binding.buttonCancel.setOnClickListener {
            finish() // 현재 액티비티를 닫고 이전 화면으로 돌아가기
        }

        // 이메일 입력 필드 변경 리스너 (TextWatcher 추가)
        binding.editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 시 UI를 기본 상태로 되돌림
                binding.editTextPassword.setBackgroundResource(R.drawable.edittext_box) // 기본 테두리
                binding.editTextPassword.setTextColor(Color.BLACK) // 기본 텍스트 색상
            }
            override fun afterTextChanged(s: Editable?) {}
        })


    }

    private fun performCheckPW(checkRequest: CheckPWRequest) {
        ApiManager.checkPW(checkRequest) { success ->
            if (success) {
                // 비밀번호 확인 성공
                navigateToMyProfileActivity()
            } else {
                // 실패 처리 UI
                binding.editTextPassword.setBackgroundResource(R.drawable.edittext_error_box)
                binding.editTextPassword.setTextColor(Color.RED)
                Toast.makeText(this@PasswordConfirmationActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun navigateToMyProfileActivity() {
        val intent = Intent(this, MyProfileActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료 -> 내 정보로 이동
    }

    private fun navigateToMyPageActivity() {
        val intent = Intent(this, MypageActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료 -> 마이페이지로 이동
    }
}
