package com.example.buildup

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.api.ApiManager
import com.example.buildup.data.AnalysisData
import com.example.buildup.databinding.ActivityCompareBinding

class CompareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompareBinding

    private val analysisHistory = mutableListOf<AnalysisData>()
    private var selectedYearPast: String? = null
    private var selectedMonthPast: String? = null
    private var selectedDayPast: String? = null
    private var selectedYearCurrent: String? = null
    private var selectedMonthCurrent: String? = null
    private var selectedDayCurrent: String? = null

    private var selectedPastDataId: String? = null
    private var selectedCurrentDataId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            showLoading(true)
            ApiManager.fetchAnalysisData(userId) { success, response ->
                runOnUiThread {
                    showLoading(false)
                    if (success && response != null) {
                        populateAnalysisHistory(response.data)
                    } else {
                        val errorMessage = response?.message ?: "데이터를 불러오는 데 실패했습니다."
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "사용자 ID가 없습니다. 로그인 상태를 확인하세요.", Toast.LENGTH_SHORT).show()
        }

        setupDateSelectors()
    }

    private fun setupDateSelectors() {
        // 왼쪽 버튼 클릭 설정
        binding.buttonYearLeft.setOnClickListener { showSelectionMenu(it, "year", isLeft = true) }
        binding.buttonMonthLeft.setOnClickListener { showSelectionMenu(it, "month", isLeft = true) }
        binding.buttonDayLeft.setOnClickListener { showSelectionMenu(it, "day", isLeft = true) }

        // 오른쪽 버튼 클릭 설정
        binding.buttonYearRight.setOnClickListener { showSelectionMenu(it, "year", isLeft = false) }
        binding.buttonMonthRight.setOnClickListener { showSelectionMenu(it, "month", isLeft = false) }
        binding.buttonDayRight.setOnClickListener { showSelectionMenu(it, "day", isLeft = false) }
    }

    private fun showSelectionMenu(view: View, type: String, isLeft: Boolean) {
        val options = when (type) {
            "year" -> analysisHistory.map { it.date.split("-")[0] }.distinct().sorted()
            "month" -> (1..12).map { it.toString().padStart(2, '0') }
            "day" -> (1..31).map { it.toString().padStart(2, '0') }
            else -> emptyList()
        }

        val popupMenu = androidx.appcompat.widget.PopupMenu(this, view)
        options.forEachIndexed { index, value ->
            popupMenu.menu.add(0, index, index, value)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val selectedValue = options[menuItem.itemId]

            when {
                isLeft && type == "year" -> {
                    selectedYearPast = selectedValue
                    binding.buttonYearLeft.text = "${selectedValue}년"
                }
                isLeft && type == "month" -> {
                    selectedMonthPast = selectedValue
                    binding.buttonMonthLeft.text = "${selectedValue}월"
                }
                isLeft && type == "day" -> {
                    selectedDayPast = selectedValue
                    binding.buttonDayLeft.text = "${selectedValue}일"
                }
                !isLeft && type == "year" -> {
                    selectedYearCurrent = selectedValue
                    binding.buttonYearRight.text = "${selectedValue}년"
                }
                !isLeft && type == "month" -> {
                    selectedMonthCurrent = selectedValue
                    binding.buttonMonthRight.text = "${selectedValue}월"
                }
                !isLeft && type == "day" -> {
                    selectedDayCurrent = selectedValue
                    binding.buttonDayRight.text = "${selectedValue}일"
                }
            }

            updateListViewIfReady()
            true
        }

        popupMenu.show()
    }

    private fun updateListViewIfReady() {
        // 왼쪽 리스트뷰 업데이트
        if (selectedYearPast != null && selectedMonthPast != null && selectedDayPast != null) {
            val selectedDatePast = "$selectedYearPast-$selectedMonthPast-$selectedDayPast"
            val filteredListPast = analysisHistory.filter { it.date == selectedDatePast }
            val adapterPast = ArrayAdapter(this, android.R.layout.simple_list_item_1, filteredListPast.map { it.name })
            binding.listViewLeft.adapter = adapterPast

            binding.listViewLeft.setOnItemClickListener { _, _, position, _ ->
                val selectedData = filteredListPast[position]
                selectedPastDataId = selectedData.id
                Toast.makeText(this, "${selectedData.name} 선택됨!", Toast.LENGTH_SHORT).show()
            }
        }

        // 오른쪽 리스트뷰 업데이트
        if (selectedYearCurrent != null && selectedMonthCurrent != null && selectedDayCurrent != null) {
            val selectedDateCurrent = "$selectedYearCurrent-$selectedMonthCurrent-$selectedDayCurrent"
            val filteredListCurrent = analysisHistory.filter { it.date == selectedDateCurrent }
            val adapterCurrent = ArrayAdapter(this, android.R.layout.simple_list_item_1, filteredListCurrent.map { it.name })
            binding.listViewRight.adapter = adapterCurrent

            binding.listViewRight.setOnItemClickListener { _, _, position, _ ->
                val selectedData = filteredListCurrent[position]
                selectedCurrentDataId = selectedData.id
                Toast.makeText(this, "${selectedData.name} 선택됨!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        val visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.listViewLeft.visibility = visibility
    }

    private fun populateAnalysisHistory(data: List<Map<String, Any>>?) {
        analysisHistory.clear()
        data?.forEach { item ->
            val id = (item["analysis_idx"] as? Double)?.toInt()?.toString() ?: ""
            val name = formatTime(item["created_at"] as? String)
            val date = formatDate(item["created_at"] as? String)
            val scores = parseScores(item["analysis_result"] as? String)
            analysisHistory.add(AnalysisData(id, name, date, scores))
        }
    }

    private fun formatDate(dateString: String?): String {
        return try {
            val parts = dateString?.split("T")?.first()?.split("-") ?: return "날짜 없음"
            "${parts[0]}-${parts[1]}-${parts[2]}"
        } catch (e: Exception) {
            "날짜 없음"
        }
    }

    private fun formatTime(dateString: String?): String {
        return try {
            val parts = dateString?.split("T") ?: return "시간 없음"
            val timePart = parts.getOrNull(1)?.split(":") ?: return "시간 없음"
            "${timePart[0]}시 ${timePart[1]}분"
        } catch (e: Exception) {
            "시간 없음"
        }
    }

    private fun parseScores(jsonStr: String?): Map<String, Float> {
        return try {
            val jsonObject = org.json.JSONObject(jsonStr ?: "")
            val avgMeasurements = jsonObject.optJSONObject("average_measurements")
            avgMeasurements?.let { avg ->
                avg.keys().asSequence().associateWith { avg.optDouble(it, 0.0).toFloat() }
            } ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
