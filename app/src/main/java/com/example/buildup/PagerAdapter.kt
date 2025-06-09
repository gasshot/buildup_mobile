import androidx.fragment.app.Fragment
import com.example.buildup.PageFragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val numberOfPages = 2

    override fun getItemCount(): Int = numberOfPages

    override fun createFragment(position: Int): Fragment {
        return PageFragment.newInstance(position + 1)
    }
}