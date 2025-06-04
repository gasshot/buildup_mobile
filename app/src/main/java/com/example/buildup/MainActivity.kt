package com.example.buildup

import PagerAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val adapter = PagerAdapter(this)
        viewPager.adapter = adapter

        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString("userId", null)
        val userName = sharedPreferences.getString("userName", null)
//        val userEmail = sharedPreferences.getString("userEmail", null)
//        val userSex = sharedPreferences.getString("userSex", null)
//        val userBirthdate = sharedPreferences.getString("userBirthdate", null)
//        val userJoinDate = sharedPreferences.getString("userJoinDate", null)
//        val userRole = sharedPreferences.getString("userRole", null)


        // 로그아웃 버튼 동작
        binding.buttonMypage.setOnClickListener {
            val intent = Intent(this@MainActivity, MypageActivity::class.java)
            startActivity(intent)
            finish()
        }

//        // 분석 버튼 클릭 시 이미지 선택 Intent 호출
//        binding.analysisBtn.setOnClickListener {
//            Log.d("일단 테스트", "이미지 저장")
//
//            // 다음 화면으로 이동(분석화면)
//            val intent = Intent(this@MainActivity, AnalysisActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }

}
