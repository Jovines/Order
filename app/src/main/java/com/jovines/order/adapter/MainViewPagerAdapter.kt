package com.jovines.order.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jovines.order.ui.fragment.ItemListFragment
import com.jovines.order.ui.fragment.OrderFragment

/**
 * @author Jovines
 * @create 2020-04-15 5:10 PM
 *
 * 描述:
 *
 */
class MainViewPagerAdapter(val context: FragmentActivity) : FragmentStateAdapter(context) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            OrderFragment()
        } else {
            ItemListFragment()
        }
    }
}