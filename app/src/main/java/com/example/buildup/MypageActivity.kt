package com.example.buildup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.activities.mypage.MyProfileActivity
import com.example.buildup.api.RetrofitClient
import com.example.buildup.data.CheckPWRequest
import com.example.buildup.data.CheckPWResponse
import com.example.buildup.databinding.ActivityMypageBinding

// Retrofit 관련 클래스들 (예시, 실제 파일명과 매개변수 확인 필요)
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: 실제 RetrofitClient, CheckPWRequest, CheckPWResponse, LoginResponse 클래스들이 정의된 곳의 import 문을 추가하세요.
// 예: import com.example.buildup.network.RetrofitClient
// 예: import com.example.buildup.data.CheckPWRequest
// 예: import com.example.buildup.data.CheckPWResponse
// 예: import com.example.buildup.data.LoginResponse // onFailure에서 사용되므로 필요할 수 있습니다.


class MypageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private var currentUserId: String? = null // 현재 로그인된 사용자의 ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences에서 사용자 ID 가져오기
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getString("userId", null)

        if (currentUserId == null) {
            Toast.makeText(this, "사용자 정보를 찾을 수 없습니다. 로그인이 필요합니다.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.announceBtn.setOnClickListener {
            // 공지사항 버튼 클릭 시 실행될 코드
        }

        binding.myProfileBtn.setOnClickListener {
            showPasswordConfirmationDialog()
        }

        binding.analysisBtn.setOnClickListener {
            Toast.makeText(this@MypageActivity, "피부분석 클릭", Toast.LENGTH_SHORT).show()
        }

        binding.csBtn.setOnClickListener {
            // 고객센터 버튼 클릭 시 실행될 코드
        }
    }

    /**
     * 현재 비밀번호 확인 다이얼로그를 표시합니다.
     */
    private fun showPasswordConfirmationDialog() {
        val userId = currentUserId ?: run {
            Toast.makeText(this, "사용자 정보를 가져올 수 없어 비밀번호 확인을 진행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_password_input, null)

        val editTextPassword = dialogView.findViewById<EditText>(R.id.editTextPassword)
        val buttonConfirm = dialogView.findViewById<Button>(R.id.buttonConfirm)
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)

        builder.setView(dialogView)
            .setTitle("비밀번호 확인")

        val dialog = builder.create()

        buttonConfirm.setOnClickListener {
            val enteredPassword = editTextPassword.text.toString()

            // 입력된 비밀번호가 비어있는지 확인
            if (enteredPassword.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 여기서 함수 종료
            }

            // --- 여기에서 서버 비밀번호 확인 함수 호출 ---
            val checkRequest = CheckPWRequest(userId, enteredPassword) // 사용자 ID와 입력된 비밀번호로 요청 객체 생성
            performCheckPW(checkRequest, dialog) // 다이얼로그 객체를 넘겨주어 성공 시 닫을 수 있도록 함
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "비밀번호 확인을 취소했습니다.", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    /**
     * 서버에 비밀번호 확인을 요청하는 함수
     * @param checkRequest 비밀번호 확인 요청 데이터를 담은 객체
     * @param dialog 현재 표시 중인 AlertDialog 인스턴스 (비밀번호 확인 성공 시 닫기 위함)
     */
    private fun performCheckPW(checkRequest: CheckPWRequest, dialog: AlertDialog) {
        RetrofitClient.instance.checkPW(checkRequest)
            .enqueue(object : Callback<CheckPWResponse> { // CheckPWResponce 오타를 CheckPWResponse로 수정
                // MypageActivity.kt의 performCheckPW 함수 내부
                override fun onResponse(call: Call<CheckPWResponse>, response: Response<CheckPWResponse>) {
                    if (response.isSuccessful) {
                        val checkPWResponse = response.body()
                        if (checkPWResponse?.success == true) {
                            Toast.makeText(this@MypageActivity, "비밀번호 확인 완료!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            navigateToMyProfileActivity()
                        } else {
                            // success가 false인 경우 (비밀번호 불일치 등)
                            // 서버에서 보낸 메시지가 있다면 사용하고, 없으면 R.string.error_password_mismatch 사용
                            val message = getString(R.string.error_password_mismatch)
                            Toast.makeText(this@MypageActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        // error_join_request_failed를 활용하여 HTTP 코드와 오류 메시지를 표시
                        val errorMessage = getString(R.string.error_join_request_failed, response.code(), errorBody ?: "알 수 없는 오류")
                        Toast.makeText(this@MypageActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CheckPWResponse>, t: Throwable) {
                    Toast.makeText(this@MypageActivity, getString(R.string.error_network_connection), Toast.LENGTH_SHORT).show() // 네트워크 연결 오류 메시지
                }
            })
    }

    /**
     * 비밀번호 확인 후 내 정보 화면으로 이동합니다.
     */
    private fun navigateToMyProfileActivity() {
        val intent = Intent(this, MyProfileActivity::class.java) // TODO: 실제 회원 정보 액티비티 클래스로 변경하세요.
        startActivity(intent)
        // Toast.makeText(this, "실제 내 정보 화면으로 이동하는 코드는 주석 처리되어 있습니다.", Toast.LENGTH_LONG).show() // 이제 필요 없음
    }
}