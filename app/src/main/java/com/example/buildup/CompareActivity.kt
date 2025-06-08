package com.example.buildup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.api.ApiManager
import com.example.buildup.data.AnalysisData
import com.example.buildup.databinding.ActivityCompareBinding
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class CompareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompareBinding

    private val analysisHistory = mutableListOf<AnalysisData>()
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
                        setupSpinners()
                    } else {
                        Toast.makeText(this, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "사용자 ID가 없습니다. 로그인 상태를 확인하세요.", Toast.LENGTH_SHORT).show()
        }

        binding.compareButton.setOnClickListener {
            if (selectedPastDataId != null && selectedCurrentDataId != null) {
                Toast.makeText(this, "차트 보기 클릭됨!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "비교할 데이터를 선택하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonReturnToMain2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        val visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.listViewLeft.visibility = visibility
        binding.listViewRight.visibility = visibility
        binding.compareButton.isEnabled = !isLoading
    }

    private fun populateAnalysisHistory(data: List<Map<String, Any>>?) {
        analysisHistory.clear()
        data?.forEach { item ->
            val id = (item["analysis_idx"] as? Double)?.toInt()?.toString() ?: ""
            val name = formatTime(item["created_at"] as? String) // 시:분:초를 표시
            val date = formatDate(item["created_at"] as? String) // 연월일
            val scores = parseScores(item["analysis_result"] as? String)
            analysisHistory.add(AnalysisData(id, name, date, scores))
        }
    }

    private fun formatDate(dateString: String?): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(dateString ?: "")
            val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            "날짜 없음"
        }
    }

    private fun formatTime(dateString: String?): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(dateString ?: "")
            val outputFormat = SimpleDateFormat("a h시 m분 s초", Locale.getDefault()) // a: 오전/오후, h: 12시간제 시
            outputFormat.format(date)
        } catch (e: Exception) {
            "시간 없음"
        }
    }

    private fun parseScores(jsonStr: String?): Map<String, Float> {
        return try {
            val jsonObject = JSONObject(jsonStr ?: "")
            val avgMeasurements = jsonObject.optJSONObject("average_measurements")
            avgMeasurements?.let { avg ->
                avg.keys().asSequence().associateWith { avg.optDouble(it, 0.0).toFloat() }
            } ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun setupSpinners() {
        val dateList = analysisHistory.map { it.date }.distinct()

        setupSpinner(binding.spinnerLeft, dateList) { selectedDate ->
            updateListView(binding.listViewLeft, selectedDate) { id -> selectedPastDataId = id }
        }

        setupSpinner(binding.spinnerRight, dateList) { selectedDate ->
            updateListView(binding.listViewRight, selectedDate) { id -> selectedCurrentDataId = id }
        }
    }

    private fun setupSpinner(spinner: Spinner, data: List<String>, onItemSelected: (String) -> Unit) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = data[position]
                onItemSelected(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 선택이 해제된 경우 처리할 로직
            }
        }
    }

    private fun updateListView(listView: android.widget.ListView, filterDate: String, onItemClick: (String) -> Unit) {
        val filteredList = analysisHistory.filter { it.date == filterDate }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, filteredList.map { it.name }) // 시간만 표시
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedData = filteredList[position]
            onItemClick(selectedData.id)
            Toast.makeText(this, "${selectedData.name} 선택됨!", Toast.LENGTH_SHORT).show()
        }
    }
}


