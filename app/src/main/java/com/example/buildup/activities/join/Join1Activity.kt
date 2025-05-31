package com.example.buildup.activities.join

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.data.CheckIDRequest
import com.example.buildup.data.CheckIDResponse
import com.example.buildup.R
import com.example.buildup.api.RetrofitClient
import com.example.buildup.databinding.ActivityJoin1Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Join1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityJoin1Binding
    private var isIdAvailable: Boolean = false // ID 사용 가능 여부
    private var isIdChecked: Boolean = false // ID 중복 확인을 한 번이라도 했는지 여부

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJoin1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // ID 입력 필드 변경 리스너
        binding.editTextJoinId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // ID가 변경되면, ID 사용 가능 상태를 초기화하고, 중복 확인 여부도 초기화
                isIdAvailable = false
                isIdChecked = false
                // UI를 기본 상태로 되돌림
                binding.editTextJoinId.setTextColor(Color.BLACK)
                binding.editTextJoinId.setBackgroundResource(R.drawable.edittext_box)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 비밀번호 확인 입력 필드 변경 리스너
        binding.editTextJoinPwConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 비밀번호 확인이 변경되면, UI를 기본 상태로 되돌림
                binding.editTextJoinPwConfirm.setTextColor(Color.BLACK)
                binding.editTextJoinPwConfirm.setBackgroundResource(R.drawable.edittext_box)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // ID 중복 확인 버튼 (이런 버튼이 따로 있는 것이 사용자 경험에 좋습니다)
        // 만약 '다음' 버튼 하나로 모든 걸 처리해야 한다면, 이 로직을 '다음' 버튼 내부에 통합해야 합니다.
        // 현재 코드에서는 '다음' 버튼 클릭 시 ID 중복 확인을 시도하는 방식이므로, 해당 로직을 통합합니다.

        binding.buttonNextTo2.setOnClickListener {
            val id = binding.editTextJoinId.text.toString().trim()
            val pw1 = binding.editTextJoinPw.text.toString().trim()
            val pw2 = binding.editTextJoinPwConfirm.text.toString().trim()

            // 1. ID 입력 여부 확인
            if (id.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_id), Toast.LENGTH_SHORT).show()
                binding.editTextJoinId.setBackgroundResource(R.drawable.edittext_error_box)
                return@setOnClickListener
            }

            // 2. 비밀번호 입력 여부 확인
            if (pw1.isEmpty() || pw2.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_password), Toast.LENGTH_SHORT).show()
                if (pw1.isEmpty()) binding.editTextJoinPw.setBackgroundResource(R.drawable.edittext_error_box)
                if (pw2.isEmpty()) binding.editTextJoinPwConfirm.setBackgroundResource(R.drawable.edittext_error_box)
                return@setOnClickListener
            }

            // 3. 비밀번호 강도 검사 활성화
            // TODO: 비밀번호 강도 기준을 string.xml에 정의하거나 더 복잡한 정규식으로 강화할 수 있습니다.
