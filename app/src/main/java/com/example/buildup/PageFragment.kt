import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.buildup.R

class PageFragment : Fragment() {
    private var pageNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = arguments?.getInt(ARG_PAGE_NUMBER) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_page, container, false)

        // ImageView 설정
        val pageImageView = view.findViewById<ImageView>(R.id.pageImageView)
        val imageResId = when (pageNumber) {
            1 -> R.drawable.main_page_01  // 페이지 1의 이미지
            2 -> R.drawable.skinthera3  // 페이지 2의 이미지
            else -> R.drawable.skinthera2 // 기본 이미지
        }
        pageImageView?.setImageResource(imageResId)

        val pageButton = view.findViewById<Button>(R.id.pageButton)
        pageButton?.text = when (pageNumber) {
            1 -> "피부분석"
            2 -> "비교하기"
            else -> "$pageNumber 번째 페이지 버튼"
        }
        pageButton?.setOnClickListener {
            when (pageNumber) {
                1 -> Toast.makeText(requireContext(), "피부분석 버튼 클릭!", Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(requireContext(), "비교하기 버튼 클릭!", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(requireContext(), "기본 페이지 버튼 클릭!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    companion object {
        private const val ARG_PAGE_NUMBER = "page_number"

        fun newInstance(pageNumber: Int): PageFragment {
            val fragment = PageFragment()
            val args = Bundle()
            args.putInt(ARG_PAGE_NUMBER, pageNumber)
            fragment.arguments = args
            return fragment
        }
    }
}

