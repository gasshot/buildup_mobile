package com.example.buildup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.databinding.ActivityMainBinding


// 로그아웃 경고창
import android.app.AlertDialog
import android.content.DialogInterface

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE) // this.MODE_PRIVATE 또는 MODE_PRIVATE
        // 전달받은 데이터 추출
        val savedUserId = sharedPreferences.getString("userId", null)
        val savedUserName = sharedPreferences.getString("userName", null)
        val savedUserRole = sharedPreferences.getString("userRole", null)

        // UI에 데이터 표시
        binding.textViewUserName.text = "사용자 이름: $savedUserName"
        binding.textViewUserRole.text = "역할: $savedUserRole"
        binding.textViewUserId.text = "ID: $savedUserId"

        binding.buttonLogout.setOnClickListener{
            // 로그아웃
            // 다음 화면으로 이동(메인화면)

            val builder = AlertDialog.Builder(this)
            builder.setTitle("경고")
                .setMessage("로그아웃할까요?")
                .setPositiveButton("예") { dialog, which ->
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("아니오") { dialog, which ->
                    dialog.dismiss()
                }
                .show()


        }
        binding.BtnPersonal.setOnClickListener {

            // 클릭 이벤트가 발생했을 때 실행될 코드 블록입니다.
            // Toast 메시지를 생성하고 화면에 보여줍니다.
            Toast.makeText(this, "버튼이 클릭되었습니다!", Toast.LENGTH_SHORT).show()

            // 만약 다른 버튼에 대해서도 클릭 이벤트를 처리하고 싶다면,
            // 위와 같은 방식으로 해당 버튼의 ID를 찾아 클릭 리스너를 설정하면 됩니다.
            // 예시:
            // val anotherButton: Button = findViewById(R.id.anotherButtonId)
            // anotherButton.setOnClickListener {
            //     Toast.makeText(this, "다른 버튼이 클릭되었습니다!", Toast.LENGTH_LONG).show()
            // }
        }

    }

}