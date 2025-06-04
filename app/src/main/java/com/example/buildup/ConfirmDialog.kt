import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.buildup.databinding.DialogConfirmBinding
import androidx.core.content.ContextCompat
import android.view.ViewGroup.LayoutParams
import com.example.buildup.R

class ConfirmDialog private constructor(
    private val title: String,
    private val content: String?,
    private val buttonText: String,
    private val id: Int,
    private val onConfirm: (Int) -> Unit
) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogConfirmBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(
            title: String,
            content: String?,
            buttonText: String,
            id: Int,
            onConfirm: (Int) -> Unit
        ): ConfirmDialog {
            return ConfirmDialog(title, content, buttonText, id, onConfirm)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogConfirmBinding.inflate(inflater, container, false)
        val view = binding.root

        // 다이얼로그 배경 설정
        dialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.border_dialog)
        )

        // 다이얼로그 UI 초기화
        initView()

        return view
    }

    private fun initView() {
        // 제목
        binding.dialogTitleTv.text = title

        // 내용
        if (content.isNullOrEmpty()) {
            binding.dialogDescTv.visibility = View.GONE
        } else {
            binding.dialogDescTv.text = content
        }

        // 확인 버튼 텍스트 설정
        binding.confirmButton.text = buttonText

        // 취소 버튼이 없는 경우 처리
        if (id == -1) {
            binding.cancelButton.visibility = View.GONE
        }

        // 버튼 클릭 리스너
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.confirmButton.setOnClickListener {
            onConfirm(id)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그 크기 조정
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
