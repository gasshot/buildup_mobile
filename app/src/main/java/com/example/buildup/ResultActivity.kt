package com.example.buildup

import android.content.Intent
import androidx.core.text.HtmlCompat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.buildup.databinding.ActivityResultBinding
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    // 기준값 정의 (가정값)
    private val referenceRange = mapOf(
        "수분" to (0f to 100f),
        "탄력" to (0f to 1f),
        "주름" to (0f to 50f),
        "스팟" to (0f to 300f),
        "모공" to (0f to 600f)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent 데이터 수신
        val personalColorTone = intent.getStringExtra("personal_color_tone") ?: "정보 없음"
        val skinAnalysisJson = intent.getStringExtra("skin_analysis_json")
        val requester = intent.getStringExtra("requester")

        // JSON → Map 변환
        val skinAnalysisMap: Map<String, Any>? = skinAnalysisJson?.let {
            try {
                Gson().fromJson(it, object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type)
            } catch (e: Exception) {
                null
            }
        }

        // 평균 측정값 Map
        val averageMeasurements = skinAnalysisMap?.get("average_measurements") as? Map<String, Any>

        // 예측된 피부 타입
        val predictedSkinType = skinAnalysisMap?.get("predicted_skin_type")?.toString() ?: "정보 없음"

        // 평균 계산
        val averages = calculateAverages(averageMeasurements ?: emptyMap())

        // 정규화 및 RadarEntry 생성
        val entries = averages.map { (key, value) ->
            val range = referenceRange[key] ?: (0f to 1f)
            val normalized = normalizeScore(value, range)
            RadarEntry(normalized * 100f) // 0~100 스케일
        }

        // 레이더 차트 축 라벨
        val labels = listOf("수분", "탄력", "주름", "스팟", "모공")
        binding.radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // DataSet 세팅
        val dataSet = RadarDataSet(entries, "피부 분석 결과").apply {
            color = ContextCompat.getColor(this@ResultActivity, R.color.teal_700)
            fillColor = ContextCompat.getColor(this@ResultActivity, R.color.teal_700)
            setDrawFilled(true)
            setDrawHighlightCircleEnabled(false)
            setDrawValues(false)
            lineWidth = 0f
        }

        binding.radarChart.apply {
            isHighlightPerTapEnabled = false
            data = RadarData(dataSet)
            description.isEnabled = false
            animateXY(1400, 1400)
            invalidate()

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                textSize = 14f
                textColor = ContextCompat.getColor(this@ResultActivity, R.color.yAxisLabelColor)
            }

            yAxis.apply {
                axisMaximum = 100f
                axisMinimum = 0f
                granularity = 20f
                setLabelCount(6, true)
                textSize = 12f
                textColor = ContextCompat.getColor(this@ResultActivity, R.color.yAxisLabelColor)
                setDrawLabels(false)
            }
        }
        binding.buttonCosmic.setOnClickListener {
            val intent = Intent(this@ResultActivity, RecommendCosmicActivity::class.java).apply {
                putExtra("personal_color_tone", personalColorTone)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP // 이미 존재하면 재활성화
            }
            startActivity(intent)
        }

        binding.buttonAI.setOnClickListener {
            val intent = Intent(this@ResultActivity, SkinAdviceActivity::class.java).apply {
                putExtra("personal_color_tone", personalColorTone)
                putExtra("predicted_skin_type", predictedSkinType)
                putExtra("requester", requester)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP // 이미 존재하면 재활성화
            }
            startActivity(intent)
        }

        // 메인화면 이동 버튼
        binding.btnToMain.setOnClickListener {
            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            finish()
        }

        // 피부 타입 텍스트 (HTML 포함)
        val htmlText = "당신의 피부 타입은 <b>${predictedSkinType}</b> 입니다."
        binding.textViewTon.text = personalColorTone
        binding.textViewRecommendContent.text ="추천 버튼을 눌러서 제품을 추천받아보세요."
        binding.textViewSkinTypeContent.text = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun normalizeScore(value: Float, range: Pair<Float, Float>): Float {
        val (min, max) = range
        return if (max - min == 0f) 0f else (value - min) / (max - min)
    }

    private fun calculateAverages(data: Map<String, Any?>): Map<String, Float> {
        fun getFloat(key: String) = (data[key] as? Number)?.toFloat() ?: 0f

        val moisture = (getFloat("수분_이마") + getFloat("수분_오른쪽볼") + getFloat("수분_왼쪽볼") + getFloat("수분_턱")) / 4
        val elasticity = (getFloat("탄력_턱_R2") + getFloat("탄력_왼쪽볼_R2") + getFloat("탄력_오른쪽볼_R2") + getFloat("탄력_이마_R2")) / 4
        val pores = (getFloat("모공개수_오른쪽볼") + getFloat("모공개수_왼쪽볼")) / 2
        val spots = getFloat("스팟개수_정면")
        val wrinkles = (getFloat("주름_왼쪽눈가_Ra") + getFloat("주름_오른쪽눈가_Ra")) / 2

        return mapOf(
            "수분" to moisture,
            "탄력" to elasticity,
            "모공" to pores,
            "스팟" to spots,
            "주름" to wrinkles
        )
    }
}