//            if (pw1.length < 8 || !pw1.matches(".*[a-zA-Z].*".toRegex()) || !pw1.matches(".*\\d.*".toRegex()) || !pw1.matches(".*[!@#\$%^&*()].*".toRegex())) {
//                Toast.makeText(this, getString(R.string.error_password_strength), Toast.LENGTH_SHORT).show()
//                binding.editTextJoinPw.setBackgroundResource(R.drawable.edittext_error_box)
//                return@setOnClickListener
//            }

            // 4. 비밀번호 일치 여부 확인
            if (pw1 != pw2) {
                Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
                binding.editTextJoinPwConfirm.setTextColor(Color.RED)
                binding.editTextJoinPwConfirm.setBackgroundResource(R.drawable.edittext_error_box)
                return@setOnClickListener
            }

            // --- ID 중복 확인 로직 ---
            // ID 중복 확인이 아직 되지 않았거나, ID가 변경되었는데 재확인을 안 한 경우
            // 여기서 서버 통신을 시작합니다.
            if (!isIdChecked || !isIdAvailable) {
                val checkIDRequest = CheckIDRequest(id)
                Log.d("Join1Activity", "ID 중복 확인 요청: $id")

                RetrofitClient.instance.checkID(checkIDRequest).enqueue(object : Callback<CheckIDResponse> {
                    override fun onResponse(call: Call<CheckIDResponse>, response: Response<CheckIDResponse>) {
                        isIdChecked = true // 중복 확인 완료
                        if (response.isSuccessful) {
                            val checkIDResponse = response.body()
                            if (checkIDResponse?.success == true) {
                                if (checkIDResponse.possible == false) {
                                    // ID 사용 불가능
                                    isIdAvailable = false
                                    binding.editTextJoinId.setTextColor(Color.RED)
                                    binding.editTextJoinId.setBackgroundResource(R.drawable.edittext_error_box)
                                    Toast.makeText(this@Join1Activity, getString(R.string.error_id_already_taken), Toast.LENGTH_SHORT).show()
                                } else {
                                    // ID 사용 가능
                                    isIdAvailable = true
                                    binding.editTextJoinId.setTextColor(Color.BLACK)
                                    binding.editTextJoinId.setBackgroundResource(R.drawable.edittext_box)
                                    // 중요: ID 사용 가능하면 다시 "다음" 버튼 클릭 로직을 호출 (또는 바로 진행)
                                    // 이 방식은 재귀 호출이 될 수 있으므로, 아래처럼 직접 다음 단계로 진행하는 것이 더 좋습니다.
                                    proceedToNextStep(id, pw1)
                                }
                            } else {
                                // 서버 응답 실패 (success: false)
                                isIdAvailable = false // 통신 실패 시 ID 사용 불가로 처리
                                Toast.makeText(this@Join1Activity, getString(R.string.error_server_response), Toast.LENGTH_SHORT).show()
                                binding.editTextJoinId.setBackgroundResource(R.drawable.edittext_error_box)
                            }
                        } else {
                            // HTTP 응답 코드 실패 (e.g., 404, 500)
                            isIdAvailable = false // 통신 실패 시 ID 사용 불가로 처리
                            Log.e("JoinActivity", "Server error: ${response.code()}, ${response.errorBody()?.string()}")
                            Toast.makeText(this@Join1Activity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                            binding.editTextJoinId.setBackgroundResource(R.drawable.edittext_error_box)
                        }
                    }

                    override fun onFailure(call: Call<CheckIDResponse>, t: Throwable) {
                        isIdAvailable = false // 네트워크 오류 시 ID 사용 불가로 처리
                        isIdChecked = false // 다시 확인하도록 초기화
                        Log.e("JoinActivity", "Network error on ID check: ${t.message}", t)
                        Toast.makeText(this@Join1Activity, getString(R.string.error_network_connection), Toast.LENGTH_SHORT).show()
                        binding.editTextJoinId.setBackgroundResource(R.drawable.edittext_error_box)
                    }
                })
            } else if (isIdAvailable) {
                // ID 중복 확인이 완료되었고 사용 가능하다면 바로 다음 단계로 진행
                proceedToNextStep(id, pw1)
            } else {
                // ID 중복 확인은 했으나 사용 불가능한 상태라면
                Toast.makeText(this@Join1Activity, getString(R.string.error_id_already_taken), Toast.LENGTH_SHORT).show()
                binding.editTextJoinId.setBackgroundResource(R.drawable.edittext_error_box)
            }
        }
    } // end of onCreate

    // 다음 액티비티로 넘어가는 로직을 별도 함수로 분리
    private fun proceedToNextStep(id: String, pw1: String) {
        val intent = Intent(this, Join2Activity::class.java)
        intent.putExtra("user_id", id)
        intent.putExtra("user_pw", pw1)
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
}