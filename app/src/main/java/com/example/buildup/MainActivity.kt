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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val adapter = PagerAdapter(this)
        viewPager.adapter = adapter
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        // ViewPager2와 TabLayout 연결
        TabLayoutMediator(tabLayout, viewPager) { _, _ ->
            // 탭의 제목 설정 필요 없으면 비워둠
        }.attach()

        // SharedPreferences에서 사용자 정보 불러오기
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "N/A")
        val userNickname = sharedPreferences.getString("userName", "N/A")

        binding.textView6.text = "${userNickname}님"

        // 마이페이지 버튼 동작
        binding.buttonMypage.setOnClickListener {
            val intent = Intent(this@MainActivity, MypageActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.recommendBtn.setOnClickListener {
            // 다음 화면으로 이동(과거 기록 비교 분석 화면)
        }

        // 분석 버튼 클릭 시 이미지 선택 Intent 호출
        binding.AnalysisBtn.setOnClickListener {

            // 다음 화면으로 이동(분석화면)
            val intent = Intent(this@MainActivity, AnalysisActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            finish()
        }

    }

}
