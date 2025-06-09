package com.example.buildup

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.buildup.MainActivity
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
        val pageButton = view.findViewById<Button>(R.id.pageButton)

        // 페이지별 콘텐츠 데이터 클래스
        data class PageContent(val imageResId: Int, val buttonText: String, val toastMessage: String)

        // 페이지별 콘텐츠 가져오기
        fun getPageContent(pageNumber: Int): PageContent {
            return when (pageNumber) {
                1 -> PageContent(
                    R.drawable.main_page_01,
                    "피부분석",
                    "피부분석 버튼 클릭!"
                )
                2 -> PageContent(
                    R.drawable.main_page_02,
                    "비교하기",
                    "비교하기 버튼 클릭!"
                )
                else -> PageContent(
                    R.drawable.skinthera2,
                    "$pageNumber 번째 페이지 버튼",
                    "기본 페이지 버튼 클릭!"
                )
            }
        }

        val content = getPageContent(pageNumber)
        pageImageView.setImageResource(content.imageResId)
        pageButton.text = content.buttonText


        // 버튼 클릭 리스너
        pageButton.setOnClickListener {
            when (pageNumber) {
                1 -> {
                    // 페이지 1 버튼 클릭 이벤트
                    Toast.makeText(requireContext(), "피부분석 버튼 클릭!", Toast.LENGTH_SHORT).show()
                    // 여기에 원하는 동작 추가
                    val intent = Intent(requireContext(), AnalysisActivity::class.java)
                    startActivity(intent)
                }
                2 -> {
                    // 페이지 2 버튼 클릭 이벤트
                    Toast.makeText(requireContext(), "비교하기 버튼 클릭!", Toast.LENGTH_SHORT).show()
                    // 여기에 원하는 동작 추가
                    val intent = Intent(requireContext(), CompareActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    Toast.makeText(requireContext(), "기본 페이지 버튼 클릭!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 버튼 위치 설정 (FrameLayout.LayoutParams 필요)
        val layoutParams = pageButton.layoutParams as FrameLayout.LayoutParams

        when (pageNumber) {
            1 -> {
                layoutParams.gravity = Gravity.BOTTOM or Gravity.START
                layoutParams.marginStart = 220
                layoutParams.bottomMargin = 100
            }
            2 -> {
                layoutParams.gravity = Gravity.END
                layoutParams.marginEnd = 100
                layoutParams.topMargin = 800
            }
            else -> {
                layoutParams.gravity = Gravity.CENTER
                // 필요하면 margin 초기화
                layoutParams.marginStart = 0
                layoutParams.marginEnd = 0
                layoutParams.topMargin = 0
                layoutParams.bottomMargin = 0
            }
        }
        pageButton.layoutParams = layoutParams

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
