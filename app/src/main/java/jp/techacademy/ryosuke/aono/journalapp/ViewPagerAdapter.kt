package jp.techacademy.ryosuke.aono.journalapp

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

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