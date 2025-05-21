import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.buildup.R

class PageFragment(private val pageNumber: Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutId = when (pageNumber) {
//            1 -> R.layout.fragment_page_1
//            2 -> R.layout.fragment_page_2
//            3 -> R.layout.fragment_page_3
//            4 -> R.layout.fragment_page_4
//            5 -> R.layout.fragment_page_5
            else -> R.layout.fragment_page // 기본 레이아웃
        }
        val view = inflater.inflate(layoutId, container, false)

        val pageNumberTextView = view.findViewById<TextView>(R.id.pageNumberTextView)
        Log.d("PageFragment", "Page $pageNumber, TextView: $pageNumberTextView")
        pageNumberTextView?.text = "페이지 $pageNumber"

        val pageButton = view.findViewById<Button>(R.id.pageButton)
        pageButton?.text = "$pageNumber 번째 페이지 버튼"
        pageButton?.setOnClickListener {
            Toast.makeText(requireContext(), "$pageNumber 번째 페이지 버튼 클릭!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}