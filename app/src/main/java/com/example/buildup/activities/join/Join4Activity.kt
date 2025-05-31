package com.example.buildup.activities.join

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.R
import com.example.buildup.databinding.ActivityJoin4Binding
import java.util.Calendar

class Join4Activity : AppCompatActivity() {
    private lateinit var binding: ActivityJoin4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 객체 생성 및 setContentView에 적용
        binding = ActivityJoin4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전 액티비티에서 받은 데이터
        val id = intent.getStringExtra("user_id")
        val pw1 = intent.getStringExtra("user_pw")
        val email = intent.getStringExtra("user_email")
        val nickname = intent.getStringExtra("user_nickname")

        // 성별 선택
        binding.buttonNextTofinal.setOnClickListener {
            val selectedSex = when (binding.radioGroupGender.checkedRadioButtonId) {
                R.id.radioMale -> "남성"
                R.id.radioFemale -> "여성"
                else -> {
                    Toast.makeText(this@Join4Activity, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val birthdate = binding.editTextBirthdate.text.toString()
            if (birthdate.isEmpty()) {
                Toast.makeText(this, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 다음 액티비티로 이동
            val intent = Intent(this, JoinActivity::class.java) // 마지막 액티비티로 변경
            intent.putExtra("user_id", id)
            intent.putExtra("user_pw", pw1)
            intent.putExtra("user_email", email)
            intent.putExtra("user_nickname", nickname)
            intent.putExtra("user_sex", selectedSex)
            intent.putExtra("user_birthdate", birthdate)
            startActivity(intent)
        }

        // 생일 선택
        binding.editTextBirthdate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.editTextBirthdate.setText(date)
            }, year, month, day)

            datePickerDialog.show()
        }
    }
}
