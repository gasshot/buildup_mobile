import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val numberOfPages = 5

    override fun getItemCount(): Int = numberOfPages

    override fun createFragment(position: Int): Fragment {
        return PageFragment(position + 1)
    }
}