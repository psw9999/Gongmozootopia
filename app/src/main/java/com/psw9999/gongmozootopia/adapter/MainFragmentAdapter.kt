package com.psw9999.gongmozootopia.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainFragmentAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {
    var mainFragmentList = listOf<Fragment>()

    override fun getItemCount(): Int {
        return mainFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mainFragmentList[position]
    }

}