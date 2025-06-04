package com.example.buildup

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.buildup.databinding.DialogNicknameUpdateBinding

class DialogUpdateNickname : DialogFragment() {

    private var _binding: DialogNicknameUpdateBinding? = null
    private val binding get() = _binding!!

    private var onNicknameChanged: ((Boolean, String?) -> Unit)? = null

    companion object {
        private const val ARG_CURRENT_NICKNAME = "current_nickname"

        /**
         * DialogUpdateNickname 인스턴스를 생성하는 메서드
         */
        fun newInstance(
            currentNickname: String,
            onNicknameChanged: (Boolean, String?) -> Unit
        ): DialogUpdateNickname {
            return DialogUpdateNickname().apply {
                arguments = Bundle().apply {
                    putString(ARG_CURRENT_NICKNAME, currentNickname)
                }
                this.onNicknameChanged = onNicknameChanged
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogNicknameUpdateBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        setupListeners()

        return builder.create()
    }

    private fun setupListeners() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.confirmButton.setOnClickListener {
            val newNickname = binding.inputNewNickname.text.toString().trim()
            val currentNickname = arguments?.getString(ARG_CURRENT_NICKNAME)

            if (currentNickname != null && validateInputs(currentNickname, newNickname)) {
                onNicknameChanged?.invoke(true, newNickname)
                dismiss()
            }
        }
    }

    private fun validateInputs(currentNickname: String, newNickname: String): Boolean {
        if (newNickname.isEmpty()) {
            showError(binding.inputNewNickname, "새로운 닉네임을 입력하세요.")
            return false
        }

        if (currentNickname == newNickname) {
            showError(binding.inputNewNickname, "새 닉네임과 현재 닉네임이 동일합니다.")
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
