package com.example.buildup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.databinding.ActivityRecommandCosmicBinding

class RecommendCosmicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommandCosmicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommandCosmicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent 데이터 처리
        handleIntent(intent)

        binding.backButton.setOnClickListener {
            onBackPressed() // 뒤로가기 처리
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        // Intent 데이터 수신
        val personalColorTone = intent.getStringExtra("personal_color_tone") ?: "정보 없음"

        // 이미지 리소스 설정
        val colorImageResId = when (personalColorTone) {
            "봄웜톤(spring)" -> R.drawable.amuse_product_spring_warm
            "봄쿨톤(spring cool)" -> R.drawable.amuse_product_spring_cool
            "여름웜톤(summer warm)" -> R.drawable.amuse_product_summer_warm
            "여름쿨톤(summer)" -> R.drawable.amuse_product_summer_cool
            "가을웜톤(fall)" -> R.drawable.amuse_product_autumn_warm
            "가을쿨톤(fall cool)" -> R.drawable.amuse_product_autumn_cool
            "겨울웜톤(winter warm)" -> R.drawable.amuse_product_winter_warm
            "겨울쿨톤(winter)" -> R.drawable.amuse_product_winter_cool
            else -> null
        }

        // 이미지뷰에 설정
        if (colorImageResId != null) {
            binding.recommendImage.setImageResource(colorImageResId)
        } else {
            // 기본 이미지나 에러 처리를 할 수 있음
            binding.recommendImage.setImageResource(R.drawable.skinthera2) // 기본 이미지 리소스
        }
    }
}
