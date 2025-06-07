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

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding 초기화
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기준값 정의 (가정값)
        val referenceRange = mapOf(
            "수분" to (0f to 100f),
            "탄력" to (0f to 1f),
            "주름" to (0f to 50f),
            "스팟" to (0f to 300f),
            "모공" to (0f to 600f)
        )

        // 실제 점수 예시
        val actualScores = mapOf(
            "수분" to 75f,
            "탄력" to 0.8f,
            "주름" to 25f,
            "스팟" to 150f,
            "모공" to 200f
        )

        // 점수 정규화 함수
        fun normalizeScore(value: Float, range: Pair<Float, Float>): Float {
            val (min, max) = range
            return if (max - min == 0f) 0f else (value - min) / (max - min)
        }

        // 정규화된 점수를 0~100 정수 단위로 변환
        val entries = actualScores.map { (key, value) ->
            val range = referenceRange[key] ?: (0f to 1f)
            val normalized = normalizeScore(value, range)
            RadarEntry((normalized * 100).toInt().toFloat()) // 정수 단위로 변환 후 다시 float로
        }

        // 축 이름 설정
        val labels = listOf("수분", "탄력", "주름", "스팟", "모공")
        binding.radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // DataSet 생성
        val dataSet = RadarDataSet(entries, "피부 분석 결과").apply {
            color = resources.getColor(R.color.teal_700, null)
            fillColor = resources.getColor(R.color.teal_700, null)
            setDrawFilled(true) // 영역 채우기
            setDrawHighlightCircleEnabled(false) // 강조 원 비활성화
            setDrawValues(false) // 각 점수 값 표시 비활성화
            lineWidth = 0f // 연결선 두께를 0으로 설정하여 선 제거
        }

        // RadarData 설정 및 차트 업데이트
        binding.radarChart.apply {
            isHighlightPerTapEnabled = false // 하이라이트 비활성화
            data = RadarData(dataSet)
            description.isEnabled = false
            animateXY(1400, 1400)

            invalidate()
        }

        binding.radarChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            textSize = 14f  // 원하는 폰트 크기 (sp 단위로 해석됨)
            textColor = ContextCompat.getColor(this@ResultActivity, R.color.yAxisLabelColor)
        }

        binding.radarChart.yAxis.apply {
            axisMaximum = 100f  // 최대값을 100으로 고정
            axisMinimum = 0f    // 최소값은 0
            granularity = 20f   // 눈금 간격 20으로 설정
            setLabelCount(6, true)  // 0,20,40,60,80,100 이렇게 6개 눈금 강제 설정
            textSize = 12f          // y축 라벨 크기
            textColor = ContextCompat.getColor(this@ResultActivity, R.color.yAxisLabelColor)
            setDrawLabels(false)  // y축 라벨 숨기기
        }

        // 메인화면으로 돌아가는 버튼 클릭 이벤트
        binding.btnToMain.setOnClickListener {
            val intent = Intent(this@ResultActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 텍스트 처리

        val htmlText = "당신의 피부 타입은 <b>🌻복합지성🌻</b> 입니다."
        binding.textViewSkinTypeTitle.text = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)









    }
}
