package com.example.buildup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.api.ApiManager
import com.example.buildup.databinding.ActivitySkinAdviceBinding
import io.noties.markwon.Markwon

class SkinAdviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySkinAdviceBinding
    private lateinit var loadingDialog: AILoadingDialog

    // 이전 데이터 저장용 변수들
    private var lastRequester: String? = null
    private var lastPredictedSkinType: String? = null
    private var lastPersonalColorTone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkinAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = AILoadingDialog(this)

        handleIntent(intent)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val requester = intent.getStringExtra("requester") ?: ""
        val predictedSkinType = intent.getStringExtra("predicted_skin_type") ?: ""
        val personalColorTone = intent.getStringExtra("personal_color_tone") ?: ""

        // 이전에 처리한 데이터와 동일하면 다시 호출하지 않음
        if (requester == lastRequester &&
            predictedSkinType == lastPredictedSkinType &&
            personalColorTone == lastPersonalColorTone) {
            // 데이터가 같으므로 아무 작업 안 함
            return
        }

        // 데이터가 변경되었으므로 변수 업데이트
        lastRequester = requester
        lastPredictedSkinType = predictedSkinType
        lastPersonalColorTone = personalColorTone

        loadingDialog.show()

        ApiManager.fetchSkinAdvice(requester, predictedSkinType, personalColorTone) { success, advice ->
            runOnUiThread {
                if (loadingDialog.isShowing) {
                    loadingDialog.dismiss()
                }

                if (success) {
                    val markwon = Markwon.create(this@SkinAdviceActivity)
                    markwon.setMarkdown(binding.adviceTextView, advice ?: "")
                } else {
                    Toast.makeText(this@SkinAdviceActivity, "조언을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}



