package com.example.buildup.activities.mypage

import ConfirmDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.LoginActivity
import com.example.buildup.MypageActivity
import com.example.buildup.databinding.ActivityMyProfileBinding
import androidx.core.content.edit

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences에서 사용자 정보 불러오기
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "N/A")
        val userDob = sharedPreferences.getString("userBirthdate", "N/A")
        val userGender = sharedPreferences.getString("userSex", "N/A")
        val userEmail = sharedPreferences.getString("userEmail", "N/A")

        // 데이터를 TextView에 설정
        binding.tvUserId.text = userId
        binding.tvUserDob.text = userDob
        binding.tvUserGender.text = userGender
        binding.tvUserEmail.text = userEmail

        binding.btnBack.setOnClickListener{
            navigateToMyPageActivity()
        }

        binding.btnChangePassword.setOnClickListener{
            // 비밀번호 변경 시 다이얼로그 뜨게하기
        }


        binding.btnUnjoin.setOnClickListener{
            quit()
        }
    }



    private fun quit() {
        // 다이얼로그 정보 설정
        val title = "정말 계정을 삭제하시겠어요?"
        val content = "지금까지의 정보가 모두 사라집니다."

        // 다이얼로그 생성
        val dialog = ConfirmDialog.newInstance(
            title,
            content,
            "확인",
            1
        ) { id ->
            // 확인 버튼 클릭 시 처리
            if (id == 1) {
                handleAccountDeletion()
            }
        }

        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false

        // 다이얼로그 표시
        supportFragmentManager.let {
            dialog.show(it, "ConfirmDialog")
        }
    }

    private fun handleAccountDeletion() {
        // 계정 삭제 처리 로직(예정)
        // 예: API 호출, 로컬 데이터 삭제 등
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            // 모든 데이터 삭제
            clear()
        }

        val intent = Intent(this@MyProfileActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMyPageActivity() {
        val intent = Intent(this, MypageActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료 -> 마이페이지로 이동
    }
}

