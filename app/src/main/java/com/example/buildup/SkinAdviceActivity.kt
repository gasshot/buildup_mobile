package com.example.buildup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.api.ApiManager
import com.example.buildup.databinding.ActivitySkinAdviceBinding

class SkinAdviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySkinAdviceBinding
    private lateinit var loadingDialog: AILoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkinAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = AILoadingDialog(this)

        val requester = intent.getStringExtra("requester") ?: ""
        val predictedSkinType = intent.getStringExtra("predicted_skin_type") ?: ""
        val personalColorTone = intent.getStringExtra("personal_color_tone") ?: ""

        // 로딩창 띄우기
        loadingDialog.show()

        ApiManager.fetchSkinAdvice(requester, predictedSkinType, personalColorTone) { success, advice ->
            runOnUiThread {
                // 로딩창 닫기
                if (loadingDialog.isShowing) {
                    loadingDialog.dismiss()
                }

                if (success) {
                    binding.adviceTextView.text = advice
                } else {
                    Toast.makeText(this@SkinAdviceActivity, "조언을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this@SkinAdviceActivity, MainActivity::class.java))
            finish()
        }
    }
}

