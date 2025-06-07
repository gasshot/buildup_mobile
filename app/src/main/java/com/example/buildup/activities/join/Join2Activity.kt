package com.example.buildup.activities.join

import android.content.Intent
import android.graphics.Color // Color 클래스를 사용하기 위해 추가
import android.os.Bundle
import android.text.Editable // TextWatcher 사용을 위해 추가
import android.text.TextWatcher // TextWatcher 사용을 위해 추가
import android.util.Log
import android.util.Patterns // Patterns 클래스 사용을 위해 추가
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.LoginActivity
import com.example.buildup.R
import com.example.buildup.databinding.ActivityJoin2Binding

class Join2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityJoin2Binding
    private lateinit var selectedDomain: String // 선택된 도메인을 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 뷰 바인딩 객체 생성 및 setContentView에 적용
        // 기존의 setContentView(R.layout.activity_join2)는 제거되었습니다.
        binding = ActivityJoin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 화면으로 돌아가기 리스너
        binding.buttonReturnToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료

        }

        // 1. Spinner를 가져오기
        val spinner: Spinner = binding.spinnerEmailDomain

        // 2. Spinner 도메인 데이터 설정
        val domains = listOf("gmail.com", "yahoo.com", "naver.com", "daum.net", "hotmail.com")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, domains)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // 초기 선택 도메인 설정
        selectedDomain = domains[0] // 스피너 초기화 시 첫 번째 항목을 기본값으로 설정

        // 3. Spinner 선택 시 selectedDomain 변수 업데이트
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedDomain = domains[position] // 사용자가 선택한 도메인으로 업데이트
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 스피너에 아무것도 선택되지 않았을 때 호출되지만, 여기서는 특별한 처리 없음
            }
        }

        // 이메일 입력 필드 변경 리스너 (TextWatcher 추가)
        binding.editTextJoinEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 시 UI를 기본 상태로 되돌림
                binding.editTextJoinEmail.setBackgroundResource(R.drawable.edittext_box) // 기본 테두리
                binding.editTextJoinEmail.setTextColor(Color.BLACK) // 기본 텍스트 색상
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        // 이전 액티비티에서 받은 데이터
        val id = intent.getStringExtra("user_id")
        val pw1 = intent.getStringExtra("user_pw")

        // "다음" 버튼 클릭 리스너
        binding.buttonNextTo3.setOnClickListener {
            Log.d("click", "click")
            // 사용자가 입력한 이메일 접두사의 양쪽 공백을 제거하고, 선택된 도메인을 합쳐 완전한 이메일 주소를 만듭니다.
            val enteredEmailPrefix = binding.editTextJoinEmail.text.toString().trim()
            val fullEmail = enteredEmailPrefix + "@" + selectedDomain // @ 기호를 포함하여 이메일 완성
            Log.d("fullEmail", fullEmail)
            
            // 이메일 입력 필드가 비어있는지 확인
            if (enteredEmailPrefix.isEmpty()) {
                Toast.makeText(
                    this@Join2Activity,
                    getString(R.string.error_all_fields_required), // "모든 필드를 입력해주세요" 메시지 활용
                    Toast.LENGTH_SHORT
                ).show()
                binding.editTextJoinEmail.setBackgroundResource(R.drawable.edittext_error_box)
                binding.editTextJoinEmail.setTextColor(Color.RED)
                return@setOnClickListener
            }

            // 이메일 유효성 검사 활성화: Patterns.EMAIL_ADDRESS를 사용하여 올바른 이메일 형식인지 확인
            if (!Patterns.EMAIL_ADDRESS.matcher(fullEmail).matches()) {
                Toast.makeText(
                    this@Join2Activity,
                    getString(R.string.error_invalid_email), // res/values/strings.xml에 정의된 문자열
                    Toast.LENGTH_SHORT
                ).show()
                // 유효성 검사 실패 시 UI 오류 표시
                binding.editTextJoinEmail.setBackgroundResource(R.drawable.edittext_error_box) // 오류 테두리
                binding.editTextJoinEmail.setTextColor(Color.RED) // 텍스트 색상 변경
                return@setOnClickListener // 유효성 검사 실패 시 다음 코드 실행 방지
            } else {
                // 유효성 검사 통과 시 UI 초기화 (이전에 오류가 있었을 경우)
                binding.editTextJoinEmail.setBackgroundResource(R.drawable.edittext_box) // 기본 테두리
                binding.editTextJoinEmail.setTextColor(Color.BLACK) // 기본 텍스트 색상
            }

            // 모든 유효성 검사에 통과하면 다음 액티비티로 데이터를 전달하고 전환합니다.
            val intent = Intent(this, Join3Activity::class.java)
            intent.putExtra("user_id", id)
            intent.putExtra("user_pw", pw1)
            intent.putExtra("user_email", fullEmail) // 구성된 전체 이메일 주소 전달
            startActivity(intent)
        }
    }
}