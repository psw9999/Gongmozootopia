package com.psw9999.gongmozootopia.UI.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.psw9999.gongmozootopia.Adapter.MainViewPager
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.UI.Fragment.CommentFragment
import com.psw9999.gongmozootopia.UI.Fragment.StockListFragment
import com.psw9999.gongmozootopia.UI.Fragment.CalendarFragment
import com.psw9999.gongmozootopia.UI.Fragment.ConfigurationFragment
import com.psw9999.gongmozootopia.databinding.ActivityMainBinding
import com.psw9999.gongmozootopia.UI.Activity.LoadingActivity.Companion.STOCK_DATA

class MainActivity : AppCompatActivity() {
    val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    private lateinit var viewPager2 : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewPager()
        initBottomNavigation()
    }

    private fun initViewPager()
    {
        viewPager2 = binding.viewPager2Main
        val pagerAdapter = MainViewPager(this)
        pagerAdapter.fragmentList = listOf(
            StockListFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(STOCK_DATA,
                        intent.getParcelableArrayListExtra(STOCK_DATA))
                }
            }
            ,CalendarFragment(),ConfigurationFragment(),CommentFragment())
        viewPager2.adapter = pagerAdapter
        viewPager2.isUserInputEnabled = false
    }

    private fun initBottomNavigation() {
        //TODO : OnNavigationItemSelectedListener 왜 Deprecated?
        binding.bottomNavigationMain.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.notification -> {
                    setPageIndex(3)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.setting -> {
                    setPageIndex(2)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.calendar -> {
                    setPageIndex(1)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    setPageIndex(0)
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }

    private fun setPageIndex(index: Int) {
        viewPager2.currentItem = index
    }
}