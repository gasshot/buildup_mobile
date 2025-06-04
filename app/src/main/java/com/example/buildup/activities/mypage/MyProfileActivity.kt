package com.example.buildup.activities.mypage

import ConfirmDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.buildup.LoginActivity
import com.example.buildup.MypageActivity
import com.example.buildup.databinding.ActivityMyProfileBinding
import androidx.core.content.edit
import com.example.buildup.DialogUpdateNickname
import com.example.buildup.DialogUpdatePW
import com.example.buildup.api.ApiManager
import com.example.buildup.data.UpdatePWRequest
import com.example.buildup.data.UpdateNicknameRequest

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences에서 사용자 정보 불러오기
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "N/A")
        val userNickname = sharedPreferences.getString("userName", "N/A")
        val userDob = sharedPreferences.getString("userBirthdate", "N/A")
        val userGender = sharedPreferences.getString("userSex", "N/A")
        val userEmail = sharedPreferences.getString("userEmail", "N/A")

        // 데이터를 TextView에 설정
        binding.tvUserId.text = userId
        binding.tvNickname.text = userNickname
        binding.tvUserDob.text = userDob
        binding.tvUserGender.text = userGender
        binding.tvUserEmail.text = userEmail

        binding.btnBack.setOnClickListener{
            navigateToMyPageActivity()
        }
        
        binding.btnChangeNickname.setOnClickListener{
            // 닉네임 변경 시 다이얼로그 뜨게하기
            updateNickname()
        }
        
        binding.btnChangePassword.setOnClickListener{
            // 비밀번호 변경 시 다이얼로그 뜨게하기
            updatePW()
        }


        binding.btnUnjoin.setOnClickListener{
            // 회원 탈퇴
            quit()
        }
    }

    private fun updateNickname() {
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "N/A") ?: "N/A"
        val currentNickname = sharedPreferences.getString("nickname", "기본닉네임") ?: "기본닉네임"

        // 다이얼로그 생성 및 표시
        val dialog = DialogUpdateNickname.newInstance(
            currentNickname = currentNickname,
            onNicknameChanged = { isSuccess, newNickname ->
                if (isSuccess && newNickname != null) {
                    val updateNicknameRequest = UpdateNicknameRequest(userId, newNickname)

                    // 서버 호출
                    ApiManager.updateNickname(updateNicknameRequest) { success ->
                        if (success) {
                            Toast.makeText(this, "닉네임이 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()

                            // SharedPreferences 업데이트
                            sharedPreferences.edit {
                                putString("nickname", newNickname)
                            }

                            // UI 업데이트
                            binding?.tvNickname?.text = newNickname
                        } else {
                            Toast.makeText(this, "닉네임 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "닉네임 변경이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // 다이얼로그 표시
        dialog.show(supportFragmentManager, "UpdateNicknameDialog")
    }

    private fun updatePW() {

        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "N/A") ?: "N/A"

        // 다이얼로그 생성
        val dialog = DialogUpdatePW.newInstance { isSuccess, currentPassword, newPassword ->
            if (isSuccess && currentPassword != null && newPassword != null) {
                val updatePWRequest = UpdatePWRequest(userId, currentPassword, newPassword)

                ApiManager.updatePassword(updatePWRequest) { success ->
                    if (success) {
                        Toast.makeText(this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false

        // 다이얼로그 표시
        supportFragmentManager.let {
            dialog.show(it, "DialogUpdatePW")
        }
    }


    private fun quit() {
        // 다이얼로그 정보 설정
        val title = "정말 계정을 삭제하시겠어요?"
        val content = "지금까지의 정보가 모두 사라집니다."

        // 다이얼로그 생성
        val dialog = ConfirmDialog.newInstance(
            title,
            content,
            "확인",
            1
        ) { id ->
            // 확인 버튼 클릭 시 처리
            if (id == 1) {
                handleAccountDeletion()
            }
        }

        // 알림창이 띄워져있는 동안 배경 클릭 막기
        dialog.isCancelable = false

        // 다이얼로그 표시
        supportFragmentManager.let {
            dialog.show(it, "ConfirmDialog")
        }
    }

    private fun handleAccountDeletion() {
        // 계정 삭제 처리 로직(예정)
        // 예: API 호출, 로컬 데이터 삭제 등
        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            // 모든 데이터 삭제
            clear()
        }

        val intent = Intent(this@MyProfileActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMyPageActivity() {
        val intent = Intent(this, MypageActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료 -> 마이페이지로 이동
    }
}

