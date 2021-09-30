package jp.techacademy.ryosuke.aono.journalapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.common.util.CollectionUtils.listOf

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity)  {
    val titleIds = listOf(R.string.tab_title_timeline,R.string.tab_title_myList)

    private val fragments = listOf(TimeLineFragment(),MyJournalFragment())
    override fun getItemCount(): Int {
       return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}