package com.example.buildup

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.databinding.ActivityMypageBinding

class MypageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)


        binding.announceBtn.setOnClickListener {
            Toast.makeText(this@MypageActivity, "공지사항 클릭", Toast.LENGTH_SHORT).show()
        }
        binding.myProfileBtn.setOnClickListener {
            Toast.makeText(this@MypageActivity, "내정보 클릭", Toast.LENGTH_SHORT).show()
        }
        binding.analysisBtn.setOnClickListener {
            Toast.makeText(this@MypageActivity, "피부분석 클릭", Toast.LENGTH_SHORT).show()
        }
        binding.csBtn.setOnClickListener {
            Toast.makeText(this@MypageActivity, "고객센터 클릭", Toast.LENGTH_SHORT).show()
        }

    }
}