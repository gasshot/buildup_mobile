package com.example.buildup

import PagerAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
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

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(tabLayout, viewPager) { _, _ ->
            // 탭 제목 필요하면 작성
        }.attach()

// 탭 뷰 전체에 클릭 불가 처리 (터치 이벤트도 막음)
        for (i in 0 until binding.tabLayout.tabCount) {
            binding.tabLayout.getTabAt(i)?.view?.isClickable = false
            binding.tabLayout.getTabAt(i)?.view?.isFocusable = false
        }
        binding.tabLayout.isEnabled = false

        // SharedPreferences에서 사용자 정보 불러오기
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "N/A")
        val userNickname = sharedPreferences.getString("userName", "N/A")

        binding.textView6.text = "${userNickname}님"

        // 버튼 클릭 리스너
        binding.buttonMypage.setOnClickListener {
            startActivity(Intent(this@MainActivity, MypageActivity::class.java))
            finish()
        }

        binding.recommendBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, CompareActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.AnalysisBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, AnalysisActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            finish()
        }
    }
}
