package com.example.buildup

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.buildup.databinding.DialogPasswordUpdateBinding

class DialogUpdatePW : DialogFragment() {

    private var _binding: DialogPasswordUpdateBinding? = null
    private val binding get() = _binding!!
    private var onPasswordChanged: ((Boolean, String?, String?) -> Unit)? = null // 데이터 전달용 콜백 수정

    companion object {
        fun newInstance(onPasswordChanged: (Boolean, String?, String?) -> Unit): DialogUpdatePW {
            return DialogUpdatePW().apply {
                this.onPasswordChanged = onPasswordChanged
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPasswordUpdateBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        setupListeners()
        return builder.create()
    }

    private fun setupListeners() {
        binding.cancelButton.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }

        binding.confirmButton.setOnClickListener {
            val currentPassword = binding.inputCurrentPassword.text.toString().trim()
            val newPassword = binding.inputNewPassword.text.toString().trim()
            val confirmPassword = binding.inputConfirmPassword.text.toString().trim()

            if (validateInputs(currentPassword, newPassword, confirmPassword)) {
                onPasswordChanged?.invoke(true, currentPassword, newPassword) // 콜백으로 데이터 전달
                dismiss() // 다이얼로그 닫기
            }
        }
    }

    private fun validateInputs(currentPassword: String, newPassword: String, confirmPassword: String): Boolean {
        if (currentPassword.isEmpty()) {
            showError(binding.inputCurrentPassword, "현재 비밀번호를 입력하세요.")
            return false
        }

        if (newPassword.isEmpty()) {
            showError(binding.inputNewPassword, "새로운 비밀번호를 입력하세요.")
            return false
        }

        if (confirmPassword.isEmpty()) {
            showError(binding.inputConfirmPassword, "비밀번호 확인을 입력하세요.")
            return false
        }

        if (newPassword != confirmPassword) {
            showError(binding.inputConfirmPassword, "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.")
            return false
        }

        if (currentPassword == newPassword) {
            showError(binding.inputNewPassword, "새 비밀번호와 현재 비밀번호가 동일합니다.")
            return false
        }

        return true
    }

    private fun showError(inputField: EditText, errorMessage: String) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        inputField.setBackgroundResource(R.drawable.edittext_error_box)
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그 크기 조정
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

