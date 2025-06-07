package com.example.buildup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.buildup.activities.mypage.PasswordConfirmationActivity
import com.example.buildup.databinding.ActivityMypageBinding

class MypageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.announceBtn.setOnClickListener {
            // 공지사항 버튼 클릭 시 실행될 코드
        }

        binding.announceBtn.setOnClickListener {
            // 이벤트 버튼 클릭 시 실행될 코드
        }

        binding.myProfileBtn.setOnClickListener {
            navigateToPasswordConfirmationActivity()
        }

        binding.analysisBtn.setOnClickListener {
            Toast.makeText(this@MypageActivity, "피부분석 클릭", Toast.LENGTH_SHORT).show()
        }

        binding.donationBtn.setOnClickListener {

        }

        binding.customServiceBtn.setOnClickListener {
            // 고객센터 버튼 클릭 시 실행될 코드
        }

        binding.logoutBtn.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
            sharedPreferences.edit {
                // 모든 데이터 삭제
                clear()
            }

            val intent = Intent(this@MypageActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.buttonReturnToMain.setOnClickListener {
            val intent = Intent(this@MypageActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun navigateToPasswordConfirmationActivity() {
        val intent = Intent(this, PasswordConfirmationActivity::class.java) // TODO: 실제 회원 정보 액티비티 클래스로 변경하세요.
        startActivity(intent)
    }
}